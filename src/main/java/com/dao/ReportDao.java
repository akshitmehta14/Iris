package com.dao;

import com.pojo.*;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ReportDao extends AbstractDao{
    private String SELECT_ALL_LIQUIDATION = "Select p from LiquidationPojo p";
    private String SELECT_ALL_NOOS = "Select p from NoosPojo p";
    private String SELECT_ALL_Identification = "Select p from GoodSizesPojo p";
    private String DELETE_ALL_LIQUIDATION = "Delete from LiquidationPojo";
    private String DELETE_ALL_NOOS= "Delete from NoosPojo";
    private String DELETE_ALL_IDENTIFICATION = "Delete from GoodSizesPojo";


    @Transactional
    public void addLiquidation(LiquidationPojo liquidationPojo) {
        em().persist(liquidationPojo);
    }

    @Transactional
    public void addNoos(NoosPojo noosPojo) {
        em().persist(noosPojo);
    }

    @Transactional
    public void addIdentification(GoodSizesPojo goodSizesPojo) {
        em().persist(goodSizesPojo);
    }

    @Transactional
    public void deleteLiquidation(){
        em().createQuery(DELETE_ALL_LIQUIDATION).executeUpdate();
    }

    @Transactional
    public void deleteNoos(){
        em().createQuery(DELETE_ALL_NOOS).executeUpdate();
    }

    @Transactional
    public void deleteIdentification(){
        em().createQuery(DELETE_ALL_IDENTIFICATION).executeUpdate();
    }

    public List<LiquidationPojo> selectAllLiquidation() {
        TypedQuery<LiquidationPojo> query = getQuery(SELECT_ALL_LIQUIDATION, LiquidationPojo.class);
        return query.getResultList();
    }

    public List<NoosPojo> selectAllNoos() {
        TypedQuery<NoosPojo> query = getQuery(SELECT_ALL_NOOS, NoosPojo.class);
        return query.getResultList();
    }

    public List<GoodSizesPojo> selectAllIdentification() {
        TypedQuery<GoodSizesPojo> query = getQuery(SELECT_ALL_Identification, GoodSizesPojo.class);
        return query.getResultList();
    }
}
