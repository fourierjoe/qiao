package com.qiao.service.datasource.mybatis.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import yhao.infra.common.ContextHolder;
import yhao.infra.service.datasource.usemaster.UseMasterContextKey;

/**
 * @Description:
 * @Created by ql on 2019/4/24/024 22:11
 * @Version: v1.0
 */
@Intercepts({@Signature(
        method = "prepare",
        type = StatementHandler.class,
        args = {Connection.class}
)})
public class MultiDataSourceInterceptor extends AbstractInterceptor {
    private String commonDatabase;
    private static final List<String> commonTables = new ArrayList();

    public MultiDataSourceInterceptor() {
    }

    public Object intercept(Invocation invocation) throws Throwable {
        RoutingStatementHandler statement = (RoutingStatementHandler)invocation.getTarget();
        BoundSql boundSql = statement.getBoundSql();
        String sql = boundSql.getSql();
        Connection con = (Connection)invocation.getArgs()[0];
        String preSql = sql.trim().substring(0, 6).toLowerCase();
        if ("select".equals(preSql)) {
            con.setReadOnly(true);
        }

        if (ContextHolder.getContextData(UseMasterContextKey.class) != null) {
            con.setReadOnly(false);
        }

        Object result;
        try {
            sql = this.appentDbNameForCommonTables(sql);
            FieldUtils.writeDeclaredField(boundSql, "sql", sql, true);
            result = invocation.proceed();
        } finally {
            con.setReadOnly(false);
        }

        String sqlStr = null;
        if (boundSql.getParameterObject() != null) {
            ObjectMapper mapper = new ObjectMapper();
            sqlStr = mapper.writeValueAsString(boundSql.getParameterObject());
        }

        SqlStatInterceptor.initStat(ContextHolder.getDataSource() + ":" + sql + ":" + sqlStr);
        return result;
    }

    private String appentDbNameForCommonTables(String sql) {
        if (this.commonDatabase != null && !this.commonDatabase.trim().equals("") && commonTables.size() != 0) {
            String table;
            for(Iterator var2 = commonTables.iterator(); var2.hasNext(); sql = table.replaceAll(this.commonDatabase, table)) {
                table = (String)var2.next();
                table = table.trim();
                char[] var = new char[]{'\n'};
                sql = sql.replaceAll(new String(var), " ");
                sql = table.replaceAll(this.commonDatabase, table);
            }

            return sql;
        } else {
            return sql;
        }
    }

    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
        String commonDataSource = properties.getProperty("commonDataSource");
        String commonDatabase = properties.getProperty("commonDatabase");
        String commonTables = properties.getProperty("commonTables");
        if (commonTables != null && !commonTables.trim().equals("")) {
            this.commonDatabase = commonDatabase;
            String[] var5 = commonTables.split(",");
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String table = var5[var7];
                commonTables.add(table);
            }

        }
    }

    public static List<String> getCommonTables() {
        List<String> out = new ArrayList();
        Iterator var1 = commonTables.iterator();

        while(var1.hasNext()) {
            String table = (String)var1.next();
            out.add(table);
        }

        return out;
    }
}
