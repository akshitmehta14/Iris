package com.service;

import com.dao.ReportDao;
import com.pojo.GoodSizesPojo;
import com.pojo.LiquidationPojo;
import com.pojo.NoosPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {
    @Autowired
    private ReportDao dao;

    public void addLiquidation(LiquidationPojo liquidationPojo){
        dao.addLiquidation(liquidationPojo);
    }

    public void addNoos(NoosPojo noosPojo){
        dao.addNoos(noosPojo);
    }

    public void addIdentification(GoodSizesPojo goodSizesPojo){
        dao.deleteIdentification();
        dao.addIdentification(goodSizesPojo);
    }

    public void deleteLiquidation(){
        dao.deleteLiquidation();
    }
    public void deleteNoos(){
        dao.deleteNoos();
    }

    public void deleteIdentification(){
        dao.deleteIdentification();
    }

    public List<LiquidationPojo> selectAllLiquidation() {
        return dao.selectAllLiquidation();
    }

    public List<NoosPojo> selectAllNoos() {
        return dao.selectAllNoos();
    }

    public List<GoodSizesPojo> selectAllIdentification() {
        return dao.selectAllIdentification();
    }

}
