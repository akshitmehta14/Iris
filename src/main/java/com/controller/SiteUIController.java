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
        return mav("home.html");
    }

    @RequestMapping(value = "/input")
    public ModelAndView input() {
        return mav("input.html");
    }

    @RequestMapping(value = "/report")
    public ModelAndView report() {
        return mav("report.html");
    }

    private ModelAndView mav(String page) {
        ModelAndView mav = new ModelAndView(page);
        mav.addObject("info", new InfoData());
        mav.addObject("baseUrl", baseUrl);
        return mav;
    }

}
