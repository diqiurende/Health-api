package com.example.health.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.ctc.wstx.util.StringUtil;
import com.example.health.api.db.dao.UserMapper;
import com.example.health.api.mis.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserMapper userMapper;

    @Override
    public Integer login(Map param) {
        MD5 md5= MD5.create();
        String username= MapUtil.getStr(param,"username");
        String password=MapUtil.getStr(param,"password");//提取账户和密码
        String temp=md5.digestHex(username);
        String start= StrUtil.subWithLength(temp,0,6);
        String end=StrUtil.subSuf(temp,temp.length()-3);
        password=md5.digestHex(start+password+end).toUpperCase();//加盐
        param.replace("password",password);//替换原来的密码

        Integer userid=userMapper.login(param);
        return userid;




    }
}
