package com.github.sparkzxl.cache.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.Duration;

/**
 * description: 缓存 key 封装
 *
 * @author zhouxinlei
 * @since 2022-10-14 16:29:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheKey {
    /**
     * redis key
     */
    @NonNull
    private String key;
    /**
     * 超时时间 秒
     */
    private Duration expire;

    public CacheKey(final @NonNull String key) {
        this.key = key;
    }


}
