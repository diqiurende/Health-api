package com.example.health.api.front.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public interface CustomerService {

    public boolean sendSmsCode(String tel);
    public HashMap login(String tel, String code);
    public HashMap searchSummary(int id);

    public boolean update(Map param);


}
