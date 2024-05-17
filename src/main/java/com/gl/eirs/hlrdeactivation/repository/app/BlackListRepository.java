package com.gl.eirs.hlrdeactivation.repository.app;

import com.gl.eirs.hlrdeactivation.entity.app.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Integer> {
    List<BlackList> findAllByImsi(String imsi);

}
