package com.service;

import com.dao.StoreDao;
import com.pojo.StorePojo;
import com.pojo.StylePojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {
    @Autowired
    private StoreDao storeDao;
    public void add(StorePojo storePojo) {
        storeDao.add(storePojo);
    }

    public int select(String s) throws ApiException {
        StorePojo storePojo = storeDao.select(s);
        if(storePojo== null){
            throw new ApiException("No such Store exists.");
        }
        return storePojo.getId();
    }

    public List<StorePojo> selectAll(){
        return storeDao.selectAll();
    }

}
