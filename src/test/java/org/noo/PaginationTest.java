package org.noo;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.noo.dialect.page.Page;
import org.noo.dialect.page.PageContext;
import org.noo.dialect.page.PageVO;
import org.noo.mapper.DictMapper;
import org.noo.module.Dict;
import org.noo.module.TestFind;

import java.io.Reader;
import java.util.List;

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
    protected static SqlSession _session;

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
        Page paginationSupport = new PageVO();
        paginationSupport.setCurrentPage(2);
        paginationSupport.setPageSize(10);
        List<Dict> list = dictMapper.findAllDict(paginationSupport);
        System.out.println(list.size());
        System.out.println(paginationSupport.getTotalPages());
        System.out.println(paginationSupport.getTotalRows());
    }

    @Test
    public void testByFind() throws Exception {
        DictMapper dictMapper = _session.getMapper(DictMapper.class);
        TestFind find = new TestFind();
        Page paginationSupport = new PageVO();
        paginationSupport.setCurrentPage(3);
        paginationSupport.setPageSize(10);
        find.setPageVO(paginationSupport);
        List<Dict> dicts = dictMapper.findAllDictByP(find);
        System.out.println(dicts.size());
        System.out.println(paginationSupport.getTotalPages());
        System.out.println(paginationSupport.getTotalRows());
    }


    @Test
    public void testContext() throws Exception {
        PageContext page = PageContext.getPageContext();
        page.setCurrentPage(2);
        page.setPageSize(10);
        DictMapper dictMapper = _session.getMapper(DictMapper.class);
        List<Dict> dicts = dictMapper.findAllDictByContext();
        System.out.println(dicts.size());
        System.out.println(page.getTotalPages());
        System.out.println(page.getTotalRows());
    }
}
