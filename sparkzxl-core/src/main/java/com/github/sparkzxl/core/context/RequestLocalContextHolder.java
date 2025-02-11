package com.github.sparkzxl.core.context;

import cn.hutool.core.convert.Convert;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.util.StrPool;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * description: 获取当前域中的 用户id, 用户昵称
 * 注意： 用户id 和 用户昵称必须在前端 通过请求头的方法传入。 否则这里无法获取
 *
 * @author zhouxinlei
 */
public class RequestLocalContextHolder {

    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void set(String key, Object value) {
        Map<String, Object> map = getLocalMap();
        map.put(key, value == null ? StrPool.EMPTY : value);
    }

    public static <T> T get(String key, Class<T> type) {
        Map<String, Object> map = getLocalMap();
        return Convert.convert(type, map.get(key));
    }

    public static <T> T get(String key, Class<T> type, Object def) {
        Map<String, Object> map = getLocalMap();
        return Convert.convert(type, map.getOrDefault(key, String.valueOf(def == null ? StrPool.EMPTY : def)));
    }

    public static String get(String key) {
        Map<String, Object> map = getLocalMap();
        return Convert.toStr(map.getOrDefault(key, StrPool.EMPTY));
    }

    public static Map<String, Object> getLocalMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = Maps.newHashMap();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

    public static void setLocalMap(Map<String, Object> threadLocalMap) {
        THREAD_LOCAL.set(threadLocalMap);
    }


    public static <T> T getUserId(Class<T> tClass) {
        return get(BaseContextConstants.JWT_KEY_USER_ID, tClass, 0L);
    }

    public static <T> void setUserId(T userId) {
        set(BaseContextConstants.JWT_KEY_USER_ID, userId);
    }

    /**
     * 账号表中的name
     *
     * @return String
     */
    public static String getAccount() {
        return get(BaseContextConstants.JWT_KEY_ACCOUNT, String.class);
    }

    /**
     * 账号表中的name
     *
     * @param account 账户
     */
    public static void setAccount(String account) {
        set(BaseContextConstants.JWT_KEY_ACCOUNT, account);
    }


    /**
     * 获取登录的用户姓名
     *
     * @return String
     */
    public static String getName() {
        return get(BaseContextConstants.JWT_KEY_NAME, String.class);
    }

    /**
     * 登录的账号
     *
     * @param name 用户姓名
     */
    public static void setName(String name) {
        set(BaseContextConstants.JWT_KEY_NAME, name);
    }

    /**
     * 获取用户token
     *
     * @return String
     */
    public static String getToken() {
        return get(BaseContextConstants.JWT_TOKEN_HEADER, String.class);
    }

    public static void setToken(String token) {
        set(BaseContextConstants.JWT_TOKEN_HEADER, token);
    }

    public static String getTenant() {
        return get(BaseContextConstants.TENANT_ID, String.class, StrPool.EMPTY);
    }

    public static void setTenant(String val) {
        set(BaseContextConstants.TENANT_ID, val);
    }

    public static String getVersion() {
        return get(BaseContextConstants.VERSION, String.class);
    }

    public static void setVersion(String val) {
        set(BaseContextConstants.VERSION, val);
    }

    public static String getClientId() {
        return get(BaseContextConstants.CLIENT_ID, String.class);
    }

    public static void setClientId(String val) {
        set(BaseContextConstants.CLIENT_ID, val);
    }

    public static boolean getBoot() {
        return get(BaseContextConstants.IS_BOOT, Boolean.class, false);
    }

    /**
     * 账号id
     *
     * @param val 是否boot
     */
    public static void setBoot(Boolean val) {
        set(BaseContextConstants.IS_BOOT, val);
    }


    public static void remove() {
        THREAD_LOCAL.remove();
    }

    public static <T> List<T> getList(String key, Class<T> type) {
        Map<String, Object> map = getLocalMap();
        Object o = map.get(key);
        if (o == null) {
            return Collections.emptyList();
        }
        List<T> dataList = Lists.newArrayList();
        if (o instanceof Collection) {
            List<Object> list = (List<Object>) o;
            for (Object val : list) {
                dataList.add(Convert.convert(type, val));
            }
        } else {
            List<Object> list = Lists.newArrayList(o);
            for (Object val : list) {
                dataList.add(Convert.convert(type, val));
            }
        }
        return dataList;
    }
}
