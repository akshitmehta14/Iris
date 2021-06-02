package com.controller;

import com.dto.StyleDto;
import com.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


@Api
@RestController
@RequestMapping(path = "/api/style")
public class StyleController {
    @Autowired
    private StyleDto styleDto;

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
        styleDto.add(file);
    }

    @ApiOperation(value = "Download errors")
    @RequestMapping(path = "/errors",method = RequestMethod.GET)
    public void downloadErrors(HttpServletResponse response) throws IOException, ApiException {
        styleDto.downloadErrors(response);
    }

    @ApiOperation(value = "Display all styles")
    @RequestMapping(method = RequestMethod.GET)
    public void selectAll(HttpServletResponse response) throws IOException {
        System.out.println(5);
        styleDto.selectAll(response);
    }
}
