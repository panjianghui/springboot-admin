package com.pjh.v1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class IdexController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }
    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }
    @RequestMapping("/getIndex1")
    public String getIndex() {
        return "hello boy!!";
    }

    @RequestMapping("/getindex")
    public String index(ModelMap map) throws Exception {
        map.addAttribute("hello", "http://www.baidu.com");
        return "index";
    }

}
