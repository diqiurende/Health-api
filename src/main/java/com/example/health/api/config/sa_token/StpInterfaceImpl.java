package com.example.health.api.config.sa_token;

import cn.dev33.satoken.stp.StpInterface;
import com.example.health.api.db.dao.UserMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 自定义权限加载接口实现类
 */
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Resource
    UserMapper userMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {

        List<String> list = new ArrayList<>();
        int userId = Integer.parseInt(loginId.toString());
        Set<String> set = userMapper.searchUserPermissions(userId);
        list.addAll(set);
        return list;
    }

    /**
     * 返回一个用户所拥有的角色标识集合  不通过角色（修改频繁）来鉴权
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        ArrayList<String> list = new ArrayList();
        return list;

    }

}
