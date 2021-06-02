package com.module;

import org.springframework.beans.factory.annotation.Autowired;

public class GoodSizesModule extends AbstractModule {

    @Autowired
    private ModuleData moduleData;

    @Override
    public void run() {

        // TODO make sure to clear output data
    }
}
