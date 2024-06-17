package com.gl.eirs.hlrdeactivation.service.impl;

import com.gl.eirs.hlrdeactivation.builder.*;
import com.gl.eirs.hlrdeactivation.entity.app.*;
import com.gl.eirs.hlrdeactivation.repository.app.*;
import com.gl.eirs.hlrdeactivation.service.interfce.IDbTransactions;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Autowired
    private ImeiListHisRepository imeiListHisRepository;
    @Autowired
    private ImeiListRepository imeiListRepository;

    @Autowired
    private DuplicateDeviceDetailRepository duplicateDeviceDetailRepository;

    @Autowired
    private DuplicateDeviceDetailHisRepository duplicateDeviceDetailHisRepository;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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

    @Override
    @Transactional
    public boolean dbTransaction(ImeiList imeiList) {
        try {
            imeiListRepository.deleteById(Math.toIntExact(imeiList.getId()));

            // insert into imei list history table

            ImeiListHis imeiListHis = ImeiListHisBuilder.forInsert(imeiList, 0, "delete");
            imeiListHisRepository.save(imeiListHis);
            BlackList blackListEntry = blackListRepository.findByImeiAndSource(imeiList.getImei(), "auto pairing");
            logger.info("The black list entry with imei: {} and source auto pairing {}", imeiList.getImei(), blackListEntry);
            if (blackListEntry != null) {
                blackListRepository.delete(blackListEntry);
                BlackListHis blackListHisEntry = BlackListHisBuilder.forInsert(blackListEntry, 0);
                blackListHisRepository.save(blackListHisEntry);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean dbTransaction(DuplicateDeviceDetail duplicateDeviceDetail) {
        try {
            duplicateDeviceDetailRepository.deleteById(Math.toIntExact(duplicateDeviceDetail.getId()));

            // insert into grey list history table

            DuplicateDeviceDetailHis duplicateDeviceDetailHis = DuplicateDeviceDetailHisBuilder.forInsert(duplicateDeviceDetail, 0,"delete");
            duplicateDeviceDetailHisRepository.save(duplicateDeviceDetailHis);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
