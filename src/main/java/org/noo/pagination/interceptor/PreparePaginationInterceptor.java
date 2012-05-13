/*
 * Copyright (c) 2010-2011 NOO. All Rights Reserved.
 * [Id:PagingPlugin.java  2011-11-18 下午12:31 poplar.yfyang ]
 */
package org.noo.pagination.interceptor;

import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.noo.pagination.page.Page;
import org.noo.pagination.uitls.Reflections;

import java.sql.Connection;
import java.util.Properties;

/**
 * <p>
 * Mybatis数据库分页插件.
 * 拦截StatementHandler的prepare方法
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2011-11-18 下午12:31
 * @since JDK 1.5
 */
@SuppressWarnings("UnusedDeclaration")
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})
})
public class PreparePaginationInterceptor extends BaseInterceptor {
    /**
     * 序列化
     */
    private static final long serialVersionUID = -6075937069117597841L;

    public PreparePaginationInterceptor() {
        super();
    }

    @Override
    public Object intercept(Invocation ivk) throws Throwable {
        if (ivk.getTarget().getClass().isAssignableFrom(RoutingStatementHandler.class)) {
            final RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
            final BaseStatementHandler delegate = (BaseStatementHandler) Reflections.getFieldValue(statementHandler, DELEGATE);
            final MappedStatement mappedStatement = (MappedStatement) Reflections.getFieldValue(delegate, MAPPED_STATEMENT);

            if (mappedStatement.getId().matches(_SQL_PATTERN)) { //拦截需要分页的SQL
                BoundSql boundSql = delegate.getBoundSql();
                //分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
                Object parameterObject = boundSql.getParameterObject();
                if (parameterObject == null) {
                    log.error("参数未实例化");
                    throw new NullPointerException("parameterObject尚未实例化！");
                } else {
                    final Connection connection = (Connection) ivk.getArgs()[0];
                    final String sql = boundSql.getSql();
                    //记录统计
                    final int count = SQLHelp.getCount(sql, connection,
                            mappedStatement, parameterObject, boundSql);
                    Page page = null;
                    page = convertParameter(parameterObject, page);
                    page.init(count, page.getPageSize(), page.getCurrentPage());
                    String pagingSql = SQLHelp.generatePageSql(sql, page, DIALECT);
                    if (log.isDebugEnabled()) {
                        log.debug("分页SQL:" + pagingSql);
                    }
                    //将分页sql语句反射回BoundSql.
                    Reflections.setFieldValue(boundSql, "sql", pagingSql);
                }
            }
        }
        return ivk.proceed();
    }


    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {
        initProperties(properties);
    }
}
