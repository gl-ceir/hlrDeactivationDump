package com.gl.eirs.hlrdeactivation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

    String fileName;
    boolean isValid;
    long numberOfRecords;
    boolean isProcessed;
    int imsiColumnNumber;
    int msisdnColumnNumber;
    int deactivationDateColumnNumber;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String name;
    long greyListFound;
    long greyListNotFound;
    long greyListSuccess;
    long greyListFailure;
    long blacklistFound;
    long blackListNotFound;
    long blackListSuccess;
    long blackListFailure;
    long exceptionListFound;
    long exceptionListNotFound;
    long exceptionListSuccess;
    long exceptionListFailure;

    public static FileDto FileDtoBuilder(File file, String folderPath, long recordCount) {
        FileDto fileDto = new FileDto();
        fileDto.setName(file.getName());
        fileDto.setFileName(folderPath + "/" + file.getName());
        fileDto.setProcessed(false);
        fileDto.setNumberOfRecords(recordCount);
        fileDto.setValid(true);
        fileDto.setEndTime(LocalDateTime.now());
        fileDto.setStartTime(LocalDateTime.now());
        fileDto.setGreyListFound(0);
        fileDto.setGreyListNotFound(0);
        fileDto.setGreyListSuccess(0);

        fileDto.setBlacklistFound(0);
        fileDto.setBlackListNotFound(0);
        fileDto.setBlackListSuccess(0);

        fileDto.setExceptionListFound(0);
        fileDto.setExceptionListNotFound(0);
        fileDto.setExceptionListSuccess(0);
        return fileDto;
    }

}


