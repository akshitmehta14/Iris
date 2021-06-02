package com.module;

import com.model.SalesData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModuleData {

    // input maps/lists
    // sales, sku, style, store


    // output maps/lists
    private List<SalesData> cleanedSales;

    public List<SalesData> getCleanedSales() {
        return cleanedSales;
    }

    public void setCleanedSales(List<SalesData> cleanedSales) {
        this.cleanedSales = cleanedSales;
    }

    public void clear() {
        cleanedSales = null;
    }
}
