package com.gl.eirs.hlrdeactivation.service.impl;

import com.gl.eirs.hlrdeactivation.builder.BlackListHisBuilder;
import com.gl.eirs.hlrdeactivation.builder.ExceptionListHisBuilder;
import com.gl.eirs.hlrdeactivation.builder.GreyListHisBuilder;
import com.gl.eirs.hlrdeactivation.entity.app.*;
import com.gl.eirs.hlrdeactivation.repository.app.*;
import com.gl.eirs.hlrdeactivation.service.interfce.IDbTransactions;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbTransactionsService implements IDbTransactions {

    @Autowired
    GreyListRepository greyListRepository;
    @Autowired
    GreyListHisRepository greyListHisRepository;
    @Autowired
    BlackListHisRepository blackListHisRepository;
    @Autowired
    BlackListRepository blackListRepository;
    @Autowired
    ExceptionListRepository exceptionListRepository;
    @Autowired
    ExceptionListHisRepository exceptionListHisRepository;

    @Override
    @Transactional
    public boolean dbTransaction(GreyList greyList) {

        // delete from grey list
        try {
            greyListRepository.deleteById(Math.toIntExact(greyList.getId()));

            // insert into grey list history table

            GreyListHis greyListHis = GreyListHisBuilder.forInsert(greyList, 0);
            greyListHisRepository.save(greyListHis);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean dbTransaction(BlackList blackList) {
        try {
            blackListRepository.deleteById(Math.toIntExact(blackList.getId()));

            // insert into grey list history table

            BlackListHis blackListHis = BlackListHisBuilder.forInsert(blackList, 0);
            blackListHisRepository.save(blackListHis);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean dbTransaction(ExceptionList exceptionList) {
        try {
            exceptionListRepository.deleteById(Math.toIntExact(exceptionList.getId()));

            // insert into grey list history table

            ExceptionListHis exceptionListHis = ExceptionListHisBuilder.forInsert(exceptionList, 0);
            exceptionListHisRepository.save(exceptionListHis);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
