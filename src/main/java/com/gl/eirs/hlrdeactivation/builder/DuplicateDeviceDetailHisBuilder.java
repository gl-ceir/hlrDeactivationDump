package com.gl.eirs.hlrdeactivation.builder;

import com.gl.eirs.hlrdeactivation.entity.app.DuplicateDeviceDetail;
import com.gl.eirs.hlrdeactivation.entity.app.DuplicateDeviceDetailHis;
import lombok.Builder;
import org.springframework.stereotype.Component;

import static com.gl.eirs.hlrdeactivation.constants.Constants.remarks;

@Component
@Builder
public class DuplicateDeviceDetailHisBuilder {
    public static DuplicateDeviceDetailHis forInsert(DuplicateDeviceDetail duplicateDeviceDetail, int operation, String action) {
        DuplicateDeviceDetailHis duplicateDeviceDetailHis = new DuplicateDeviceDetailHis();
        duplicateDeviceDetailHis.setImsi(duplicateDeviceDetail.getImsi());
        duplicateDeviceDetailHis.setId(duplicateDeviceDetail.getId());
        duplicateDeviceDetailHis.setImei(duplicateDeviceDetail.getImei());
        duplicateDeviceDetailHis.setMsisdn(duplicateDeviceDetail.getMsisdn());
        duplicateDeviceDetailHis.setCreatedOn(duplicateDeviceDetail.getCreatedOn());
        duplicateDeviceDetailHis.setModifiedOn(duplicateDeviceDetail.getModifiedOn());
        duplicateDeviceDetailHis.setFileName(duplicateDeviceDetail.getFileName());
        duplicateDeviceDetailHis.setEdrTime(duplicateDeviceDetail.getEdrTime());
        duplicateDeviceDetailHis.setStatus(duplicateDeviceDetail.getStatus());
        duplicateDeviceDetailHis.setOperator(duplicateDeviceDetail.getOperator());
        duplicateDeviceDetailHis.setExpiryDate(duplicateDeviceDetail.getExpiryDate());
        duplicateDeviceDetailHis.setUpdateBy(duplicateDeviceDetail.getUpdateBy());
        duplicateDeviceDetailHis.setTransactionId(duplicateDeviceDetail.getTransactionId());
        duplicateDeviceDetailHis.setDocumentType1(duplicateDeviceDetail.getDocumentType1());
        duplicateDeviceDetailHis.setDocumentType2(duplicateDeviceDetail.getDocumentType2());
        duplicateDeviceDetailHis.setDocumentType3(duplicateDeviceDetail.getDocumentType3());
        duplicateDeviceDetailHis.setDocumentType4(duplicateDeviceDetail.getDocumentType4());
        duplicateDeviceDetailHis.setDocumentPath1(duplicateDeviceDetail.getDocumentPath1());
        duplicateDeviceDetailHis.setDocumentPath2(duplicateDeviceDetail.getDocumentPath2());
        duplicateDeviceDetailHis.setDocumentPath3(duplicateDeviceDetail.getDocumentPath3());
        duplicateDeviceDetailHis.setDocumentPath4(duplicateDeviceDetail.getDocumentPath4());
        duplicateDeviceDetailHis.setReminderStatus(duplicateDeviceDetail.getReminderStatus());
        duplicateDeviceDetailHis.setSuccessCount(duplicateDeviceDetail.getSuccessCount());
        duplicateDeviceDetailHis.setFailCount(duplicateDeviceDetail.getFailCount());
        duplicateDeviceDetailHis.setActualImei(duplicateDeviceDetail.getActualImei());
        duplicateDeviceDetailHis.setAction(action);
        duplicateDeviceDetailHis.setActionRemark(remarks);
        return duplicateDeviceDetailHis;
    }
}
