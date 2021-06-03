package com.dao;

import com.pojo.AlgoInputPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class AlgoParametersDao extends AbstractDao{
    private String SELECT_ALL = "Select p from AlgoInputPojo p";
    private String Delete_ALL = "DELETE from AlgoInputPojo";

    @Transactional
    public void add(AlgoInputPojo inputPojo){
        em().persist(inputPojo);
    }

    @Transactional
    public void delete(){
        em().createQuery(Delete_ALL).executeUpdate();
    }

    public List<AlgoInputPojo> selectAll(){
        TypedQuery<AlgoInputPojo> query = getQuery(SELECT_ALL, AlgoInputPojo.class);
        return query.getResultList();
    }
}
