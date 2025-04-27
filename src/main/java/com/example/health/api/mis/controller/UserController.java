package com.example.health.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.example.health.api.common.PageUtils;
import com.example.health.api.common.R;
import com.example.health.api.db.pojo.CustomerImEntity;
import com.example.health.api.db.pojo.UserEntity;
import com.example.health.api.mis.controller.form.*;
import com.example.health.api.mis.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mis/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/login")
    public R login (@RequestBody @Valid LoginForm form){
        Map param= BeanUtil.beanToMap(form);
        Integer userid=userService.login(param);
        //判断userid
        if(userid!=null){
            //实现同端互斥功能 原来的令牌失效
            StpUtil.logout(userid,"Web");
            StpUtil.login(userid,"Web");
            String token =StpUtil.getTokenValueByLoginId(userid,"Web");
            //通过重写的方法获取权限列表
            List<String> permissions=StpUtil.getPermissionList();
            return R.ok()
                    .put("result",true)
                    .put("token",token)
                    .put("permissions",permissions);
        }
        else{
            return R.ok().put("result",false);
        }
    }

    @GetMapping("/logout")
    @SaCheckLogin
    public R logout(){
        int userId=StpUtil.getLoginIdAsInt();//取出登陆的id
        //销毁令牌
        StpUtil.logout(userId,"Web");
        return R.ok();

    }

    @PostMapping("updatePassword")
    @SaCheckLogin
    public R updatePassword(@RequestBody @Valid UpdatePasswordForm form){
        int userId=StpUtil.getLoginIdAsInt();//获取当前登陆的用户id
        HashMap map=new HashMap<>();
        map.put("userId",userId);
        map.put("password",form.getPassword());
        map.put("newPassword",form.getNewPassword());
        int resultRows=userService.updatePassword(map);
        return  R.ok().put("rows",resultRows);

    }

    @PostMapping("/searchPage")
    @SaCheckPermission(value = {"ROOT","USER:SELECT"},mode = SaMode.OR)
    public R searchPage(@RequestBody  @Valid SearchUserByPageForm pageForm){
        //获取分页起始 页长度
            int page=pageForm.getPage();
            int length=pageForm.getLength();
            int start =(page-1)*length;
            Map param=BeanUtil.beanToMap(pageForm);
            param.put("start",start);
            PageUtils pageUtils=userService.searchByPage(param);
            return R.ok().put("page",pageUtils);
    }

    @PostMapping("/insertUser")
    @SaCheckPermission(value = {"ROOT","USER:INSERT"},mode =SaMode.OR)
    public R insertUser(@Valid @RequestBody InsertUserForm form){
        //经验证后转为pojo对象
        UserEntity userEntity=BeanUtil.toBean(form,UserEntity.class);
        //设置为在职状态
        userEntity.setStatus(1);
        String roleJson = JSONUtil.toJsonStr(form.getRole());//将form里的Integer[]字段转换为json字符串
        userEntity.setRole(roleJson);
        int rows=userService.insert(userEntity);
        return R.ok().put("rows",rows);
    }

    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT","USER:SELECT"},mode =SaMode.OR)
    public  R searchById(@Valid @RequestBody SearchUserByIdForm form){
        HashMap map=userService.searchById(form.getUserId());
        return R.ok().put("result",map);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT","USER:UPDATE"},mode =SaMode.OR)
    public  R update(@Valid @RequestBody UpdateUserForm form){
        Map map= BeanUtil.beanToMap(form);
        String roleJson = JSONUtil.toJsonStr(form.getRole());
        map.replace("role",roleJson);
        int rows=userService.update(map);
        if(rows==1){
            StpUtil.logout(form.getUserId());
        }
        return R .ok().put("rows",rows);
    }

    @PostMapping("/deleteUserByIds")
    @SaCheckPermission(value = {"ROOT","USER:UPDATE"},mode =SaMode.OR)
    public  R deleteUserByIds(@Valid @RequestBody DeleteUserByIdsForm form){
        //不能删除自己的用户
        Integer myId=StpUtil.getLoginIdAsInt();
        if(ArrayUtil.contains(form.getIds(),myId)){
            return R.error("你不能删除自己的账户");
        }
        int rows=userService.deleteUserByIds(form.getIds());
        if(rows>0){
            for(Integer id:form.getIds()){
                StpUtil.logout(id);
            }
        }
        return R.ok().put("rows",rows);
    }

    @PostMapping("/dismiss")
    @SaCheckPermission(value = {"ROOT","USER:UPDATE"},mode =SaMode.OR)
    public R dissmiss(@RequestBody @Valid DismissForm form){
        int userId= form.getUserId();
        int rows=userService.dismisss(userId);
        return R.ok().put("rows",rows);
    }


    @GetMapping("/searchDoctorById")
    @SaCheckLogin
    public R searchDoctorById() {
        Integer userId = StpUtil.getLoginIdAsInt();
        HashMap map = userService.searchDoctorById(userId);
        return R.ok().put("result", map);
    }







}
