package com.service;

import com.dao.StyleDao;
import com.pojo.StylePojo;
import io.swagger.models.auth.In;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class StyleService {
    @Autowired
    private StyleDao styleDao;

    public void add(StylePojo stylePojo) {
        styleDao.add(stylePojo);
    }

    public int select(String styleCode) throws ApiException {
        StylePojo stylePojo =  styleDao.select(styleCode);
        if(stylePojo == null){
            throw new ApiException("No such style exists.");
        }
        return stylePojo.getId();
    }
    public List<StylePojo> selectAll(){
        return styleDao.selectAll();
    }

    public void exists(StylePojo input) throws ApiException {
        StylePojo stylePojo =  styleDao.select(input.getStyleCode());
        if(stylePojo!=null){
            throw new ApiException("Product with same Style Code exists.");
        }
    }

    public HashMap<Integer, StylePojo> selectAllMap() {
        List<StylePojo> stylePojo = styleDao.selectAll();
        HashMap<Integer,StylePojo> styleMap = new HashMap<Integer,StylePojo>();
        for(StylePojo p:stylePojo){
            styleMap.put(p.getId(),p);
        }
        return styleMap;
    }
}
