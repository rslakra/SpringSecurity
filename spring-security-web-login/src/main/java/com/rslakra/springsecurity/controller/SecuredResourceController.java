package com.rslakra.springsecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class SecuredResourceController {

    /**
     *
     * @param request
     * @param response
     */
    @RequestMapping("/secured")
    public void secureResource(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("accessing secured resource");
    }

}
