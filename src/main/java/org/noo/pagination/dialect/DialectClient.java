/*
 * Copyright (c) 2010-2011 NOO. All Rights Reserved.
 * [Id:DialectClient.java  2011-11-18 下午2:54 poplar.yfyang ]
 */
package org.noo.pagination.dialect;

import com.google.common.collect.Maps;
import org.noo.pagination.dialect.db.MySQLDialect;
import org.noo.pagination.dialect.db.OracleDialect;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * 数据库分页方言获取类.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2011-11-18 下午2:54
 * @since JDK 1.5
 */
public class DialectClient implements Serializable {
    private static final long serialVersionUID = 8107330250767760951L;

    private static final Map<String, Dialect> DBMS_DIALECT = Maps.newHashMap();

    /**
     * 根据数据库名称获取数据库分页查询的方言实现。
     *
     * @param dbmsName 数据库名称
     * @return 数据库分页方言实现
     */
    public static Dialect getDbmsDialiect(String dbmsName) {
        //转换为小写，保持一致性
        dbmsName = dbmsName.toLowerCase();
        if (DBMS_DIALECT.containsKey(dbmsName)) {
            return DBMS_DIALECT.get(dbmsName);
        }
        Dialect dialect = createDbmsDialiect(dbmsName);
        DBMS_DIALECT.put(dbmsName, dialect);
        return dialect;
    }

    private static Dialect createDbmsDialiect(String dbmsName) {
        if (dbmsName != null && !dbmsName.equals("")) {
//            int dbmsNameCode = System.identityHashCode(dbmsName);
            if ("mysql".equals(dbmsName)) {
                return new MySQLDialect();
            } else if ("oracle".endsWith(dbmsName)) {
                return new OracleDialect();
            }
            return null;
        } else {
            throw new UnsupportedOperationException("数据库方言不能为空");
        }
    }


}
