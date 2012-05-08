package org.noo.dialect.model;

import java.io.Serializable;

/**
 * <p>
 * .
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 下午12:53
 * @since JDK 1.5
 */
public interface RecordPage extends Serializable {
    /**
     * 获取总记录数
     *
     * @return 总纪录数
     */
    int getTotal();

    /**
     * 设置总记录数
     *
     * @param total 总记录数
     */
    void setTotal(int total);

    /**
     * 获取分页大小
     *
     * @return 分页大小，默认为10
     */
    int getPagingSize();

    /**
     * 设置分页大小
     *
     * @param pagingSize 分页大小 默认为10
     */
    void setPagingSize(int pagingSize);

    /**
     * 获取当前查询的索引位置，可以对应页面的当前页
     *
     * @return 取得当前查询的索引位置，可以对应页面的当前页
     */
    int getLeaf();

    /**
     * 设置当前查询的索引位置，可以对应页面的当前页
     *
     * @param leaf 当前查询的索引位置，可以对应页面的当前页
     */
    void setLeaf(int leaf);
}
