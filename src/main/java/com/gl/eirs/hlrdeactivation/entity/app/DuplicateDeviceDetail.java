package com.gl.eirs.hlrdeactivation.entity.app;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="duplicate_device_detail")
public class DuplicateDeviceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="imsi")
    String imsi;

    @Column(name="msisdn")
    String msisdn;

    @Column(name="imei")
    String imei;

    @Column(name="created_on")
    String createdOn;

    @Column(name="modified_on")
    LocalDateTime modifiedOn;

    @Column(name="file_name")
    String fileName;

    @Column(name="edr_time")
    String edrTime;



    @Column(name="operator")
    String operator;

    @Column(name="expiry_date")
    String expiryDate;

    @Column(name="status")
    String status;

    @Column(name="remarks")
    String remarks;

    @Column(name="updated_by")
    String updateBy;

    @Column(name="transaction_id")
    String transactionId;

    @Column(name="document_type1")
    String documentType1;

    @Column(name="document_type2")
    String documentType2;

    @Column(name="document_type3")
    String documentType3;

    @Column(name="document_type4")
    String documentType4;

    @Column(name="document_path1")
    String documentPath1;

    @Column(name="document_path2")
    String documentPath2;

    @Column(name="document_path3")
    String documentPath3;

    @Column(name="document_path4")
    String documentPath4;

    @Column(name="reminder_status")
    String reminderStatus;

    @Column(name="success_count")
    String successCount;

    @Column(name = "fail_count")
    String failCount;

    @Column(name = "actual_imei")
    String actualImei;
}

