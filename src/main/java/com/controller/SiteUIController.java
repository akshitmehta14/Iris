package com.controller;

import com.model.InfoData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SiteUIController {

    @Value("${app.baseUrl}")
    private String baseUrl;

    @RequestMapping(value = "/home")
    public ModelAndView home() {
        return getMav("home.html");
    }

    // TODO rename to module
    @RequestMapping(value = "/input")
    public ModelAndView input() {
        return getMav("input.html");
    }

    @RequestMapping(value = "/report")
    public ModelAndView report() {
        return getMav("report.html");
    }

    private ModelAndView getMav(String page) {
        ModelAndView mav = new ModelAndView(page);
        mav.addObject("baseUrl", baseUrl);
        return mav;
    }

}
