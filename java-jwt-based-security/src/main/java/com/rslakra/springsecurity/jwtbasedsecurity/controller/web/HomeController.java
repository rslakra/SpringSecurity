package com.rslakra.springsecurity.jwtbasedsecurity.controller.web;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import com.rslakra.springsecurity.jwtbasedsecurity.utils.JWTUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(value = {"/", "/index"}, method = GET)
    public String indexPage(Model model, HttpServletRequest servletRequest) {
        model.addAttribute("requestUrl", JWTUtils.getRequestUrl(servletRequest));
        return "index";
    }
}
