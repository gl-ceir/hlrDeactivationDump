package com.gl.eirs.hlrdeactivation.repository.app;


import com.gl.eirs.hlrdeactivation.entity.app.DuplicateDeviceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuplicateDeviceDetailRepository extends JpaRepository<DuplicateDeviceDetail, Integer> {
    List<DuplicateDeviceDetail> findAllByImsi(String imsi);

}