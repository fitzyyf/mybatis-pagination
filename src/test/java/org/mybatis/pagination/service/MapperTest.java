package org.mybatis.pagination.service;

import java.util.List;
import javax.annotation.Resource;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.pagination.domain.Resources;
import org.mybatis.pagination.dto.PageMyBatis;
import org.mybatis.pagination.dto.datatables.PagingCriteria;
import org.mybatis.pagination.dto.datatables.SearchField;
import org.mybatis.pagination.dto.datatables.SortDirection;
import org.mybatis.pagination.dto.datatables.SortField;
import org.mybatis.pagination.mapper.ResourcesMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * <p>
 * .
 * </p>
 *
 * @author mumu@yfyang
 * @version 1.0 2013-09-09 2:15 PM
 * @since JDK 1.5
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/test-context.xml")
public class MapperTest {

    @Resource
    private ResourcesMapper resourcesMapper;

//    @Before
//    //使用该注释会使用事务，而且在测试完成之后会回滚事务，也就是说在该方法中做出的一切操作都不会对数据库中的数据产生任何影响
//    @Rollback(false) //这里设置为false，就让事务不回滚
//    public void setUp() throws Exception {
//        Resources resources;
//        for (int i = 0; i < 1000; i++) {
//            resources = new Resources();
//            resources.setId(UUID.randomUUID().toString());
//            resources.setName("测试数据" + i);
//            resources.setPath("test/pageh/" + i);
//            resourcesMapper.insertSelective(resources);
//        }
//
//    }

    @Test
    public void testPagaination() throws Exception {

        PagingCriteria baseCriteria = PagingCriteria.createCriteria(0, 15, 15);
        PageMyBatis<Resources> pageMyBatis = resourcesMapper.selectByPage(baseCriteria);
        for (Resources pageMyBati : pageMyBatis) {
            System.out.println(pageMyBati);
        }
    }

    @Test
    public void testPagainationSqlContainOrder() throws Exception {
        PagingCriteria baseCriteria = PagingCriteria.createCriteria(0, 15, 15);
        PageMyBatis<Resources> pageMyBatis = resourcesMapper.selectByPageOrder(baseCriteria);
        for (Resources pageMyBati : pageMyBatis) {
            System.out.println(pageMyBati);
        }

    }

    @Test
    public void testPagainationAndWrap() throws Exception {
        PagingCriteria baseCriteria = PagingCriteria.createCriteria(0, 15, 15);
        PageMyBatis<Resources> pageMyBatis = resourcesMapper.selectByPage(baseCriteria);
        System.out.println("pageMyBatis.warp() = " + pageMyBatis.warp());
    }

    @Test
    public void testPagainationAndOrder() throws Exception {

        List<SortField> sortFields = Lists.newArrayList();
        sortFields.add(new SortField("name", SortDirection.DESC));
        sortFields.add(new SortField("path", SortDirection.ASC));

        PagingCriteria baseCriteria = PagingCriteria.createCriteriaWithSort(20, 15, 15, sortFields);
        PageMyBatis<Resources> pageMyBatis = resourcesMapper.selectByPage(baseCriteria);
        for (Resources pageMyBati : pageMyBatis) {
            System.out.println(pageMyBati);
        }
    }

    @Test
    public void testPagainationAndSearch() throws Exception {
        List<SearchField> searchFields = Lists.newArrayList();
        searchFields.add(new SearchField("name", false, false, "11"));

        PagingCriteria baseCriteria = PagingCriteria.createCriteriaWithSearch(0, 15, 15, searchFields);
        PageMyBatis<Resources> pageMyBatis = resourcesMapper.selectByPage(baseCriteria);
        for (Resources pageMyBati : pageMyBatis) {
            System.out.println(pageMyBati);
        }
    }

    @Test
    public void testPagainationAndOrderSearch() throws Exception {
        List<SearchField> searchFields = Lists.newArrayList();
        searchFields.add(new SearchField("name", false, false, "11"));

        PagingCriteria baseCriteria = PagingCriteria.createCriteriaWithSearch(0, 15, 15, searchFields);
        PageMyBatis<Resources> pageMyBatis = resourcesMapper.selectByPageOrder(baseCriteria);
        for (Resources pageMyBati : pageMyBatis) {
            System.out.println(pageMyBati);
        }
    }

    @Test
    public void testPaginationMoreWhere() throws Exception {
        List<SearchField> searchFields = Lists.newArrayList();
        searchFields.add(new SearchField("name", false, false, "11"));

        PagingCriteria baseCriteria = PagingCriteria.createCriteriaWithSearch(0, 15, 15, searchFields);
        PageMyBatis<Resources> pageMyBatis = resourcesMapper.selectByPageOrderAndWhere(baseCriteria,"aa");
        for (Resources pageMyBati : pageMyBatis) {
            System.out.println(pageMyBati);
        }

    }
}
