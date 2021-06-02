package com.controller;

import com.dto.SkuDto;
import com.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Api
@RestController
@RequestMapping(path = "/api/sku")
public class SkuController {
    @Autowired
    private SkuDto skuDto;

    @ApiOperation(value = "Adds styles")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void add(@RequestPart MultipartFile file) throws IOException, ApiException {
//        System.out.println(5);
//        BufferedReader TSVFile = new BufferedReader(new
//                InputStreamReader(file.getInputStream(), "UTF-8"));
//        TSVParser csvParser = new TSVParser(fileReader, TSVFormat.DEFAULT);
//
//        Iterable<TSVRecord> csvRecords = csvParser.getRecords();
//
//        for (TSVRecord csvRecord : csvRecords) {
//            System.out.println(csvRecord);
//        }
        skuDto.add(file);
    }
    @ApiOperation(value = "Display all sku")
    @RequestMapping(method = RequestMethod.GET)
    public void selectAll(HttpServletResponse response) throws IOException {
        skuDto.selectAll(response);
    }

    @ApiOperation(value = "Download errors")
    @RequestMapping(path = "/errors",method = RequestMethod.GET)
    public void downloadErrors(HttpServletResponse response) throws IOException, ApiException {
        skuDto.downloadErrors(response);
    }
}
