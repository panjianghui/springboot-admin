package com.pjh.v1.controller;

import com.pjh.v1.FileUtil;
import com.pjh.v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 用户业务处理类
 */
@Controller
public class userController {
    @Autowired
    UserService userService;
    // 查询用户列表
    @RequestMapping(value = "/getuserlist", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getUserList(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "limit") Integer limit,
            @RequestParam(value = "id") String id,
            HttpServletRequest request, HttpServletResponse response) {

        HashMap<Object, Object> inMap = new HashMap<Object, Object>();
        List<Map<String, Object>> userList = userService.getUserList(inMap);

        TreeMap<String, Object> outMap = new TreeMap<>();
        outMap.put("code", userList.size() > 0 ? 0 : 1);
        outMap.put("msg","查询成功");
        outMap.put("count", userList.size());
        outMap.put("data", userList);
        return outMap;
    }
}
