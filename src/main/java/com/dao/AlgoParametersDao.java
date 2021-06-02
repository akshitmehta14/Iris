package com.dao;

import com.pojo.AlgoInputPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class AlgoParametersDao extends AbstractDao{
    private String SELECT_ALL = "Select p from AlgoInputPojo p";

    @Transactional
    public void add(AlgoInputPojo inputPojo){
        em().persist(inputPojo);
    }

    public List<AlgoInputPojo> selectAll(){
        TypedQuery<AlgoInputPojo> query = getQuery(SELECT_ALL, AlgoInputPojo.class);
        return query.getResultList();
    }
}
