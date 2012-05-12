package org.noo.dialect.interceptor;

import org.apache.ibatis.exceptions.PersistenceException;
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
import org.noo.dialect.annotation.Paging;
import org.noo.dialect.page.Page;
import org.noo.dialect.page.PageContext;
import org.noo.dialect.page.PageVO;
import org.noo.dialect.uitls.Reflections;

import java.lang.reflect.Field;
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
public class QueryPaginationInterceptor extends BaseInterceptor {

    private static final long serialVersionUID = 3576678797374122941L;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String originalSql = boundSql.getSql().trim();
        RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
        Object parameterObject = boundSql.getParameterObject();
        if (boundSql.getSql() == null || "".equals(boundSql.getSql()))
            return null;

        //分页参数--上下文传参
        PageVO pageVO = null;
        PageContext context = PageContext.getPageContext();

        //map传参每次都将currentPage重置,先判读map再判断context
        if (parameterObject != null) {
            if (parameterObject instanceof Page) {
                pageVO = (PageVO) parameterObject;
            } else {
                //参数为某个实体，该实体拥有Page属性
                Paging paging = parameterObject.getClass().getAnnotation(Paging.class);
                String field = paging.field();
                Field pageField = Reflections.getAccessibleField(parameterObject, field);
                if (pageField != null) {
                    pageVO = (PageVO) Reflections.getFieldValue(parameterObject, field);
                    if (pageVO == null)
                        throw new PersistenceException("分页参数不能为空");
                    //通过反射，对实体对象设置分页对象
                    Reflections.setFieldValue(parameterObject, field, pageVO);
                } else {
                    throw new NoSuchFieldException(parameterObject.getClass().getName()
                            + "不存在 page 属性！");
                }
            }
        }


        //分页参数--context参数里的Page传参
        if (pageVO == null) {
            pageVO = context;
        }
        //后面用到了context的东东
        if (pageVO != null) {
            int totPage = pageVO.getTotalRows();
            //得到总记录数
            if (totPage == 0) {
                Connection connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
                totPage = SQLHelp.getCount(originalSql, connection, mappedStatement, parameterObject, boundSql);
            }

            //分页计算
            pageVO.init(totPage, pageVO.getPageSize(), pageVO.getCurrentPage());

            //分页查询 本地化对象 修改数据库注意修改实现

            String pageSql = SQLHelp.generatePageSql(originalSql, pageVO, DBMS_THREAD_LOCAL.get());
            if (log.isDebugEnabled()) {
                log.debug("分页SQL:" + pageSql);
            }
            invocation.getArgs()[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
            BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), pageSql,boundSql.getParameterMappings(),boundSql.getParameterObject());
            MappedStatement newMs = copyFromMappedStatement(mappedStatement,new BoundSqlSqlSource(newBoundSql));

            invocation.getArgs()[0]= newMs;
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms,
                                                    SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(),
                ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if(ms.getKeyProperties()!=null){
            for (String keyProperty : ms.getKeyProperties()) {
                builder.keyProperty(keyProperty);
            }
        }
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.cache(ms.getCache());
        MappedStatement newMs = builder.build();
        return newMs;
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
