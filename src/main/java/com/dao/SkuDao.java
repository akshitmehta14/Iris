package com.dao;

import com.pojo.SkuPojo;
import com.pojo.StorePojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;


@Repository
public class SkuDao extends AbstractDao {
    private String SELECT = "Select p from SkuPojo p where p.skuCode=:skuCode";
    private String SELECT_ALL = "Select p from SkuPojo p";

    @Transactional
    public void add(SkuPojo skuPojo) {
        em().persist(skuPojo);
    }

    public SkuPojo select(String skuCode) {
        TypedQuery<SkuPojo> query = getQuery(SELECT, SkuPojo.class);
        query.setParameter("skuCode", skuCode);
        return getSingle(query);
    }
    public List<SkuPojo> selectAll(){
        TypedQuery<SkuPojo> query = getQuery(SELECT_ALL, SkuPojo.class);
        return query.getResultList();
    }
}
