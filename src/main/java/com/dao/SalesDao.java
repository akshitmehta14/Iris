package com.dao;

import com.pojo.SalesPojo;
import com.pojo.SkuPojo;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class SalesDao extends AbstractDao {
    private String SELECT_ALL = "Select p from SalesPojo p";

    @Transactional
    public void add(SalesPojo salesPojo) {
        em().persist(salesPojo);
    }

    public List<SalesPojo> selectAll() {
        TypedQuery<SalesPojo> query = getQuery(SELECT_ALL, SalesPojo.class);
        return query.getResultList();
    }
}
