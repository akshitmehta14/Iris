package com.service;

import com.dao.AlgoParametersDao;
import com.pojo.AlgoInputPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlgoService {
    @Autowired
    AlgoParametersDao algoParametersDao;

    public void addParameters(AlgoInputPojo inputPojo) throws ApiException {
        checkParameters(inputPojo);
        algoParametersDao.delete();
        algoParametersDao.add(inputPojo);
    }

    public AlgoInputPojo selectRecent(){
        List<AlgoInputPojo> list =  algoParametersDao.selectAll();
        return list.get(list.size()-1);
    }

    private void checkParameters(AlgoInputPojo inputPojo) throws ApiException {
        if(inputPojo.getBadSize()==0.0 && inputPojo.getGoodSize()==0.0){
            inputPojo.setGoodSize(75);
            inputPojo.setBadSize(25);
        }
        if(inputPojo.getGoodSize()==0.0){
            inputPojo.setGoodSize(inputPojo.getBadSize());
        }
        if(inputPojo.getBadSize()==0.0){
            inputPojo.setBadSize(inputPojo.getGoodSize());
        }
        if(inputPojo.getGoodSize()<inputPojo.getBadSize()){
            throw new ApiException("Good Size percentage cannot be greater than bad size percentage.");
        }

    }
}
