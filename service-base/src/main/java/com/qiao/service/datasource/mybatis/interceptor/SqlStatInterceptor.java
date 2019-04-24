package com.qiao.service.datasource.mybatis.interceptor;

import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @Description:
 * @Created by ql on 2019/4/24/024 22:12
 * @Version: v1.0
 */
@Intercepts({@Signature(
        method = "batch",
        type = StatementHandler.class,
        args = {Statement.class}
), @Signature(
        method = "update",
        type = StatementHandler.class,
        args = {Statement.class}
), @Signature(
        method = "query",
        type = StatementHandler.class,
        args = {Statement.class, ResultHandler.class}
)})
public class SqlStatInterceptor extends AbstractInterceptor {
    private static ThreadLocal<LinkedList<StatInfo>> sqlLocal = new ThreadLocal();
    private static final Logger logger = LoggerFactory.getLogger("sqlLogger");

    public SqlStatInterceptor() {
    }

    public static void initStat(String sql) {
        LinkedList<SqlStatInterceptor.StatInfo> list = (LinkedList)sqlLocal.get();
        if (list == null) {
            list = new LinkedList();
            sqlLocal.set(list);
        }

        list.push(new SqlStatInterceptor.StatInfo(sql));
    }

    public static void clearStat() {
        sqlLocal.remove();
    }

    public Object intercept(Invocation invocation) throws Throwable {
        boolean var9 = false;

        Logger var10000;
        Object result;
        try {
            var9 = true;
            result = invocation.proceed();
            var9 = false;
        } finally {
            if (var9) {
                LinkedList list = (LinkedList)sqlLocal.get();
                if (list != null) {
                    if (list.size() > 0) {
                        SqlStatInterceptor.StatInfo stat = (SqlStatInterceptor.StatInfo)list.pop();
                        var10000 = logger;
                        stat.getSql().info(System.currentTimeMillis() - stat.getStartLong());
                    }

                    if (list.size() == 0) {
                        clearStat();
                    }
                }

            }
        }

        LinkedList<SqlStatInterceptor.StatInfo> list = (LinkedList)sqlLocal.get();
        if (list != null) {
            if (list.size() > 0) {
                SqlStatInterceptor.StatInfo stat = (SqlStatInterceptor.StatInfo)list.pop();
                var10000 = logger;
                stat.getSql().info(System.currentTimeMillis() - stat.getStartLong());
            }

            if (list.size() == 0) {
                clearStat();
            }
        }

        return result;
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
    }

    public static class StatInfo {
        private String sql;
        private long startLong;

        public StatInfo(String sql) {
            this.sql = sql;
            this.startLong = System.currentTimeMillis();
        }

        public String getSql() {
            return this.sql;
        }

        public long getStartLong() {
            return this.startLong;
        }
    }
}
