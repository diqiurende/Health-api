package com.example.health.api.mis.service.impl;

import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;

import com.example.health.api.common.PageUtils;
import com.example.health.api.db.dao.UserMapper;
import com.example.health.api.db.pojo.UserEntity;
import com.example.health.api.mis.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Override
    public int updatePassword(Map param) {
        int userId=MapUtil.getInt(param,"userId");
        String username=userMapper.SearchUserNameById(userId);//提取到username 用于下面加密

        MD5 md5= MD5.create();
        String password = MapUtil.getStr(param, "password");
        String temp=md5.digestHex(username);
        String start= StrUtil.subWithLength(temp,0,6);
        String end=StrUtil.subSuf(temp,temp.length()-3);
        password=md5.digestHex(start+password+end).toUpperCase();//加盐
        param.replace("password",password);
        String newPassword = MapUtil.getStr(param, "newPassword");//提取新密码
        newPassword = md5.digestHex(start + newPassword + end).toUpperCase();//加盐
        param.replace("newPassword", newPassword);

        System.out.println(param);

        int resultRows=userMapper.UpadatePassword(param);
        return resultRows;

    }

    @Override
    public PageUtils searchByPage(Map param) {

        //构建初始列表
        ArrayList<HashMap> list=new ArrayList<>();
        long count = userMapper.searchCount(param);
        //若查询到的总记录数不为空 查询当页数据
        if(count>0){
            list=userMapper.searchByPage(param);
        }
        //提取分页参数
        int page=MapUtil.getInt(param,"page");
        int length=MapUtil.getInt(param,"length");

        //构建分页工具对象并返回
        PageUtils pageUtils=new PageUtils(list,count,page,length);
        return pageUtils;
    }


    @Override
    public long searchCount(Map param) {
        return 0;
    }

    @Override
    @Transactional
    public int insert(UserEntity user) {
        MD5 md5 = MD5.create();
        String temp = md5.digestHex(user.getUsername());
        String tempStart = StrUtil.subWithLength(temp, 0, 6);
        String tempEnd = StrUtil.subSuf(temp, temp.length() - 3);
        String password = md5.digestHex(tempStart + user.getPassword() + tempEnd).toUpperCase();
        user.setPassword(password);
        int rows = userMapper.insert(user);
        return rows;
    }

    @Override
    public HashMap searchById(int userId) {
        HashMap map=userMapper.searchById(userId);
        return map;
    }

    @Override
    public int update(Map param) {
       int rows=userMapper.update(param);
       return rows;
    }

    @Override
    @Transactional
    public int deleteUserByIds(Integer[] ids){
        int rows=userMapper.deleteUserByIds(ids);
        return rows;
    }

    @Override
    public int dismisss(int id) {
        int rows=userMapper.dismisss(id);
        return rows;
    }


    @Override
    public HashMap searchDoctorById(int userId) {
        HashMap map = userMapper.searchDoctorById(userId);
        return map;
    }
}
