package com.controller;

import com.dto.AlgoDto;
import com.model.InputForm;
import com.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Api
@RestController
@RequestMapping(path = "/api/algo")
public class AlgoRunController {
    @Autowired
    private AlgoDto algoDto;

    @ApiOperation(value = "Runs the Algorithm")
    @RequestMapping(method = RequestMethod.POST)
    public void addParameters(@RequestBody InputForm input) throws ApiException {
        System.out.println(input.getLiquidationMultiplier());
        System.out.println(input.getDate());
        System.out.println(input.getBadSize());
        algoDto.addParameters(input);
    }

    @ApiOperation(value = "Runs the Algorithm")
    @RequestMapping(method = RequestMethod.GET)
    public void runAlgo() throws ApiException, IOException {
        algoDto.algoRun();
    }
}