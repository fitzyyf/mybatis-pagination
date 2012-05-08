package org.noo;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.noo.dialect.model.Pagetag;
import org.noo.mapper.DictMapper;

import java.io.Reader;

/**
 * <p>
 * 测试查询.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 下午2:01
 * @since JDK 1.5
 */
public class PaginationTest {
    protected static SqlSession _session ;

    @Before
    public void setUp() throws Exception {
        String resource = "conf.xml";
        Reader reader = Resources.getResourceAsReader(resource);
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        _session = sessionFactory.openSession();

    }

    @Test
    public void testName() throws Exception {
        DictMapper dictMapper = _session.getMapper(DictMapper.class);
        Pagetag pagetag = new Pagetag();
        pagetag.setLeaf(0);
        pagetag.setPagingSize(10);
        dictMapper.findAllDict(pagetag);
    }
}
