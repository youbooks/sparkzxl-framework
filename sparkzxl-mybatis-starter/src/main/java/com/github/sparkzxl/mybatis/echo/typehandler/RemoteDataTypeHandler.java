package com.github.sparkzxl.mybatis.echo.typehandler;

import com.github.sparkzxl.entity.data.RemoteData;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

/**
 * description: 远程数据 类型转换器
 * <p>
 * 借鉴Hibernate的外键字段的思想，自定义一个类型处理器，
 * 将数据库中 bigint、int、varchar类型的字段映射成JavaBean中 RemoteData 类型的字段
 *
 * @author zhouxinlei
 */
public class RemoteDataTypeHandler extends BaseTypeHandler<RemoteData> {
    /**
     * insert 、update 时执行该方法
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, RemoteData parameter, JdbcType jdbcType)
            throws SQLException {
        if (parameter != null && parameter.getKey() != null) {
            if (parameter.getKey() instanceof String) {
                ps.setString(i, (String) parameter.getKey());
            } else if (parameter.getKey() instanceof Long) {
                ps.setLong(i, (Long) parameter.getKey());
            } else if (parameter.getKey() instanceof Integer) {
                ps.setInt(i, (Integer) parameter.getKey());
            } else {
                ps.setObject(i, parameter.getKey());
            }
        } else {
            ps.setNull(i, Types.BIGINT);
        }
    }

    @Override
    public RemoteData getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object key = rs.getObject(columnName);
        return build(key);
    }

    @Override
    public RemoteData getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object key = rs.getObject(columnIndex);
        return build(key);
    }

    @Override
    public RemoteData getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object key = cs.getObject(columnIndex);
        return build(key);
    }

    private RemoteData build(Object key) {
        if (key == null) {
            return new RemoteData();
        }
        return new RemoteData(key);
    }

}
