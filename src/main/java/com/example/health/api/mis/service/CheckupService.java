package com.example.health.api.mis.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
@Service
public interface CheckupService {
    public HashMap searchCheckupByPlace(String uuid, String place);

    public void addResult(int userId, String name, String uuid, String place, String template, ArrayList item);
}
