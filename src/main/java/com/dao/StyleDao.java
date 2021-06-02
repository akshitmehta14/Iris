package com.dao;

import com.pojo.StylePojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class StyleDao extends AbstractDao {
    private static String SELECT = "Select p from StylePojo p where p.styleCode=:styleCode";
    private static String SELECT_ALL = "Select p from StylePojo p";

    @Transactional
    public void add(StylePojo stylePojo) {
        em().persist(stylePojo);
    }

    @Transactional
    public StylePojo select(String styleCode){
        TypedQuery<StylePojo> query = getQuery(SELECT, StylePojo.class);
        query.setParameter("styleCode", styleCode);
        return getSingle(query);
    }
    public List<StylePojo> selectAll(){
        TypedQuery<StylePojo> query = getQuery(SELECT_ALL, StylePojo.class);
        return query.getResultList();
    }
}
