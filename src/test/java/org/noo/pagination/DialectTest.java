/*
 * Copyright (c) 2010-2011 IFLYTEK. All Rights Reserved.
 * [Id:DialectTest.java  2012-03-21 下午9:12 poplar.yfyang ]
 */
package org.noo.pagination;

import org.junit.Test;
import org.noo.pagination.dialect.Dialect;
import org.noo.pagination.dialect.db.SQLServer2005Dialect;
import org.noo.pagination.model.DBMS;

/**
 * <p>
 * .
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-03-21 下午9:12
 * @since JDK 1.5
 */
public class DialectTest {
    @Test
    public void testMSSql() throws Exception {
        Dialect dialect = new SQLServer2005Dialect();
        String sql = dialect.getLimitString("SELECT  *  FROM C_QuickQuery where  electronicrecordtype in " +
                " ('c443fa8c-0166-4d6a-bfa6-a47e321be3bc') and state02 in ('transfered' , 'created') " +
                "and state03<>'destructed' ", 10, 20);
        System.out.println("分页SQL："+sql);
    }

    @Test
    public void testName() throws Exception {
        DBMS dbms = DBMS.valueOf("MYSQL");
        System.out.println(dbms);
    }
}
