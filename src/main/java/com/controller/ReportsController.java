package com.controller;


import com.dto.ReportDto;
import com.pojo.GoodSizesPojo;
import com.pojo.LiquidationPojo;
import com.pojo.NoosPojo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api
@RestController
@RequestMapping(path = "/api/report")
public class ReportsController {
    @Autowired
    private ReportDto dto;

    @ApiOperation(value = "Liquidation Report")
    @RequestMapping(path = "/liquidation",method = RequestMethod.GET)
    public List<LiquidationPojo> selectAllLiquidation(){
        return dto.selectAllLiquidation();
    }

    @ApiOperation(value = "NOOS Report")
    @RequestMapping(path = "/noos",method = RequestMethod.GET)
    public List<NoosPojo> selectAllNoos(){
        return dto.selectAllNoos();
    }

    @ApiOperation(value = "Size Identification Report")
    @RequestMapping(path = "/sizes",method = RequestMethod.GET)
    public List<GoodSizesPojo> selectAllIdentification(){
        return dto.selectAllIdentification();
    }
}
