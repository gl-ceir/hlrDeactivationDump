package com.gl.eirs.hlrdeactivation.service.interfce;
import com.gl.eirs.hlrdeactivation.entity.app.*;
import jakarta.transaction.Transactional;

public interface IDbTransactions {

    public boolean dbTransaction(GreyList greyList);
    public boolean dbTransaction(BlackList blackList);
    public boolean dbTransaction(ExceptionList exceptionList);
    public boolean dbTransaction(ImeiList imeiList);
    public boolean dbTransaction(DuplicateDeviceDetail duplicateDeviceDetail);
}

