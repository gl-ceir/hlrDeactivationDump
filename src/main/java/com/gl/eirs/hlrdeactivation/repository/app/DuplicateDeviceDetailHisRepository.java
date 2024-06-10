package com.gl.eirs.hlrdeactivation.repository.app;

import com.gl.eirs.hlrdeactivation.entity.app.DuplicateDeviceDetailHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DuplicateDeviceDetailHisRepository extends JpaRepository<DuplicateDeviceDetailHis, Integer> {
}
