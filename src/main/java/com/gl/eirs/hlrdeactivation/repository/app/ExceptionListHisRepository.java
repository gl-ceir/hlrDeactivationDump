package com.gl.eirs.hlrdeactivation.repository.app;


import com.gl.eirs.hlrdeactivation.entity.app.ExceptionListHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionListHisRepository extends JpaRepository<ExceptionListHis, Integer> {
}
