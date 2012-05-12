package org.noo.dialect.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.plugin.Interceptor;
import org.noo.dialect.dialect.Dialect;
import org.noo.dialect.dialect.DialectClient;
import org.noo.dialect.page.DBMS;
import org.noo.dialect.uitls.Reflections;

import javax.xml.bind.PropertyException;
import java.io.Serializable;
import java.util.Properties;

/**
 * <p>
 * .
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-12 上午10:21
 * @since JDK 1.5
 */
public abstract class BaseInterceptor implements Interceptor, Serializable {
    /**
     * 日志
     */
    protected Log log=LogFactory.getLog(this.getClass());;


    protected static final String DELEGATE = "delegate";

    protected static final String MAPPED_STATEMENT = "mappedStatement";


    /**
     * 默认Mysql 数据库分页方言
     */
    protected static final ThreadLocal<DBMS> DBMS_THREAD_LOCAL = new ThreadLocal<DBMS>() {
        @Override
        protected DBMS initialValue() {
            return DBMS.MYSQL;
        }
    };
    /**
     * 拦截的ID，在mapper中的id，可以匹配正则
     */
    protected static final ThreadLocal<String> SQL_PATTERN = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "";
        }
    };
    private static final long serialVersionUID = 4596430444388728543L;



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
        DBMS_THREAD_LOCAL.set(DBMS.valueOf(dialect.toUpperCase()));
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
