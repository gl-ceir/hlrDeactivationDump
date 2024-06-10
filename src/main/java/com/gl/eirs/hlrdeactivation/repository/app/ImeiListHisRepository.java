package com.gl.eirs.hlrdeactivation.repository.app;

import com.gl.eirs.hlrdeactivation.entity.app.BlackListHis;
import com.gl.eirs.hlrdeactivation.entity.app.ImeiListHis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImeiListHisRepository extends JpaRepository<ImeiListHis, Integer> {
}
