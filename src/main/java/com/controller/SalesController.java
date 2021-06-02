package com.controller;

import com.dto.SalesDto;
import com.dto.StoreDto;
import com.service.ApiException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Api
@RestController
@RequestMapping(path = "/api/sales")
public class SalesController {
    @Autowired
    private SalesDto salesDto;

    @ApiOperation(value = "Adds styles")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void add(@RequestPart MultipartFile file) throws IOException, ApiException {
        salesDto.add(file);
    }
}