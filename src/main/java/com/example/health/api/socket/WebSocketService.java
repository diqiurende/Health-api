package com.example.health.api.socket;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.health.api.config.sa_token.StpCustomerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint(value = "/socket") // WebSocket 的连接地址为 /socket
@Component // 声明为 Spring 管理的组件
public class WebSocketService {

    // 存储所有已连接用户的 WebSocket 会话信息，key 为用户 ID，value 为会话对象
    public static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 客户端建立连接时触发
     */
    @OnOpen
    public void onOpen(Session session) {
        // 当前没有需要处理的逻辑，可以用来记录连接日志
    }

    /**
     * 客户端断开连接时触发
     */
    @OnClose
    public void onClose(Session session) {
        // 获取会话属性中保存的用户信息
        Map map = session.getUserProperties();
        if (map.containsKey("userId")) {
            String userId = MapUtil.getStr(map, "userId");
            // 从在线会话 Map 中移除该用户的会话
            sessionMap.remove(userId);
        }
    }

    /**
     * 客户端发送消息时触发
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 解析接收到的 JSON 字符串
        JSONObject json = JSONUtil.parseObj(message);

        // 获取消息中的操作类型
        String opt = json.getStr("opt");
        // 用户身份类型：customer 或 user
        String identity = json.getStr("identity");
        // 前端传来的登录 token
        String token = json.getStr("token");

        // 根据身份和 token 获取用户 ID
        String userId;
        if ("customer".equals(identity)) {
            userId = "customer_" + StpCustomerUtil.getLoginIdByToken(token).toString();
        } else {
            userId = "user_" + StpUtil.getLoginIdByToken(token).toString();
        }

        // 将用户 ID 保存在当前会话中
        Map map = session.getUserProperties();
        map.put("userId", userId);

        // 如果 sessionMap 已有该用户，则更新；否则新增
        if (sessionMap.containsKey(userId)) {
            sessionMap.replace(userId, session);
        } else {
            sessionMap.put(userId, session);
        }

        // 如果是心跳请求 "ping"，则不需要任何操作，直接返回
        if ("ping".equals(opt)) {
            return;
        }


    }

    /**
     * 发生异常时触发
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 发生错误", error);
    }

    /**
     * 向某个指定用户发送消息（由后台主动推送）
     */
    public static void sendInfo(String message, String userId) {
        if (StrUtil.isNotBlank(userId) && sessionMap.containsKey(userId)) {
            Session session = sessionMap.get(userId);
            sendMessage(message, session);
        }
    }

    /**
     * 向指定的 WebSocket 会话发送消息
     */
    private static void sendMessage(String message, Session session) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("发送 WebSocket 消息失败", e);
        }
    }
}
