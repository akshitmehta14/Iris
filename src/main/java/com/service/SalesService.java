package com.service;

import com.dao.SalesDao;
import com.dto.SalesDto;
import com.pojo.SalesPojo;
import com.pojo.SkuPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesService {
    @Autowired
    private SalesDao salesDao;

    public void add(SalesPojo salesPojo) {
        salesDao.add(salesPojo);
    }

    public List<SalesPojo> selectAll(){
        return salesDao.selectAll();
    }
}
