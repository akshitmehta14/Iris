package com.module;

import org.springframework.beans.factory.annotation.Autowired;

public class LiquidationCleanupModule extends AbstractModule {

    @Autowired
    private ModuleData moduleData;

    @Override
    public void run() {
        initModuleData();
    }

    private void initModuleData() {

    }
}
