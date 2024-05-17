package com.gl.eirs.hlrdeactivation.service.interfce;
import com.gl.eirs.hlrdeactivation.entity.app.BlackList;
import com.gl.eirs.hlrdeactivation.entity.app.ExceptionList;
import com.gl.eirs.hlrdeactivation.entity.app.GreyList;

public interface IDbTransactions {

    public boolean dbTransaction(GreyList greyList);
    public boolean dbTransaction(BlackList blackList);
    public boolean dbTransaction(ExceptionList exceptionList);
}

