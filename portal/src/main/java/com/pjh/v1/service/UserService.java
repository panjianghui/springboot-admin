package com.pjh.v1.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 新增用户
     * @param name
     * @param age
     */
    void create(String name, Integer age);
    List<Map<String, Object>> getUserList(HashMap<Object, Object> inMap);
}
