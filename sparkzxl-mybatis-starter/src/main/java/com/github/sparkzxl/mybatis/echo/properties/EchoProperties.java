package com.github.sparkzxl.mybatis.echo.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: 回显配置类
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.DATA_ECHO_PREFIX)
public class EchoProperties {

    /**
     * 是否启用远程查询
     */
    private Boolean enabled = true;
    /**
     * 是否启用aop注解方式
     */
    private Boolean aopEnabled = true;

    /**
     * 字典类型 和 code 的分隔符
     */
    private String dictSeparator = "###";
    /**
     * 多个字典code 之间的的分隔符
     */
    private String dictItemSeparator = ",";

    /**
     * 递归最大深度
     */
    private Integer maxDepth = 3;

    /**
     * 本地缓存配置信息
     */
    private GuavaCache guavaCache = new GuavaCache();


    @Data
    public static class GuavaCache {
        /**
         * 是否启用本地缓存
         * <p>
         * 注意：本地缓存开启后，会存在短暂的数据不一致情况(由guavaCacheRefreshWriteTime决定)， 所以对数据正确性有要求的项目建议禁用，然后在@Echo.method 方法上执行添加redis等缓存！
         */
        private Boolean enabled = false;
        /**
         * guava缓存的 最大数
         */
        private Integer maximumSize = 1000;
        /**
         * guava更新缓存的下一次时间,分钟
         */
        private Integer refreshWriteTime = 2;
        /**
         * guava自动刷新缓存的线程数量
         */
        private Integer refreshThreadPoolSize = 10;
    }

}
