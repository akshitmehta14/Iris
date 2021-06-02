package com.controller;

import com.dto.StoreDto;
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
@RequestMapping(path = "/api/store")
public class StoreController {
    @Autowired
    private StoreDto storeDto;

    @ApiOperation(value = "Adds styles")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void add(@RequestPart MultipartFile file) throws IOException, ApiException {
        storeDto.add(file);
    }

    @ApiOperation(value = "Download errors")
    @RequestMapping(path = "/errors",method = RequestMethod.GET)
    public void downloadErrors(HttpServletResponse response) throws IOException, ApiException {
        storeDto.downloadErrors(response);
    }

    @ApiOperation(value = "Display all stores")
    @RequestMapping(method = RequestMethod.GET)
    public void selectAll(HttpServletResponse response) throws IOException {
        storeDto.selectAll(response);
    }
}
