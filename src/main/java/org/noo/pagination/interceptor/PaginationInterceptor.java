package org.noo.pagination.interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.noo.pagination.page.Page;
import org.noo.pagination.page.PageContext;

import java.sql.Connection;
import java.util.Properties;

/**
 * <p>
 * 数据库分页插件，只拦截查询语句.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-12 上午10:06
 * @since JDK 1.5
 */
@Intercepts({@Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class PaginationInterceptor extends BaseInterceptor {

    private static final long serialVersionUID = 3576678797374122941L;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        final MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        if (mappedStatement.getId().matches(_SQL_PATTERN)) { //拦截需要分页的SQL
            Object parameter = invocation.getArgs()[1];
            BoundSql boundSql = mappedStatement.getBoundSql(parameter);
            String originalSql = boundSql.getSql().trim();
            Object parameterObject = boundSql.getParameterObject();
            if (boundSql.getSql() == null || "".equals(boundSql.getSql()))
                return null;

            //分页参数--上下文传参
            Page page = null;
            PageContext context = PageContext.getPageContext();

            //map传参每次都将currentPage重置,先判读map再判断context
            if (parameterObject != null) {
                page = convertParameter(parameterObject, page);
            }


            //分页参数--context参数里的Page传参
            if (page == null) {
                page = context;
            }
            //后面用到了context的东东
            if (page != null) {
                int totPage = page.getTotalRows();
                //得到总记录数
                if (totPage == 0) {
                    Connection connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
                    totPage = SQLHelp.getCount(originalSql, connection, mappedStatement, parameterObject, boundSql);
                }

                //分页计算
                page.init(totPage, page.getPageSize(), page.getCurrentPage());

                //分页查询 本地化对象 修改数据库注意修改实现

                String pageSql = SQLHelp.generatePageSql(originalSql, page, DIALECT);
                if (log.isDebugEnabled()) {
                    log.debug("分页SQL:" + pageSql);
                }
                invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
                BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql, boundSql.getParameterMappings(), boundSql.getParameterObject());
                MappedStatement newMs = copyFromMappedStatement(mappedStatement, new BoundSqlSqlSource(newBoundSql));

                invocation.getArgs()[0] = newMs;
            }
        }
        return invocation.proceed();
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        super.initProperties(properties);
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms,
                                                    SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(),
                ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null) {
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        return builder.build();
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
