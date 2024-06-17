package com.gl.eirs.hlrdeactivation.entity.app;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name="exception_list")
public class ExceptionList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="imsi")
    String imsi;

    @Column(name="msisdn")
    String msisdn;

    @Column(name="imei")
    String imei;

    @Column(name="complaint_type")
    String complainType;

    @Column(name="expiry_date")
    LocalDateTime expiryDate;

    @Column(name="mode_type")
    String modeType;


    @Column(name="request_type")
    String requestType;

    @Column(name="txn_id")
    String txnId;

    @Column(name="user_id")
    String userId;

    @Column(name="user_type")
    String userType;

    @Column(name="operator_id")
    String operatorId;

    @Column(name="operator_name")
    String operatorName;

    @Column(name="actual_imei")
    String actualImei;

    @Column(name="tac")
    String tac;

    @Column(name="remarks")
    String remarks;

    @Column(name="source")
    String source;
}
