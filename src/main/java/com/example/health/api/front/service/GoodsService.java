package com.example.health.api.front.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
@Service
public interface GoodsService {
    public HashMap searchById(int id);
}
