package org.mybatis.pagination.mapper;

import org.apache.ibatis.annotations.Param;
import org.mybatis.pagination.domain.Resources;
import org.mybatis.pagination.domain.ResourcesCriteria;
import org.mybatis.pagination.dto.PageMyBatis;
import org.mybatis.pagination.dto.datatables.PagingCriteria;
import org.mybatis.pagination.extra.MyBatisRepository;

import java.util.List;

@MyBatisRepository
public interface ResourcesMapper {
    /**
     * 根据指定的条件删除系统资源的数据库记录,auth_resources
     *
     * @param example 动态SQL条件实例
     */
    int deleteByExample(ResourcesCriteria example);

    /**
     * 根据主键删除系统资源的数据库记录,auth_resources
     *
     * @param id 主键唯一标志
     */
    int deleteByPrimaryKey(String id);

    /**
     * 新写入系统资源的数据库记录,auth_resources
     *
     * @param record 系统资源
     */
    int insert(Resources record);

    /**
     * 动态字段,写入系统资源的数据库记录,auth_resources
     *
     * @param record 系统资源
     */
    int insertSelective(Resources record);

    /**
     * 根据指定的条件查询符合条件的系统资源的数据库记录,auth_resources
     *
     * @param example 动态SQL条件实例
     */
    List<Resources> selectByExample(ResourcesCriteria example);

    /**
     * 根据指定主键获取系统资源的数据库记录,auth_resources
     *
     * @param id 主键唯一标志
     */
    Resources selectByPrimaryKey(String id);

    /**
     * 动态根据指定的条件来更新符合条件的系统资源的数据库记录,auth_resources
     *
     * @param record  系统资源
     * @param example 动态SQL条件实例
     */
    int updateByExampleSelective(@Param("record") Resources record, @Param("example") ResourcesCriteria example);

    /**
     * 根据指定的条件来更新符合条件的系统资源的数据库记录,auth_resources
     *
     * @param record  系统资源
     * @param example 动态SQL条件实例
     */
    int updateByExample(@Param("record") Resources record, @Param("example") ResourcesCriteria example);

    /**
     * 动态字段,根据主键来更新符合条件的系统资源的数据库记录,auth_resources
     *
     * @param record 系统资源
     */
    int updateByPrimaryKeySelective(Resources record);

    /**
     * 根据主键来更新符合条件的系统资源的数据库记录,auth_resources
     *
     * @param record 系统资源
     */
    int updateByPrimaryKey(Resources record);

    PageMyBatis<Resources> selectByPage(PagingCriteria pagingCriteria);
    PageMyBatis<Resources> selectByPageOrder(PagingCriteria pagingCriteria);
    PageMyBatis<Resources> selectByPageOrderAndWhere(PagingCriteria pagingCriteria,@Param("name") String name);
}