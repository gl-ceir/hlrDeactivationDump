package com.gl.eirs.hlrdeactivation.entity.app;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="sys_param")
public class SysParam {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    @Column(name = "tag")
    String tag;

    @Column(name = "value")
    String value;

}
