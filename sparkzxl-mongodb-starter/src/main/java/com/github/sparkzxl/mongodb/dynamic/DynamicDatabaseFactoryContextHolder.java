package com.github.sparkzxl.mongodb.dynamic;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * description: 全局动态数据源工厂切换
 *
 * @author zhouxinlei
 * @date 2021-09-02 16:13:50
 */
public class DynamicDatabaseFactoryContextHolder {

    /**
     * 为什么要用链表存储(准确的是栈)
     * <pre>
     * 为了支持嵌套切换，如ABC三个service都是不同的数据源
     * 其中A的某个业务要调B的方法，B的方法需要调用C的方法。一级一级调用切换，形成了链。
     * 传统的只设置当前线程的方式不能满足此业务需求，必须使用栈，后进先出。
     * </pre>
     */
    private static final ThreadLocal<Deque<MongoDatabaseFactory>> LOOKUP_KEY_HOLDER = new TransmittableThreadLocal<>(true);

    static {
        LOOKUP_KEY_HOLDER.set(new ArrayDeque<>());
    }

    private DynamicDatabaseFactoryContextHolder() {
    }

    /**
     * 获得当前线程数据源
     *
     * @return 数据源名称
     */
    public static MongoDatabaseFactory peek() {
        return LOOKUP_KEY_HOLDER.get().peek();
    }

    /**
     * 设置当前线程数据源
     * <p>
     * 如非必要不要手动调用，调用后确保最终清除
     * </p>
     *
     * @param databaseFactory 数据源工厂
     */
    public static MongoDatabaseFactory push(MongoDatabaseFactory databaseFactory) {
        LOOKUP_KEY_HOLDER.get().push(databaseFactory);
        return databaseFactory;
    }

    /**
     * 清空当前线程数据源
     * <p>
     * 如果当前线程是连续切换数据源 只会移除掉当前线程的数据源名称
     * </p>
     */
    public static void poll() {
        Deque<MongoDatabaseFactory> deque = LOOKUP_KEY_HOLDER.get();
        deque.poll();
        if (deque.isEmpty()) {
            LOOKUP_KEY_HOLDER.remove();
        }
    }

    /**
     * 强制清空本地线程
     * <p>
     * 防止内存泄漏，如手动调用了push可调用此方法确保清除
     * </p>
     */
    public static void clear() {
        LOOKUP_KEY_HOLDER.remove();
    }

}
