/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package org.mybatis.pagination.helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.mybatis.pagination.dialect.Dialect;
import org.springframework.util.Assert;

/**
 * <p>
 * .
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 上午11:30
 * @since JDK 1.5
 */
public class SqlHelper {

    public static final String SQL_ORDER = " order by ";

    public static final String OR_JOINER = " or ";


    public static final String  OR_SQL_FORMAT       = "%s or (%s) %s";
    public static final String  WHERE_SQL_FORMAT    = "%s where (%s) %s";
    public static final String  SQL_FORMAT          = "%s, %s";
    public static final String  ORDER_SQL_FORMAT    = "%s order by %s";

    /** Order by 正则表达式 */
    public static final String ORDER_BY_REGEX       = "order\\s*by[\\w|\\W|\\s|\\S]*";
    /** sql contains whre regex. */
    public static final String WHERE_REGEX          = "\\s+where\\s+";
    /** sql contains <code>order by </code> regex. */
    public static final String ORDER_REGEX          = "order\\s+by";
    /** Xsql Order by 正则表达式 */
    public static final String XSQL_ORDER_BY_REGEX  = "/~.*order\\s*by[\\w|\\W|\\s|\\S]*~/";
    /** From正则表达式 */
    public static final String FROM_REGEX           = "\\sfrom\\s";
    /** logging */
    private static final Log LOG = LogFactory.getLog(SqlHelper.class);

    /**
     * 查询总纪录数
     *
     * @param sql             SQL语句
     * @param connection      数据库连接
     * @param mappedStatement mapped
     * @param parameterObject 参数
     * @param boundSql        boundSql
     * @param dialect         database dialect
     * @return 总记录数
     * @throws java.sql.SQLException sql查询错误
     */
    public static int getCount(final String sql, final Connection connection,
                               final MappedStatement mappedStatement, final Object parameterObject,
                               final BoundSql boundSql, Dialect dialect) throws SQLException {
        final String count_sql = dialect.getCountString(sql);
        if (LOG.isDebugEnabled()) {
            LOG.debug("the pagination generate count sql  is [" + count_sql + "]");
        }
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = connection.prepareStatement(count_sql);
            final BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(), count_sql,
                    boundSql.getParameterMappings(), parameterObject);
            SqlHelper.setParameters(countStmt, mappedStatement, countBS, parameterObject);
            rs = countStmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            return count;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (countStmt != null) {
                countStmt.close();
            }
        }
    }

    /**
     * 对SQL参数(?)设值
     *
     * @param ps              表示预编译的 SQL 语句的对象。
     * @param mappedStatement MappedStatement
     * @param boundSql        SQL
     * @param parameterObject 参数对象
     * @throws java.sql.SQLException 数据库异常
     */
    @SuppressWarnings("unchecked")
    public static void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql, Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null :
                    configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    private static int indexOfByRegex(String input, String regex) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        if (m.find()) {
            return m.start();
        }
        return -1;
    }

    /**
     * 去除select 子句，未考虑union的情况
     *
     * @param sql sql
     * @return 删除掉的selcet的子句
     */
    public static String removeSelect(String sql) {
        Assert.hasText(sql);
        int beginPos = indexOfByRegex(sql.toLowerCase(), FROM_REGEX);
        Assert.isTrue(beginPos != -1, " sql : " + sql + " must has a keyword 'from'");
        return sql.substring(beginPos);
    }

    /**
     * 去除orderby 子句
     *
     * @param sql sql
     * @return 去掉order by sql
     */
    public static String removeOrders(String sql) {
        Assert.hasText(sql);
        Pattern p = Pattern.compile(ORDER_BY_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, StringHelper.EMPTY);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static boolean containOrder(String sql) {
        return containRegex(sql, ORDER_REGEX);
    }

    public static boolean containWhere(String sql) {
        return containRegex(sql, WHERE_REGEX);
    }

    public static String removeFetchKeyword(String sql) {
        return sql.replaceAll("(?i)fetch", "");
    }

    public static boolean containRegex(String sql, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        return matcher.find();
    }

    public static String removeXsqlBuilderOrders(String string) {
        Assert.hasText(string);
        Pattern p = Pattern.compile(XSQL_ORDER_BY_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(string);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return removeOrders(sb.toString());
    }
}
