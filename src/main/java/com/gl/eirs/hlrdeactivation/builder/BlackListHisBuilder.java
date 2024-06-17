package com.gl.eirs.hlrdeactivation.builder;

import com.gl.eirs.hlrdeactivation.entity.app.BlackList;
import com.gl.eirs.hlrdeactivation.entity.app.BlackListHis;
import lombok.Builder;
import org.springframework.stereotype.Component;

import static com.gl.eirs.hlrdeactivation.constants.Constants.remarks;



@Builder
public class BlackListHisBuilder {

    public static BlackListHis forInsert(BlackList blackList, int operation) {
        BlackListHis blackListHis = new BlackListHis();
        blackListHis.setImei(blackList.getImei());

        blackListHis.setImsi(blackList.getImsi());
        blackListHis.setMsisdn(blackList.getMsisdn());
        blackListHis.setRemarks(remarks);
        blackListHis.setOperation(operation);
        blackListHis.setActualImei(blackList.getActualImei());
        blackListHis.setComplainType(blackList.getComplainType());
        blackListHis.setExpiryDate(blackList.getExpiryDate());
        blackListHis.setModeType(blackList.getModeType());
        blackListHis.setOperatorName(blackList.getOperatorName());
        blackListHis.setOperatorId(blackList.getOperatorId());
        blackListHis.setRequestType(blackList.getRequestType());
        blackListHis.setTxnId(blackList.getTxnId());
        blackListHis.setUserId(blackList.getUserId());
        blackListHis.setUserType(blackList.getUserType());
        blackListHis.setTac(blackList.getTac());
        blackListHis.setSource(blackListHis.getSource());
        return blackListHis;
    }
}
