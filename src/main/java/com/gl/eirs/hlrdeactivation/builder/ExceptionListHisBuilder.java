package com.gl.eirs.hlrdeactivation.builder;


import com.gl.eirs.hlrdeactivation.entity.app.ExceptionList;
import com.gl.eirs.hlrdeactivation.entity.app.ExceptionListHis;
import lombok.Builder;
import org.springframework.stereotype.Component;

import static com.gl.eirs.hlrdeactivation.constants.Constants.remarks;

@Component
@Builder
public class ExceptionListHisBuilder {

    public static ExceptionListHis forInsert(ExceptionList exceptionList, int operation) {
        ExceptionListHis exceptionListHis = new ExceptionListHis();
        exceptionListHis.setImei(exceptionList.getImei());

        exceptionListHis.setImsi(exceptionList.getImsi());
        exceptionListHis.setMsisdn(exceptionList.getMsisdn());
        exceptionListHis.setRemarks(remarks);
        exceptionListHis.setOperation(operation);
        exceptionListHis.setActualImei(exceptionList.getActualImei());
        exceptionListHis.setComplainType(exceptionList.getComplainType());
        exceptionListHis.setExpiryDate(exceptionList.getExpiryDate());
        exceptionListHis.setModeType(exceptionList.getModeType());
        exceptionListHis.setOperatorName(exceptionList.getOperatorName());
        exceptionListHis.setOperatorId(exceptionList.getOperatorId());
        exceptionListHis.setRequestType(exceptionList.getRequestType());
        exceptionListHis.setTxnId(exceptionList.getTxnId());
        exceptionListHis.setUserId(exceptionList.getUserId());
        exceptionListHis.setUserType(exceptionList.getUserType());
        exceptionListHis.setTac(exceptionList.getTac());
        exceptionListHis.setSource(exceptionList.getSource());
        return exceptionListHis;
    }
}
