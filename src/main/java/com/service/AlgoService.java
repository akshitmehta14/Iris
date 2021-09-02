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

    public AlgoInputPojo selectRecent() throws ApiException {
        List<AlgoInputPojo> list =  algoParametersDao.selectAll();
        if(list.size()==0){
            throw new ApiException("Add some parameters first.");
        }
        return list.get(list.size()-1);
    }

    private void checkParameters(AlgoInputPojo inputPojo) throws ApiException {
        if(inputPojo.getGoodSize()<=inputPojo.getBadSize()){
            throw new ApiException("Good Size percentage cannot be less than bad size percentage.");
        }

    }
}
