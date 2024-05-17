package com.gl.eirs.hlrdeactivation.repository.app;

import com.gl.eirs.hlrdeactivation.entity.app.ExceptionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExceptionListRepository extends JpaRepository<ExceptionList, Integer> {


    List<ExceptionList> findAllByImsi(String imsi);
}
