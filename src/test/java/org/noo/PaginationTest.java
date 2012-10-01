package org.noo;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.noo.pagination.page.Page;
import org.noo.pagination.page.PageContext;
import org.noo.pagination.page.Pagination;
import org.noo.mapper.DictMapper;
import org.noo.module.Dict;
import org.noo.module.TestFind;

import java.io.Reader;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        Page paginationSupport = new Pagination();
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
        Page paginationSupport = new Pagination();
        paginationSupport.setCurrentPage(1);
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

    @Test
    public void testRunMoreThread() throws Exception {
        //构造一个线程池
        ThreadPoolExecutor producerPool = new ThreadPoolExecutor(10, 21, 0,
                TimeUnit.SECONDS, new ArrayBlockingQueue(3),
                new ThreadPoolExecutor.DiscardOldestPolicy());
        //每隔produceTaskSleepTime的时间向线程池派送一个任务。
        int i=1;
        while(i<50){
            try {
                int produceTaskSleepTime = 2;
                Thread.sleep(produceTaskSleepTime);
                final String task = "task@ " + i;
                System.out.println("put " + task);
                final int finalI = i;
                producerPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("运行"+Thread.currentThread().getName());
                        PageContext page = PageContext.getPageContext();
                        page.setCurrentPage(finalI);
                        page.setPageSize(10);
                        DictMapper dictMapper = _session.getMapper(DictMapper.class);
                        List<Dict> dicts = dictMapper.findAllDictByContext();
                        System.out.println(task + "::"+dicts.size());
                        System.out.println(task + "::"+page.getTotalPages());
                        System.out.println(task + "::"+page.getTotalRows());
                    }
                });
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Thread.sleep(1000000);
    }
}
