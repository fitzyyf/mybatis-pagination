/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package org.mybatis.pagination.dialect;

/**
 * <p>
 * 数据库类型.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 上午11:36
 * @since JDK 1.5
 */
public enum DBMS {
    MYSQL,
    ORACLE,
    DB2,
    H2,
    HSQL,
    POSTGRE,
    SQLSERVER,
    SQLSERVER2005,
    SYBASE,
    /**
     * 自定义
     */
    EX
}
