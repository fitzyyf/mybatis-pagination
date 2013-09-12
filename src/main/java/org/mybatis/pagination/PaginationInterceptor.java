/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package org.mybatis.pagination;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.pagination.dialect.DBMS;
import org.mybatis.pagination.dialect.Dialect;
import org.mybatis.pagination.dialect.DialectClient;
import org.mybatis.pagination.dto.datatables.PagingCriteria;
import org.mybatis.pagination.dto.datatables.SearchField;
import org.mybatis.pagination.dto.datatables.SortField;
import org.mybatis.pagination.helpers.Reflections;
import org.mybatis.pagination.helpers.SqlHelper;
import org.mybatis.pagination.helpers.StringHelper;

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
@Intercepts({@Signature(
        type = Executor.class,
        method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PaginationInterceptor implements Interceptor, Serializable {
    /** serial Version */
    private static final long serialVersionUID = -6075937069117597841L;
    private static final ThreadLocal<Integer> PAGINATION_TOTAL = new ThreadLocal<Integer>();
    private static final ThreadLocal<PagingCriteria> PAGE_REQUEST = new ThreadLocal<PagingCriteria>();
    /** logging */
    private static final Log log = LogFactory.getLog(PaginationInterceptor.class);
    /** mapped statement parameter index. */
    private static final int MAPPED_STATEMENT_INDEX = 0;
    /** sql id , in the mapper xml file. */
    private static String _sql_regex = "[*]";
    /** DataBase dialect. */
    protected Dialect _dialect;

    /**
     * Gets pagination total.
     *
     * @return the pagination total
     */
    public static int getPaginationTotal() {
        if (PAGINATION_TOTAL.get() == null) {
            return 0;
        }
        return PAGINATION_TOTAL.get();
    }

    /**
     * Gets page request.
     *
     * @return the page request
     */
    public static PagingCriteria getPageRequest() {
        return PAGE_REQUEST.get();
    }

    /** clear total context. */
    public static void clean() {
        PAGE_REQUEST.remove();
        PAGINATION_TOTAL.remove();
    }

    /**
     * Set the paging information,to RowBuounds.
     *
     * @param rowBounds rowBounds.
     * @return rowBounds.
     */
    private static RowBounds offset_paging(RowBounds rowBounds, PagingCriteria pageRequest) {
        // rowBuounds has offset.
        if (rowBounds.getOffset() == RowBounds.NO_ROW_OFFSET) {
            if (pageRequest != null) {
                return new RowBounds(pageRequest.getDisplayStart(), pageRequest.getDisplaySize());
            }
        }
        return rowBounds;
    }

    private static String sortAndFilterSql(String sql, PagingCriteria pagingCriteria, BoundSql boundSql) {

        boolean order = SqlHelper.containOrder(sql);
        final List<SearchField> searchFields = pagingCriteria.getSearchFields();
        if (searchFields != null && !searchFields.isEmpty()) {
            List<String> where_field = Lists.newArrayList();
            for (SearchField searchField : searchFields) {
                //TODO regex and pre
                where_field.add(searchField.getField() + StringHelper.LIKE_CHAR + "'%" + searchField.getValue() + "%'");
            }
            boolean where = SqlHelper.containWhere(sql);
            String orderBy = StringHelper.EMPTY;
            if (order) {
                String[] sqls = sql.split(SqlHelper.ORDER_REGEX);
                sql = sqls[0];
                orderBy = SqlHelper.SQL_ORDER + sqls[1];
            }
            sql = String.format((where ? SqlHelper.OR_SQL_FORMAT : SqlHelper.WHERE_SQL_FORMAT), sql
                    , Joiner.on(SqlHelper.OR_JOINER).join(where_field), orderBy);
        }

        final List<SortField> sortFields = pagingCriteria.getSortFields();
        if (sortFields != null && !sortFields.isEmpty()) {
            List<String> field_order = Lists.newArrayList();
            for (SortField sortField : sortFields) {
                field_order.add(sortField.getField() + StringHelper.BLANK_CHAR + sortField.getDirection().getDirection());
            }
            return String.format(order ? SqlHelper.SQL_FORMAT : SqlHelper.ORDER_SQL_FORMAT, sql
                    , Joiner.on(StringHelper.DOT_CHAR).join(field_order));
        }

        return sql;
    }

    /**
     * perform paging intercetion.
     *
     * @param queryArgs Executor.query params.
     */
    private void processIntercept(final Object[] queryArgs) {
        final MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
        final Object parameter = queryArgs[1];

        //the need for paging intercept.
        boolean interceptor = ms.getId().matches(_sql_regex);
        //obtain paging information.
        final PagingCriteria pageRequest = interceptor
                ? PagingParametersFinder.instance.findCriteria(parameter)
                : PagingCriteria.getDefaultCriteria();
        PAGE_REQUEST.set(pageRequest);

        final RowBounds oldRow = (RowBounds) queryArgs[2];
        final RowBounds rowBounds = (interceptor) ? offset_paging(oldRow, pageRequest) : oldRow;
        int offset = rowBounds.getOffset();
        int limit = rowBounds.getLimit();

        if (_dialect.supportsLimit() && (offset != RowBounds.NO_ROW_OFFSET || limit != RowBounds.NO_ROW_LIMIT)) {
            final BoundSql boundSql = ms.getBoundSql(parameter);
            String sql = boundSql.getSql().trim();

            Connection connection = null;
            try {
                //get connection
                connection = ms.getConfiguration().getEnvironment().getDataSource().getConnection();
                int count = SqlHelper.getCount(sql, connection, ms, parameter, boundSql, _dialect);
                PAGINATION_TOTAL.set(count);
            } catch (SQLException e) {
                log.error("The total number of access to the database failure.", e);
            } finally {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    log.error("Close the database connection error.", e);
                }
            }
            String new_sql = sortAndFilterSql(sql, pageRequest, boundSql);
            if (_dialect.supportsLimit()) {
                new_sql = _dialect.getLimitString(new_sql, offset, limit);
                offset = RowBounds.NO_ROW_OFFSET;
            } else {
                new_sql = _dialect.getLimitString(new_sql, 0, limit);
            }
            if (log.isDebugEnabled()) {
                log.debug("pagination sql is :[" + new_sql + "]");
            }
            limit = RowBounds.NO_ROW_LIMIT;

            queryArgs[2] = new RowBounds(offset, limit);

            BoundSql newBoundSql = copyFromBoundSql(ms, boundSql, new_sql);

            MappedStatement newMs = copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
            queryArgs[MAPPED_STATEMENT_INDEX] = newMs;
        }
    }

    private static BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql,
                                             String sql) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    //see: MapperBuilderAssistant
    private static MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        String[] keyProperties = ms.getKeyProperties();
        builder.keyProperty(keyProperties == null ? null : keyProperties[0]);

        //setStatementTimeout()
        builder.timeout(ms.getTimeout());

        //setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        //setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        //setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        processIntercept(invocation.getArgs());
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object o) {

        if (Executor.class.isAssignableFrom(o.getClass())) {
            return Plugin.wrap(new PaginationExecutor((Executor) o), this);
        }
        return Plugin.wrap(o, this);

    }

    /**
     * 设置属性，支持自定义方言类和制定数据库的方式
     * <p>
     * <code>dialectClass</code>,自定义方言类。可以不配置这项
     * <ode>dbms</ode> 数据库类型，插件支持的数据库
     * <code>sqlRegex</code> 需要拦截的SQL ID
     * </p>
     * 如果同时配置了<code>dialectClass</code>和<code>dbms</code>,则以<code>dbms</code>为主
     *
     * @param p 属性
     */
    @Override
    public void setProperties(Properties p) {
        String dialectClass = p.getProperty("dialectClass");
        DBMS dbms;
        if (StringHelper.isEmpty(dialectClass)) {
            String dialect = p.getProperty("dbms");
            Preconditions.checkArgument(!StringHelper.isEmpty(dialect), "dialect property is not found!");
            dbms = DBMS.valueOf(dialect.toUpperCase());
            Preconditions.checkNotNull(dbms, "plugin not super on this database.");
        } else {
            Dialect dialect1 = (Dialect) Reflections.instance(dialectClass);
            Preconditions.checkNotNull(dialect1, "dialectClass is not found!");
            DialectClient.putEx(dialect1);
            dbms = DBMS.EX;
        }


        _dialect = DialectClient.getDbmsDialect(dbms);

        String sql_regex = p.getProperty("sqlRegex");
        if (!StringHelper.isEmpty(sql_regex)) {
            _sql_regex = sql_regex;
        }
        clean();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
