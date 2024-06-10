package com.gl.eirs.hlrdeactivation.service.interfce;

import com.gl.eirs.hlrdeactivation.dto.FileDto;
import com.gl.eirs.hlrdeactivation.messages.FailureMsg;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

public interface IFileService {


    ArrayList<FileDto> getFiles(String folderPath);
    void moveFile(FileDto file, String moveFilePath);

    long getFileRecordCount(File file);

    FailureMsg readFile(FileDto file, int moduleAuditId, long startTime) throws SQLException;


}
