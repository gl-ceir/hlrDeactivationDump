package com.gl.eirs.hlrdeactivation.service.impl;

import com.gl.eirs.hlrdeactivation.alert.AlertService;
import com.gl.eirs.hlrdeactivation.builder.ModulesAuditTrailBuilder;
import com.gl.eirs.hlrdeactivation.config.AppConfig;
import com.gl.eirs.hlrdeactivation.dto.FileDto;
import com.gl.eirs.hlrdeactivation.entity.aud.ModulesAuditTrail;
import com.gl.eirs.hlrdeactivation.messages.FailureMsg;
import com.gl.eirs.hlrdeactivation.repository.app.SysParamRepository;
import com.gl.eirs.hlrdeactivation.repository.aud.ModulesAuditTrailRepository;
import com.gl.eirs.hlrdeactivation.validation.FileValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.Level;

import java.util.logging.Level;

import static com.gl.eirs.hlrdeactivation.constants.Constants.featureName;
import static com.gl.eirs.hlrdeactivation.constants.Constants.moduleName;
import static java.sql.DriverManager.getConnection;

@Service
public class MainService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FileService fileService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    ModulesAuditTrailBuilder modulesAuditTrailBuilder;

    @Autowired
    ModulesAuditTrailRepository modulesAuditTrailRepository;

    @Autowired
    FileValidation fileValidation;

    @Autowired
    SysParamRepository sysParamRepository;

    @Autowired
    AlertService alertService;


    public void hlrDeactivationProcess()  {



        logger.info("Starting the process of HLR Deactivation process");
        String imsiPrefixValue = sysParamRepository.getValueFromTag("imsiPrefix"); // comma separated 456,457
        String msisdnPrefixValue = sysParamRepository.getValueFromTag("msisdnPrefix"); //comma separated 855,856
        if (imsiPrefixValue == null || imsiPrefixValue.isBlank() || imsiPrefixValue.isEmpty() ||
                msisdnPrefixValue == null || msisdnPrefixValue.isBlank() || msisdnPrefixValue.isEmpty()) {
            // Alert and exit the process
            logger.error("The configuration value of imsiPrefix or msisdnPrefix is missing in DB.");
            alertService.raiseAnAlert("alert5001", "", "", 0);
            return;
        }

        logger.info("Getting list of files present in the directory {}", appConfig.getFilePath());
        ArrayList<FileDto> fileDtos = fileService.getFiles(appConfig.getFilePath());
        logger.info("The count of files is {}", fileDtos.size());
        if(fileDtos.isEmpty()) {
            logger.error("No files found. Raising an alert");
            alertService.raiseAnAlert("alert5100", appConfig.getFilePath(), appConfig.getOperatorName(), 0);
            System.exit(1);
        }

        // no error then pick each file and process that file. File should be picked from oldest to latest.
        try {
            for (FileDto fileDto : fileDtos) {
                logger.info("Processing the file {}", fileDto.getFileName());
                fileService.checkFileUploaded(fileDto);
                long startTime = System.currentTimeMillis();
                // create modules_audit_trail entry for this file.
                ModulesAuditTrail modulesAuditTrail = modulesAuditTrailBuilder.forInsert(201, "INITIAL", "NA", moduleName + appConfig.getOperatorName(), featureName, "", fileDto.getFileName(), LocalDateTime.now());
                ModulesAuditTrail entity = modulesAuditTrailRepository.save(modulesAuditTrail);
                int moduleAuditId = entity.getId();

            /*
                now validate the file
                1. check the headers  (imsi, msisdn, deactivation_date)
                2. check if msisdn starts with 855.
                3. check if imsi starts with 456.
                4. check if imsi and msisdn pair unique or not.
             */
                logger.info("Checking validation for the file {}", fileDto.getFileName());
                boolean checkHeaders = fileValidation.validateHeaders(fileDto, moduleAuditId, startTime);
                if (!checkHeaders) {
                    // stop the process;
                    logger.error("The file {} failed validation for headers check", fileDto.getFileName());
                    logger.info("Skipping this file from further processing");
                    logger.info("The details of file {} processed. " +
                                    "For grey_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For black_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For exception_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For duplicate_device_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]," +
                                    "For imei_pair_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]",
                            fileDto.getName(), fileDto.getGreyListFound(), fileDto.getGreyListSuccess(), fileDto.getGreyListFailure(),
                            fileDto.getBlacklistFound(), fileDto.getBlackListSuccess(), fileDto.getBlackListFailure(),
                            fileDto.getExceptionListFound(), fileDto.getExceptionListSuccess(), fileDto.getExceptionListFailure(),
                            fileDto.getDuplicateDeviceDetailFound(), fileDto.getDuplicateDeviceDetailSuccess(), fileDto.getDuplicateDeviceDetailFailure(),
                            fileDto.getImeiListFound(), fileDto.getImeiListSuccess(), fileDto.getImeiListFailure());
                    fileService.moveFile(fileDto, appConfig.getMoveFilePath());
                    continue;
                }
//            logger.info("Header are fine");

                boolean validateIMSIAndMSISDN = fileValidation.checkPrefixForIMSIAndMSISDN(fileDto, imsiPrefixValue, msisdnPrefixValue, moduleAuditId, startTime);
                if (!validateIMSIAndMSISDN) {
                    logger.error("The file {} failed validation", fileDto.getFileName());
                    logger.info("Skipping this file from further processing");
                    logger.info("The details of file {} processed. " +
                                    "For grey_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For black_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For exception_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For duplicate_device_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]," +
                                    "For imei_pair_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]",
                            fileDto.getName(), fileDto.getGreyListFound(), fileDto.getGreyListSuccess(), fileDto.getGreyListFailure(),
                            fileDto.getBlacklistFound(), fileDto.getBlackListSuccess(), fileDto.getBlackListFailure(),
                            fileDto.getExceptionListFound(), fileDto.getExceptionListSuccess(), fileDto.getExceptionListFailure(),
                            fileDto.getDuplicateDeviceDetailFound(), fileDto.getDuplicateDeviceDetailSuccess(), fileDto.getDuplicateDeviceDetailFailure(),
                            fileDto.getImeiListFound(), fileDto.getImeiListSuccess(), fileDto.getImeiListFailure());
                    fileService.moveFile(fileDto, appConfig.getMoveFilePath());
                    continue;
                }

                boolean validateIMSIAndMSISDNPair = fileValidation.checkIMSIAndMSISDNUniquePair(fileDto, moduleAuditId, startTime);
                if (!validateIMSIAndMSISDNPair) {
                    logger.error("The file {} failed validation for uniqueness pair of IMSI and MSISDN check", fileDto.getFileName());
                    logger.info("Skipping this file from further processing");
                    logger.info("The details of file {} processed. " +
                                    "For grey_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For black_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For exception_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For duplicate_device_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]," +
                                    "For imei_pair_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]",
                            fileDto.getName(), fileDto.getGreyListFound(), fileDto.getGreyListSuccess(), fileDto.getGreyListFailure(),
                            fileDto.getBlacklistFound(), fileDto.getBlackListSuccess(), fileDto.getBlackListFailure(),
                            fileDto.getExceptionListFound(), fileDto.getExceptionListSuccess(), fileDto.getExceptionListFailure(),
                            fileDto.getDuplicateDeviceDetailFound(), fileDto.getDuplicateDeviceDetailSuccess(), fileDto.getDuplicateDeviceDetailFailure(),
                            fileDto.getImeiListFound(), fileDto.getImeiListSuccess(), fileDto.getImeiListFailure());
                    fileService.moveFile(fileDto, appConfig.getMoveFilePath());
                    continue;
                }

                boolean validateMsisdnUnique = fileValidation.checkMsisdnUnique(fileDto, moduleAuditId, startTime);
                if (!validateMsisdnUnique) {
                    logger.error("The file {} failed validation for unique MSISDN check", fileDto.getFileName());
                    logger.info("Skipping this file from further processing");
                    logger.info("The details of file {} processed. " +
                                    "For grey_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For black_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For exception_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For duplicate_device_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]," +
                                    "For imei_pair_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]",
                            fileDto.getName(), fileDto.getGreyListFound(), fileDto.getGreyListSuccess(), fileDto.getGreyListFailure(),
                            fileDto.getBlacklistFound(), fileDto.getBlackListSuccess(), fileDto.getBlackListFailure(),
                            fileDto.getExceptionListFound(), fileDto.getExceptionListSuccess(), fileDto.getExceptionListFailure(),
                            fileDto.getDuplicateDeviceDetailFound(), fileDto.getDuplicateDeviceDetailSuccess(), fileDto.getDuplicateDeviceDetailFailure(),
                            fileDto.getImeiListFound(), fileDto.getImeiListSuccess(), fileDto.getImeiListFailure());
                    fileService.moveFile(fileDto, appConfig.getMoveFilePath());
                    continue;
                }
                boolean validateImsiUnique = fileValidation.checkImsiUnique(fileDto, moduleAuditId, startTime);
                if (!validateImsiUnique) {
                    logger.error("The file {} failed validation for unique IMSI check", fileDto.getFileName());
                    logger.info("Skipping this file from further processing");
                    logger.info("The details of file {} processed. " +
                                    "For grey_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For black_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For exception_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For duplicate_device_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]," +
                                    "For imei_pair_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]",
                            fileDto.getName(), fileDto.getGreyListFound(), fileDto.getGreyListSuccess(), fileDto.getGreyListFailure(),
                            fileDto.getBlacklistFound(), fileDto.getBlackListSuccess(), fileDto.getBlackListFailure(),
                            fileDto.getExceptionListFound(), fileDto.getExceptionListSuccess(), fileDto.getExceptionListFailure(),
                            fileDto.getDuplicateDeviceDetailFound(), fileDto.getDuplicateDeviceDetailSuccess(), fileDto.getDuplicateDeviceDetailFailure(),
                            fileDto.getImeiListFound(), fileDto.getImeiListSuccess(), fileDto.getImeiListFailure());
                    fileService.moveFile(fileDto, appConfig.getMoveFilePath());
                    continue;
                }

                logger.info("All validation passed for the file {}, will read the file and process each entry", fileDto.getFileName());

            /*
            1. read contents and check the file.
            2. check values in table and make entries accordingly.

             */

                FailureMsg failureMsg = fileService.readFile(fileDto, moduleAuditId, startTime);

                if (!failureMsg.getErrMsg().isBlank() || !failureMsg.getErrMsg().isEmpty()) {
                    // some error
                    logger.error("The processing failed for file {}", fileDto.getFileName());
                    modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The file " + fileDto.getName() + " failed due to exception " + failureMsg.getErrMsg() + " for operator " + appConfig.getOperatorName(), (int)fileDto.getExceptionListSuccess() + (int)fileDto.getBlackListSuccess() + (int)fileDto.getGreyListSuccess(), (int) fileDto.getBlackListFailure() +  (int) fileDto.getGreyListFailure() + (int) fileDto.getExceptionListFailure() , (int) (System.currentTimeMillis() - startTime), LocalDateTime.now(), moduleAuditId);
                    alertService.raiseAnAlert("alert5108", fileDto.getFileName(), appConfig.getOperatorName(), 0);
                    fileService.moveFile(fileDto, appConfig.getMoveFilePath());
                    logger.info("The details of file {} processed. " +
                                    "For grey_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For black_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For exception_list: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}], " +
                                    "For duplicate_device_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]," +
                                    "For imei_pair_detail: [recordsFound: {}, recordsProcessedSuccessfully: {}, recordsFailed: {}]",
                            fileDto.getName(), fileDto.getGreyListFound(), fileDto.getGreyListSuccess(), fileDto.getGreyListFailure(),
                            fileDto.getBlacklistFound(), fileDto.getBlackListSuccess(), fileDto.getBlackListFailure(),
                            fileDto.getExceptionListFound(), fileDto.getExceptionListSuccess(), fileDto.getExceptionListFailure(),
                            fileDto.getDuplicateDeviceDetailFound(), fileDto.getDuplicateDeviceDetailSuccess(), fileDto.getDuplicateDeviceDetailFailure(),
                            fileDto.getImeiListFound(), fileDto.getImeiListSuccess(), fileDto.getImeiListFailure());
                    continue;
                }

                modulesAuditTrailRepository.updateModulesAudit(200, "SUCCESS", "NA", (int) fileDto.getNumberOfRecords(), 0, (int) (System.currentTimeMillis() - startTime), LocalDateTime.now(), moduleAuditId);
                fileService.moveFile(fileDto, appConfig.getMoveFilePath());

            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


}
