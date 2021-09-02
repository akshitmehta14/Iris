package com.dao;

import com.pojo.StorePojo;
import com.pojo.StylePojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class StoreDao extends AbstractDao{

    private String SELECT = "Select p from StorePojo p where p.branch=:branch";
    private static String SELECT_ALL = "Select p from StorePojo p";
    private String SELECT_BY_ID = "Select p from StorePojo p where p.id=:id";

    @Transactional
    public void add(StorePojo storePojo) {
        em().persist(storePojo);
    }

    public StorePojo selectById(int id){
        TypedQuery<StorePojo> query = getQuery(SELECT_BY_ID, StorePojo.class);
        query.setParameter("id", id);
        return getSingle(query);
    }

    public StorePojo select(String s) {
        TypedQuery<StorePojo> query = getQuery(SELECT, StorePojo.class);
        query.setParameter("branch", s);
        return getSingle(query);
    }

    public List<StorePojo> selectAll(){
        TypedQuery<StorePojo> query = getQuery(SELECT_ALL, StorePojo.class);
        return query.getResultList();
    }

}
