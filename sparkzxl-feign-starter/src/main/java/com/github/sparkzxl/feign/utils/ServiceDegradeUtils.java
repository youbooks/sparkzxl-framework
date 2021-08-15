package com.github.sparkzxl.feign.utils;

import com.github.sparkzxl.constant.AppContextConstants;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;

/**
 * description: 降级处理工具类
 *
 * @author zhouxinlei
 */
public class ServiceDegradeUtils {

    public static void fallBack() {
        RequestContextHolderUtils.setAttribute(AppContextConstants.FALLBACK, Boolean.TRUE);
    }
}
