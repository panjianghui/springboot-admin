package com.pjh.v1.impl;

import com.pjh.v1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserImpl implements UserService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 新增用户
     * @param name
     * @param age
     */
    @Override
    public void create(String name, Integer age) {
        jdbcTemplate.update("insert into user(id,username) values(?, ?)", "1",name);
    }

    @Override
    public List<Map<String, Object>> getUserList(HashMap<Object, Object> inMap) {
        return jdbcTemplate.queryForList("select * from user;");
    }

}
