package com.gl.eirs.hlrdeactivation.repository.app;


import com.gl.eirs.hlrdeactivation.entity.app.GreyListHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GreyListHisRepository extends JpaRepository<GreyListHis, Integer> {
}
