package com.gl.eirs.hlrdeactivation.repository.app;

import com.gl.eirs.hlrdeactivation.entity.app.GreyList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GreyListRepository extends JpaRepository<GreyList, Integer> {

    List<GreyList> findAllByImsi(String imsi);
}
