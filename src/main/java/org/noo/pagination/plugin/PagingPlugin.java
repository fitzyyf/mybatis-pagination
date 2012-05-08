/*
 * Copyright (c) 2010-2011 NOO. All Rights Reserved.
 * [Id:PagingPlugin.java  2011-11-18 下午12:31 poplar.yfyang ]
 */
package org.noo.pagination.plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.noo.pagination.annotation.Paging;
import org.noo.pagination.dialect.Dialect;
import org.noo.pagination.dialect.DialectClient;
import org.noo.pagination.model.DBMS;
import org.noo.pagination.model.Pagetag;
import org.noo.pagination.uitls.ClassUtils;
import org.noo.pagination.uitls.Reflections;

import javax.xml.bind.PropertyException;
import java.io.Serializable;
import java.lang.reflect.Field;
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
public class PagingPlugin implements Interceptor, Serializable {
    /**
     * 序列化
     */
    private static final long serialVersionUID = -6075937069117597841L;


    /**
     * 日志
     */
    private static final Log log = LogFactory.getLog(PagingPlugin.class);

    private static final String DELEGATE = "delegate";

    private static final String MAPPED_STATEMENT = "mappedStatement";
    /**
     * 默认Mysql 数据库分页方言
     */
    private static final ThreadLocal<DBMS> DBMS_THREAD_LOCAL = new ThreadLocal<DBMS>() {
        @Override
        protected DBMS initialValue() {
            return DBMS.MYSQL;
        }
    };
    /**
     * 拦截的ID，在mapper中的id，可以匹配正则
     */
    private static final ThreadLocal<String> SQL_PATTERN = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "";
        }
    };

    @Override
    public Object intercept(Invocation ivk) throws Throwable {
        if (ivk.getTarget().getClass().isAssignableFrom(RoutingStatementHandler.class)) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler) Reflections.getFieldValue(statementHandler, DELEGATE);
            MappedStatement mappedStatement = (MappedStatement) Reflections.getFieldValue(delegate, MAPPED_STATEMENT);

            if (mappedStatement.getId().matches(SQL_PATTERN.get())) { //拦截需要分页的SQL
                BoundSql boundSql = delegate.getBoundSql();
                //分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
                Object parameterObject = boundSql.getParameterObject();
                if (parameterObject == null) {
                    log.error("参数未实例化");
                    throw new NullPointerException("parameterObject尚未实例化！");
                } else {
                    Connection connection = (Connection) ivk.getArgs()[0];
                    String sql = boundSql.getSql();
                    //记录统计
                    int count = SQLHelp.getCount(sql, connection, mappedStatement, parameterObject, boundSql);
                    Pagetag page;
                    //参数就是Paging实体
                    if (ClassUtils.equalObject(parameterObject, Pagetag.class)) {
                        page = (Pagetag) parameterObject;
                        page.setTotal(count);
                    } else {
                        //参数为某个实体，该实体拥有Page属性
                        Paging paging = parameterObject.getClass().getAnnotation(Paging.class);
                        String field = paging.field();
                        Field pageField = Reflections.getAccessibleField(parameterObject, field);
                        if (pageField != null) {
                            page = (Pagetag) Reflections.getFieldValue(parameterObject, field);
                            if (page == null)
                                page = new Pagetag();
                            page.setTotal(count);
                            //通过反射，对实体对象设置分页对象
                            Reflections.setFieldValue(parameterObject, field, page);
                        } else {
                            throw new NoSuchFieldException(parameterObject.getClass().getName()
                                    + "不存在 page 属性！");
                        }
                    }
                    //将分页sql语句反射回BoundSql.
                    Reflections.setFieldValue(boundSql, "sql", SQLHelp.generatePageSql(sql, page, DBMS_THREAD_LOCAL.get()));
                }
            }
        }
        return ivk.proceed();
    }


    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }


    /**
     * 设置属性，支持自定义方言类和制定数据库的方式
     * <p>
     * <code>dialectClass</code>,自定义方言类。可以不配置这项
     * <ode>dbms</ode> 数据库类型，插件支持的数据库
     * <code>sqlPattern</code> 需要拦截的SQL ID
     * </p>
     * 如果同时配置了<code>dialectClass</code>和<code>dbms</code>,则以<code>dbms</code>为主
     *
     * @param p 属性
     */
    @Override
    public void setProperties(Properties p) {
        String dialectClass = p.getProperty("dialectClass");
        if (!StringUtils.isEmpty(dialectClass)) {
            Dialect dialect1 = (Dialect) Reflections.instance(dialectClass);
            if (dialect1 == null) {
                throw new NullPointerException("方言实例错误");
            }
            DialectClient.putEx(dialect1);
            DBMS_THREAD_LOCAL.set(DBMS.EX);
        }

        String dialect = p.getProperty("dbms");
        if (StringUtils.isEmpty(dialect)) {
            try {
                throw new PropertyException("dialect property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
        DBMS_THREAD_LOCAL.set(DBMS.valueOf(dialect));
        if (DBMS_THREAD_LOCAL.get() == null) {
            try {
                throw new PropertyException("插件无法支持该数据库");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }

        SQL_PATTERN.set(p.getProperty("sqlPattern"));
        if (StringUtils.isEmpty(SQL_PATTERN.get())) {
            try {
                throw new PropertyException("sqlPattern property is not found!");
            } catch (PropertyException e) {
                e.printStackTrace();
            }
        }
    }
}
