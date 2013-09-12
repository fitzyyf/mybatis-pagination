package org.mybatis.pagination.helpers;

import org.junit.Test;

/**
 * <p>
 * .
 * </p>
 *
 * @author walter yang
 * @version 1.0 2013-09-12 11:04 AM
 * @since JDK 1.5
 */
public class SqlHelperTest {
    @Test
    public void testContainOrder() throws Exception {
        String sql = "select * from a order by xx";
        System.out.println("SqlHelper.hasOrder(sql) = " + SqlHelper.containOrder(sql));
    }

    @Test
    public void testContainWhere() throws Exception {

        String sql = "select * from a where cc = aa";
        System.out.println("SqlHelper.hasOrder(sql) = " + SqlHelper.containWhere(sql));

    }
}
