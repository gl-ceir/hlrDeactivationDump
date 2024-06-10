package com.gl.eirs.hlrdeactivation.service.impl;

import com.gl.eirs.hlrdeactivation.alert.AlertService;
import com.gl.eirs.hlrdeactivation.builder.ModulesAuditTrailBuilder;
import com.gl.eirs.hlrdeactivation.builder.ExceptionListHisBuilder;
import com.gl.eirs.hlrdeactivation.config.AppConfig;
import com.gl.eirs.hlrdeactivation.config.AppDbConfig;
import com.gl.eirs.hlrdeactivation.dto.FileDto;
import com.gl.eirs.hlrdeactivation.entity.app.*;

import com.gl.eirs.hlrdeactivation.messages.FailureMsg;
import com.gl.eirs.hlrdeactivation.repository.app.*;
import com.gl.eirs.hlrdeactivation.repository.aud.ModulesAuditTrailRepository;
import com.gl.eirs.hlrdeactivation.service.interfce.IFileService;

/*import com.imsi_retriever.IMSI_RETRIEVER;*/
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*import static com.imsi_retriever.IMSI_RETRIEVER.getImsi;*/


@Service
public class FileService implements IFileService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AppConfig appConfig;
    @Autowired
    GreyListRepository greyListRepository;
    @Autowired
    GreyListHisRepository greyListHisRepository;
    @Autowired
    BlackListRepository blackListRepository;
    @Autowired
    BlackListHisRepository blackListHisRepository;
    @Autowired
    ExceptionListRepository exceptionListRepository;
    @Autowired
    ExceptionListHisRepository exceptionListHisRepository;

    @Autowired
    DbTransactionsService dbTransactionsService;
    @Autowired
    ExceptionListHisBuilder exceptionListHisBuilder;
    @Autowired
    AlertService alertService;
    @Autowired
    ModulesAuditTrailRepository modulesAuditTrailRepository;
    @Autowired
    ModulesAuditTrailBuilder modulesAuditTrailBuilder;
    @Autowired
    private ImeiListRepository imeiListRepository;
    @Autowired
    AppDbConfig appDbConfig;
    @Autowired
    private DuplicateDeviceDetailRepository duplicateDeviceDetailRepository;


    public void checkFileUploaded(FileDto fileDto) throws Exception{
        Path filePath = Paths.get(fileDto.getFileName());

        // Get the initial size of the file
        long initialSize = Files.size(filePath);
        Thread.sleep(appConfig.getInitialTimer());
        long currentSize = Files.size(filePath);
        // Wait for a specific duration (e.g., 5 seconds)
        while(initialSize != currentSize) {
            logger.info("The file {} is still uploading waiting for {} secs.", fileDto.getName(), appConfig.getFinalTimer());
            Thread.sleep(appConfig.getFinalTimer());
            initialSize = currentSize;
            currentSize = Files.size(filePath);
        }
        logger.info("File {} uploaded completely.", fileDto.getFileName());
        return;
    }


    @Override
    public ArrayList<FileDto> getFiles(String folderPath) {

        File dir = new File(folderPath);
        FileFilter fileFilter = new WildcardFileFilter("*"+appConfig.getFileSuffix()+"*");
        File[] files = dir.listFiles(fileFilter);
        logger.info("The count of files is {}", files.length);
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));
        logger.info("The list of files picked are {}", (Object) files);
        ArrayList<FileDto> fileDtos = new ArrayList<>();
        for (File file : files) {
            fileDtos.add(FileDto.FileDtoBuilder(file, folderPath, getFileRecordCount(file)));
        }
        return fileDtos;
    }

    @Override
    public void moveFile(FileDto file, String moveFilePath) {
        try {
            logger.info("Moving File:{} to {}", file.getFileName(), moveFilePath);
            Files.move(Paths.get(file.getFileName()), Paths.get(moveFilePath + "/" + file.getName()));
            logger.info("Moved File:{} to {}", file.getFileName(), moveFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getFileRecordCount(File file) {
        try {
            logger.info("Getting the file size for file {}", file.toURI());
            Path pathFile = Paths.get(file.toURI());
            return (long) Files.lines(pathFile).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public FailureMsg readFile(FileDto file, int modulesAuditId, long startTime) throws SQLException {
        Connection conn = appDbConfig.springDataSource().getConnection();
        FailureMsg failureMsg;
        int greyListSuccessCount = 0;
        int blackListSuccessCount = 0;
        int exceptionListSuccessCount = 0;
        int imeiListSuccessCount = 0;
        int duplicateDeviceDetailSuccessCount=0;
        String inputFileName = file.getFileName();
        Path inputPath = Paths.get(inputFileName);
        String baseFileName = inputPath.getFileName().toString().replaceFirst("[.][^.]+$", "");
        String processedFileName = baseFileName + "_processed.txt";
        String filePath = appConfig.getProcessedFile();
        filePath = filePath + "/" + processedFileName;
        try( BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
                BufferedReader reader = new BufferedReader(new FileReader(file.getFileName()))) {
            String record;
            while((record = reader.readLine()) != null) {
                if (record.isEmpty()) {
                    continue;
                }
                try {
                    String[] splitRecord = record.split(appConfig.getFileSeparator(), -1);
                    String imsi = splitRecord[file.getImsiColumnNumber()].trim();
                    String msisdn = splitRecord[file.getMsisdnColumnNumber()].trim();
                    String timestamp = splitRecord[file.getDeactivationDateColumnNumber()].trim();
                    if (imsi == null || imsi.isEmpty()) {
                        // If IMSI is null or empty, try to retrieve IMSI using the msisdn
                        imsi = com.imsi_retriever.IMSI_RETRIEVER.getImsi(msisdn, conn);
                        // If IMSI still couldn't be retrieved, mark the record as not ok and skip it
                        if (imsi == null || imsi.isEmpty()) {
                            // Write the original record along with an extra "status" column indicating "not ok"
                            writer.write(record + "," + "not ok\n");
                            continue; // Skip further processing for this record
                        } else {
                            // Update the splitRecord with the retrieved IMSI
                            splitRecord[file.getImsiColumnNumber()] = imsi;
                            // Log the successful retrieval of IMSI
                            System.out.println("Retrieved IMSI: " + imsi + " for MSISDN: " + msisdn);
                        }
                    } else {
                        // Log the presence of IMSI in the record
                        System.out.println("Found IMSI in record: " + imsi);
                    }

                    // Reconstruct the record with the updated IMSI
                    record = String.join(appConfig.getFileSeparator(), splitRecord);
                    // If IMSI is found or filled, mark the record as ok
                    writer.write(record + "," + "ok\n");

                    // check in grey list. Store the id of all the matched imsi and then delete and make entry in his tabe
                    List<GreyList> greyList = greyListRepository.findAllByImsi(imsi);
                    file.setGreyListFound(file.getGreyListFound() + greyList.size());
                    if(!greyList.isEmpty()) {

                        for (GreyList list : greyList) {
                            logger.info("The IMSI matched in grey list is : {}", list.toString());
                            boolean output1 = dbTransactionsService.dbTransaction(list);
                            if (!output1) {
                                logger.error("The entry {} failed for grey list.", greyList);
                                file.setGreyListFailure(greyList.size() - greyListSuccessCount);
                                file.setGreyListSuccess(greyListSuccessCount);
                                failureMsg = FailureMsg.FailureMsgBuilder("");
                                return failureMsg;

                            }
                            greyListSuccessCount++;
                        }
                    }
                    file.setGreyListSuccess(greyListSuccessCount);
                    List<BlackList> blackList = blackListRepository.findAllByImsi(imsi);
                    file.setBlacklistFound(file.getBlacklistFound() + blackList.size());
                    if(!blackList.isEmpty()) {

                        for (BlackList list: blackList) {
                            logger.info("The IMSI matched in black list : {}", list.toString());
                            boolean output2 = dbTransactionsService.dbTransaction(list);
                            if (!output2) {
                                // stop the file from furthue processing.......
                                // update modules_audit_trail and alert
                                logger.error("The entry {} failed for black list.", list);
                                file.setBlackListFailure(blackList.size() - blackListSuccessCount);
                                file.setBlackListSuccess(blackListSuccessCount);
                                failureMsg = FailureMsg.FailureMsgBuilder("");
                                return failureMsg;
                            }
                            blackListSuccessCount++;

                        }
                    }
                    file.setBlackListSuccess(blackListSuccessCount);
                    List<ExceptionList> exceptionList = exceptionListRepository.findAllByImsi(imsi);
                    file.setExceptionListFound(file.getExceptionListFound() + exceptionList.size());
                    if(!exceptionList.isEmpty()) {

                        for (ExceptionList list : exceptionList) {
                            logger.info("The IMSI matched in exception list : {}", list.toString());
                            boolean output3 = dbTransactionsService.dbTransaction(list);
                            if (!output3) {
                                // stop the file from further processing.......
                                // update modules_audit_trail and alert
                                logger.error("The entry {} failed for exception list.", list);
                                file.setExceptionListFailure(exceptionList.size() - exceptionListSuccessCount);
                                file.setExceptionListSuccess(exceptionListSuccessCount);
                                failureMsg = FailureMsg.FailureMsgBuilder("");
                                return failureMsg;

                            }
                            exceptionListSuccessCount++;
                        }
                        file.setExceptionListSuccess(exceptionListSuccessCount);
                    }



                    List<ImeiList> imeiList = imeiListRepository.findAllByImsi(imsi);
                    file.setImeiListFound(file.getImeiListFound() + imeiList.size());
                    if(!imeiList.isEmpty()) {

                        for (ImeiList list: imeiList) {
                            logger.info("The IMSI matched in Imei list : {}", list.toString());
                            String imei = list.getImei();
                            boolean output4 = dbTransactionsService.dbTransaction(list);
                            if (!output4) {
                                // stop the file from furthue processing.......
                                // update modules_audit_trail and alert
                                logger.error("The entry {} failed for imei list.", list);
                                file.setImeiListFailure(imeiList.size() - imeiListSuccessCount);
                                file.setImeiListSuccess(imeiListSuccessCount);
                                failureMsg = FailureMsg.FailureMsgBuilder("");
                                return failureMsg;
                            }
                            imeiListSuccessCount++;

                        }
                    }
                    file.setImeiListSuccess(imeiListSuccessCount);

                    List<DuplicateDeviceDetail> duplicateDeviceDetail = duplicateDeviceDetailRepository.findAllByImsi(imsi);
                    file.setDuplicateDeviceDetailFound(file.getDuplicateDeviceDetailFound() + duplicateDeviceDetail.size());
                    if(!duplicateDeviceDetail.isEmpty()) {

                        for (DuplicateDeviceDetail list : duplicateDeviceDetail) {
                            logger.info("The IMSI matched in duplicate device detail list : {}", list.toString());
                            boolean output5 = dbTransactionsService.dbTransaction(list);
                            if (!output5) {
                                // stop the file from further processing.......
                                // update modules_audit_trail and alert
                                logger.error("The entry {} failed for duplicate device detail list.", list);
                                file.setDuplicateDeviceDetailFailure(duplicateDeviceDetail.size() - duplicateDeviceDetailSuccessCount);
                                file.setDuplicateDeviceDetailSuccess(duplicateDeviceDetailSuccessCount);
                                failureMsg = FailureMsg.FailureMsgBuilder("");
                                return failureMsg;

                            }
                            duplicateDeviceDetailSuccessCount++;
                        }
                        file.setDuplicateDeviceDetailSuccess(duplicateDeviceDetailSuccessCount);
                    }


                } catch (Exception e) {
                    logger.error(e.toString());
                    failureMsg = FailureMsg.FailureMsgBuilder(e.getLocalizedMessage());
                    return failureMsg;

                }

            }
            reader.close();
        } catch (Exception e) {
            logger.error(e.toString());
            failureMsg = FailureMsg.FailureMsgBuilder(e.getMessage());
            return failureMsg;
        }
        failureMsg = FailureMsg.FailureMsgBuilder("");
        return failureMsg;
    }

}

