package com.example.health.api.mis.service;

import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface UserService {
    public Integer login(Map param);
}
