package com.example.health.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.example.health.api.common.R;
import com.example.health.api.db.pojo.CustomerImEntity;
import com.example.health.api.mis.controller.form.LoginForm;
import com.example.health.api.mis.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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


}
