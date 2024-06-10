package com.gl.eirs.hlrdeactivation.validation;

import com.gl.eirs.hlrdeactivation.alert.AlertService;
import com.gl.eirs.hlrdeactivation.builder.ModulesAuditTrailBuilder;
import com.gl.eirs.hlrdeactivation.config.AppConfig;
import com.gl.eirs.hlrdeactivation.dto.FileDto;
import com.gl.eirs.hlrdeactivation.entity.aud.ModulesAuditTrail;
import com.gl.eirs.hlrdeactivation.repository.app.SysParamRepository;
import com.gl.eirs.hlrdeactivation.repository.aud.ModulesAuditTrailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.io.*;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




@Component
public class FileValidation {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    AppConfig appConfig;
    @Autowired
    SysParamRepository sysParamRepository;

    @Autowired
    ModulesAuditTrailRepository modulesAuditTrailRepository;
    @Autowired
    AlertService alertService;

//    @Autowired
//    ModulesAuditTrail modulesAuditTrail;
    private ModulesAuditTrail modulesAuditTrail = null;

    public boolean    validateHeaders(FileDto file, int modulesAuditId, long startTime) {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader(file.getFileName()));
            String line = reader.readLine();

            if (line != null) {

                String[] lines = line.split(appConfig.getFileSeparator(), -1);
                logger.info("Checking IMSI present in file {} ", file.getFileName());
                int imsiColumnNumber = isContains(lines, appConfig.getImsiHeaderValue());

                if (imsiColumnNumber != -1) {
                    file.setImsiColumnNumber(imsiColumnNumber);
                } else {
                    logger.info("The file {} does not contains IMSI", file.getFileName());
                    // raise alert and stop processing for this file.
//                    modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " does not contains IMSI for operator " + appConfig.getOperatorName(), moduleName, featureName, "", file.getFileName(), 0, 0, startTime);
//                    modulesAuditTrailRepository.save(modulesAuditTrail);
                    modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " does not contains IMSI for operator " + appConfig.getOperatorName(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

                    // update the audit entry for this file.
                    alertService.raiseAnAlert("alert5101", file.getFileName(), appConfig.getOperatorName(), 0);
                    return false;
                }
                logger.info("Checking MSISDN present in file {} ", file.getFileName());
                int msisdnColumnNumber = isContains(lines, appConfig.getMsisdnHeaderValue());
                if (msisdnColumnNumber != -1) {
                    file.setMsisdnColumnNumber(msisdnColumnNumber);
                } else {
                    logger.info("The file {} does not contains MSISDN", file.getFileName());
                    // raise alert and stop processing for this file.
                    // update the audit entry for this file.
//                    modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " does not contains MSISDN for operator " + appConfig.getOperatorName(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//                    modulesAuditTrailRepository.save(modulesAuditTrail);
                    modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " does not contains MSISDN for operator " + appConfig.getOperatorName(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

                    // update the audit entry for this file.
                    alertService.raiseAnAlert("alert5102", file.getFileName(), appConfig.getOperatorName(), 0);
                    return false;
                }
                logger.info("Checking deactivation_date present in file {} ", file.getFileName());
                int deactivationDateColumnNumber = isContains(lines, appConfig.getDeactivationDateHeaderValue());
                if ( deactivationDateColumnNumber != -1) {
                    logger.info("deactivation_date  present in the file {}", file.getFileName());
                    file.setDeactivationDateColumnNumber(deactivationDateColumnNumber);
                } else {
                    logger.info("The file {} does not contains deactivation_date ", file.getFileName());
                    // raise alert and stop processing for this file.
                    // update the audit entry for this file.
//                    modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " does not contains deactivation_date for operator " + appConfig.getOperatorName(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//                    modulesAuditTrailRepository.save(modulesAuditTrail);
                    modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " does not contains deactivation_date for operator " + appConfig.getOperatorName(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

                    // update the audit entry for this file.
                    alertService.raiseAnAlert("alert5103", file.getFileName(), appConfig.getOperatorName(), 0);
                    return false;
                }
            }
            else {
                logger.info("The file is empty, skipping the file {}", file.getFileName());
                return false;
            }
            reader.close();
            return true;
        } catch (IOException e) {
            logger.error("Exception in the checking validation for headers in file {}" , file.getFileName());
            // raise alert
            // update modules audit trail
//            modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " failed due to " + e.getLocalizedMessage(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//            modulesAuditTrailRepository.save(modulesAuditTrail);
            modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " failed due to " + e.getLocalizedMessage(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

            // update the audit entry for this file.
            alertService.raiseAnAlert("alert5108", file.getFileName(), appConfig.getOperatorName(), 0);
            return false;
        }
    }

    public int isContains(String[] line, String value) {
        for (int i = 0; i < line.length; i++) {
            if (line[i].equalsIgnoreCase(value)) {
                return i;
            }
        }
        return -1;

    }

    // check if imsi and msisdn starts with prefix or not

    public boolean checkPrefixForIMSIAndMSISDN(FileDto file, String imsiPrefixValue, String msisdnPrefixValue, int modulesAuditId,  long startTime) {

        String[] imsiPrefix = imsiPrefixValue.split(",", -1);
        String[] msisdnPrefix = msisdnPrefixValue.split(",", -1);
        logger.info("imsiprefix msisdnPrefix {} {}", imsiPrefix, msisdnPrefix);
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getFileName()));) {
            String nextLine;
            // skipping first line as headers/
            nextLine = reader.readLine();
            while ((nextLine = reader.readLine()) != null) {
                if (nextLine.isEmpty()) {
                    continue;
                }
                String[] record = nextLine.split(appConfig.getFileSeparator(), -1);
                String imsi = record[file.getImsiColumnNumber()].trim();
                String msisdn = record[file.getMsisdnColumnNumber()].trim();


                boolean flagImsiPrefix = false;
                boolean flagImsiNull = false;
                for (int i = 0; i < imsiPrefix.length; i++) {
                    if (imsi.isEmpty()) {
                        flagImsiNull = true;
                        break;
                    }
                    if (!imsi.startsWith(imsiPrefix[i])) {
                        flagImsiPrefix = true;
                        break;
                    }
                }
                if(flagImsiPrefix) {
                    // alert
                    logger.error("Returned for imsi");
//                    modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "Some entries does not matches the IMSI prefix for HLR deactivation file " + file.getFileName() + "for operator " + appConfig.getOperatorName(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//                    modulesAuditTrailRepository.save(modulesAuditTrail);
                    modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "Some entries does not matches the IMSI prefix for HLR deactivation file " + file.getFileName() + "for operator " + appConfig.getOperatorName(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

                    // update the audit entry for this file.
                    alertService.raiseAnAlert("alert5104", file.getFileName(), appConfig.getOperatorName(), 0);
                    return false;
                }
/*                if(flagImsiNull) {
                    logger.error("Returned for imsi");
//                    modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "Null/Non-Numeric entries detected in IMSI for HLR deactivation file " + file.getFileName() + "for operator " + appConfig.getOperatorName(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//                    modulesAuditTrailRepository.save(modulesAuditTrail);
                    // update the audit entry for this file.
                    modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "Null/Non-Numeric entries detected in IMSI for HLR deactivation file " + file.getFileName() + "for operator " + appConfig.getOperatorName(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

                    alertService.raiseAnAlert("alert5107", file.getFileName(), appConfig.getOperatorName(), 0);
                }*/
                boolean flagMsisdnPrefix = false;
                boolean flagMsisdnNull = false;
                for(int i=0;i<msisdnPrefix.length;i++) {

                    if (!msisdn.startsWith(msisdnPrefix[i])) {
                        flagMsisdnPrefix = true;
                    }
                }
                if(flagMsisdnPrefix) {
                    // alert
//                    modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "Some entries does not matches the MSISDN prefix for HLR deactivation file " + file.getFileName() + "for operator " + appConfig.getOperatorName(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//                    modulesAuditTrailRepository.save(modulesAuditTrail);
                    modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "Some entries does not matches the MSISDN prefix for HLR deactivation file " + file.getFileName() + "for operator " + appConfig.getOperatorName(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

                    // update the audit entry for this file.
                    alertService.raiseAnAlert("alert5105", file.getFileName(), appConfig.getOperatorName(), 0);
                    return false;
                }
                if(flagMsisdnNull) {
//                    logger.error("Returned for imsi");
//                    logger.error("Returned for imsi");
//                    modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "Null/Non-Numeric entries detected in MSISDN for HLR deactivation file " + file.getFileName() + "for operator " + appConfig.getOperatorName(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//                    modulesAuditTrailRepository.save(modulesAuditTrail);
                    modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "Null/Non-Numeric entries detected in MSISDN for HLR deactivation file " + file.getFileName() + "for operator " + appConfig.getOperatorName(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

                    // update the audit entry for this file.
                    alertService.raiseAnAlert("alert5107", file.getFileName(), appConfig.getOperatorName(), 0);
                    return false;

                }
            }
        } catch (IOException e) {
            // raise alert for exception
            // update modules audit trail
//            modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " failed due to " + e.getLocalizedMessage(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//            modulesAuditTrailRepository.save(modulesAuditTrail);
            modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " failed due to " + e.getLocalizedMessage(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

            // update the audit entry for this file.
            alertService.raiseAnAlert("alert5108", file.getFileName(), appConfig.getOperatorName(), 0);
            return false;
        }
        return true;
    }


    public boolean checkIMSIAndMSISDNUniquePair(FileDto file, int modulesAuditId,  long startTime) {

        String imsiNumber= String.valueOf(file.getImsiColumnNumber()+1);
        String msisdnNumber = String.valueOf(file.getMsisdnColumnNumber()+1);

        try {

            List<ProcessBuilder> processBuilders = Arrays.asList(
                    new ProcessBuilder("cut", "-d"+appConfig.getFileSeparator(),
                            "-f"+imsiNumber.trim()+","+msisdnNumber.trim(),
                            file.getFileName()),
                    new ProcessBuilder("sort"),
                    new ProcessBuilder("uniq"),
                    new ProcessBuilder("wc", "-l")
            );

            List<Process> processes = ProcessBuilder.startPipeline(processBuilders);
            logger.info("Process array is {}" , processes);
            Process last = processes.get(processes.size() - 1);


            // Read the output of the process
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(last.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {

                    if( !line.trim().equalsIgnoreCase(String.valueOf(file.getNumberOfRecords()))) {
                        // alert
//                        logger.error("The validation for unique pair checking failed");
//                        modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "The HLR deactivation file " +file.getFileName()+" does not contain unique pair of IMSI and MSISDN for operator " +appConfig.getOperatorName(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//                        modulesAuditTrailRepository.save(modulesAuditTrail);
                        modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The HLR deactivation file " +file.getFileName()+" does not contain unique pair of IMSI and MSISDN for operator " +appConfig.getOperatorName(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

                        // update the audit entry for this file.
                        alertService.raiseAnAlert("alert5106", file.getFileName(), appConfig.getOperatorName(), 0);
                        return false;
                    }
                }
            } catch (IOException e) {
                // alert
//                modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " failed due to " + e.getLocalizedMessage(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//                modulesAuditTrailRepository.save(modulesAuditTrail);
                // update the audit entry for this file.
                modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " failed due to " + e.getLocalizedMessage(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

                alertService.raiseAnAlert("alert5108", file.getFileName(), appConfig.getOperatorName(), 0);

                return false;
            }
        } catch (Exception e) {
            // alert
//            modulesAuditTrail = ModulesAuditTrailBuilder.forUpdate(modulesAuditId, 501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " failed due to " + e.getLocalizedMessage(), moduleName, featureName, "", file.getFileName(), 0, (int)file.getNumberOfRecords(), startTime);
//            modulesAuditTrailRepository.save(modulesAuditTrail);
            modulesAuditTrailRepository.updateModulesAudit(501, "FAIL", "The HLR deactivation file "+ file.getFileName() + " failed due to " + e.getLocalizedMessage(), 0, (int) file.getNumberOfRecords(), (int) ( System.currentTimeMillis()  -  startTime ), LocalDateTime.now(), modulesAuditId);

            // update the audit entry for this file.
            alertService.raiseAnAlert("alert5108", file.getFileName(), appConfig.getOperatorName(), 0);
            return false;
        }
        return true;
    }

}
