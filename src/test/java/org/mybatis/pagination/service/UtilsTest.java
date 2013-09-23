package org.mybatis.pagination.service;

import com.google.common.base.CaseFormat;
import org.junit.Test;

/**
 * <p>
 * .
 * </p>
 *
 * @author walter yang
 * @version 1.0 2013-09-24 12:20 AM
 * @since JDK 1.5
 */
public class UtilsTest {

    @Test
    public void testHump() throws Exception {

        System.out.println( CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "helloDoMyword"));

    }
}
