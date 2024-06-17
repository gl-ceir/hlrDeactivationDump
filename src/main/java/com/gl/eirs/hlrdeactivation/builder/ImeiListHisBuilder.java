package com.gl.eirs.hlrdeactivation.builder;

import com.gl.eirs.hlrdeactivation.entity.app.ImeiList;
import com.gl.eirs.hlrdeactivation.entity.app.ImeiListHis;
import lombok.Builder;

import static com.gl.eirs.hlrdeactivation.constants.Constants.remarks;


@Builder
public class ImeiListHisBuilder {

    public static ImeiListHis forInsert(ImeiList imeiList, int operation, String action) {
        ImeiListHis imeiListHis = new ImeiListHis();
        imeiListHis.setImei(imeiList.getImei());
        imeiListHis.setImsi(imeiList.getImsi());
        imeiListHis.setMsisdn(imeiList.getMsisdn());
        imeiListHis.setFileName(imeiList.getFileName());
        imeiListHis.setGsmaStatus(imeiList.getGsmaStatus());
        imeiListHis.setPairingDate(imeiList.getPairingDate());
        imeiListHis.setRecordTime(imeiList.getRecordTime());
        imeiListHis.setCreatedOn(imeiList.getCreatedOn());
        imeiListHis.setOperatorName(imeiList.getOperatorName());
        imeiListHis.setAllowedDays(imeiList.getAllowedDays());
        imeiListHis.setExpiryDate(imeiList.getExpiryDate());
        imeiListHis.setPairMode(imeiList.getPairMode());
        imeiListHis.setActionRemark(remarks);
        imeiListHis.setAction(action);
        return imeiListHis;
    }
}
