package com.service;

import com.dao.SkuDao;
import com.pojo.SkuPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class SkuService {
    @Autowired
    private SkuDao skuDao;

    public void add(SkuPojo skuPojo) {
        skuDao.add(skuPojo);
    }

    public String selectById(int id){
        return skuDao.selectById(id).getSkuCode();
    }

    public int select(String skuCode) throws ApiException {
        SkuPojo skuPojo = skuDao.select(skuCode);
        if(skuPojo==null){
            throw new ApiException("No such sku exists.");
        }
        return skuPojo.getId();
    }
    public List<SkuPojo> selectAll(){
        return skuDao.selectAll();
    }

    public HashMap<Integer,SkuPojo> selectAllMap(){
        List<SkuPojo> list= skuDao.selectAll();
        HashMap<Integer,SkuPojo> skuMap = new HashMap<Integer,SkuPojo>();
        for(SkuPojo p:list){
            skuMap.put(p.getId(),p);
        }
        return skuMap;
    }

    public void exists(SkuPojo input) throws ApiException {
        SkuPojo skuPojo = skuDao.select(input.getSkuCode());
        if(skuPojo!=null){
            throw new ApiException("SKU already exists.");
        }
    }
}
