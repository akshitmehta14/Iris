package com.dto;

import com.pojo.GoodSizesPojo;
import com.pojo.LiquidationPojo;
import com.pojo.NoosPojo;
import com.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportDto {
    @Autowired
    private ReportService service;

    public List<LiquidationPojo> selectAllLiquidation(){
        return service.selectAllLiquidation();
    }

    public List<NoosPojo> selectAllNoos(){
        return service.selectAllNoos();
    }

    public List<GoodSizesPojo> selectAllIdentification(){
        return service.selectAllIdentification();
    }
}
