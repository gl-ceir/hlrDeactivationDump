package com.gl.eirs.hlrdeactivation.builder;

import com.gl.eirs.hlrdeactivation.entity.app.BlackList;
import com.gl.eirs.hlrdeactivation.entity.app.GreyList;
import com.gl.eirs.hlrdeactivation.entity.app.GreyListHis;
import com.gl.eirs.hlrdeactivation.entity.aud.ModulesAuditTrail;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.gl.eirs.hlrdeactivation.constants.Constants.remarks;


@Builder
public class GreyListHisBuilder {

    public static GreyListHis forInsert(GreyList greyList, int operation) {
        GreyListHis greyListHis = new GreyListHis();
        greyListHis.setImei(greyList.getImei());

        greyListHis.setImsi(greyList.getImsi());
        greyListHis.setMsisdn(greyList.getMsisdn());
        greyListHis.setRemarks(remarks);
        greyListHis.setOperation(operation);
        greyListHis.setActualImei(greyList.getActualImei());
        greyListHis.setComplainType(greyList.getComplainType());
        greyListHis.setExpiryDate(greyList.getExpiryDate());
        greyListHis.setModeType(greyList.getModeType());
        greyListHis.setOperatorName(greyList.getOperatorName());
        greyListHis.setOperatorId(greyList.getOperatorId());
        greyListHis.setRequestType(greyList.getRequestType());
        greyListHis.setTxnId(greyList.getTxnId());
        greyListHis.setUserId(greyList.getUserId());
        greyListHis.setUserType(greyList.getUserType());
        greyListHis.setTac(greyList.getTac());
        greyListHis.setSource(greyList.getSource());
        return greyListHis;
    }
}
