package com.example.health.api.mis.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
@Service
public interface PermissionService {
    public ArrayList<HashMap> searchAllPermission();
}