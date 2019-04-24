package com.qiao.service.datasource.mybatis.interceptor;

import java.util.Properties;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import yhao.infra.service.datasource.mybatis.dialect.Dialect;
import yhao.infra.service.datasource.mybatis.interceptor.AbstractInterceptor.BoundSqlSqlSource;

/**
 * @Description:
 * @Created by ql on 2019/4/24/024 22:08
 * @Version: v1.0
 */
@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
)})
public class PaginationInterceptor extends AbstractInterceptor {
    private static int MAPPED_STATEMENT_INDEX = 0;
    private static int PARAMETER_INDEX = 1;
    private static int ROWBOUNDS_INDEX = 2;
    private Dialect dialect;

    public PaginationInterceptor() {
    }

    public Object intercept(Invocation invocation) throws Throwable {
        this.processIntercept(invocation.getArgs());
        return invocation.proceed();
    }

    private void processIntercept(final Object[] queryArgs) {
        MappedStatement ms = (MappedStatement)queryArgs[MAPPED_STATEMENT_INDEX];
        Object parameter = queryArgs[PARAMETER_INDEX];
        RowBounds rowBounds = (RowBounds)queryArgs[ROWBOUNDS_INDEX];
        int offset = rowBounds.getOffset();
        int limit = rowBounds.getLimit();
        BoundSql boundSql;
        String sql;
        MappedStatement newMs;
        if (!this.dialect.supportsLimit() || offset == 0 && limit == 2147483647) {
            if (parameter instanceof PaginationInterceptor.CountParameter) {
                parameter = ((PaginationInterceptor.CountParameter)parameter).getParameter();
                boundSql = ms.getBoundSql(parameter);
                sql = boundSql.getSql();
                sql = sql;

                try {
                    FieldUtils.writeDeclaredField(boundSql, "sql", sql, true);
                } catch (IllegalAccessException var10) {
                    ;
                }

                newMs = this.copyFromMappedStatement(ms, new BoundSqlSqlSource(boundSql), true);
                queryArgs[MAPPED_STATEMENT_INDEX] = newMs;
                queryArgs[PARAMETER_INDEX] = parameter;
            }
        } else {
            boundSql = ms.getBoundSql(parameter);
            sql = boundSql.getSql().trim();
            if (this.dialect.supportsLimitOffset()) {
                sql = this.dialect.getLimitString(sql, offset, limit);
                offset = 0;
            } else {
                sql = this.dialect.getLimitString(sql, 0, limit);
            }

            limit = 2147483647;
            queryArgs[ROWBOUNDS_INDEX] = new RowBounds(offset, limit);

            try {
                FieldUtils.writeDeclaredField(boundSql, "sql", sql, true);
            } catch (IllegalAccessException var11) {
                ;
            }

            newMs = this.copyFromMappedStatement(ms, new BoundSqlSqlSource(boundSql), false);
            queryArgs[MAPPED_STATEMENT_INDEX] = newMs;
        }

    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
        String dialectClass = properties.getProperty("dialectClass");

        try {
            this.dialect = (Dialect)Class.forName(dialectClass).newInstance();
        } catch (Exception var4) {
            throw new RuntimeException(dialectClass, var4);
        }
    }

    public static class CountParameter {
        private Object parameter;

        public CountParameter(Object parameter) {
            this.parameter = parameter;
        }

        public Object getParameter() {
            return this.parameter;
        }
    }
}
