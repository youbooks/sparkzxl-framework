package com.github.sparkzxl.lock.executor;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * description: 分布式锁zookeeper处理器
 *
 * @author zhouxinlei
 * @since 2022-05-03 10:04:37
 */
@Slf4j
@RequiredArgsConstructor
public class ZookeeperLockExecutor extends AbstractLockExecutor<InterProcessMutex> {

    private final CuratorFramework curatorFramework;

    @Override
    public InterProcessMutex acquire(String lockKey, String lockValue, long expire, long acquireTimeout) {
        if (!CuratorFrameworkState.STARTED.equals(curatorFramework.getState())) {
            log.warn("instance must be started before calling this method");
            return null;
        }

        String nodePath = "/curator/distributed-lock/%s";
        return Try.of(() -> {
            InterProcessMutex mutex = new InterProcessMutex(curatorFramework, String.format(nodePath, lockKey));
            final boolean locked = mutex.acquire(acquireTimeout, TimeUnit.MILLISECONDS);
            return obtainLockInstance(locked, mutex);
        }).getOrElseGet(throwable -> {
            log.error("Zookeeper lock failed：", throwable);
            return null;
        });
    }

    @Override
    public boolean releaseLock(String key, String value, InterProcessMutex lockInstance) {
        return Try.of(() -> {
            lockInstance.release();
            return true;
        }).getOrElseGet(throwable -> {
            log.error("zookeeper lock release error", throwable);
            return false;
        });
    }

}