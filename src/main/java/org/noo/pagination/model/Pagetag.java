/*
 * Copyright (c) 2010-2011 NOO. All Rights Reserved.
 * [Id:PagingParameter.java  2011-11-18 下午12:55 poplar.yfyang ]
 */
package org.noo.pagination.model;

import java.io.Serializable;

/**
 * <p>
 * 分页参数对象.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2011-11-18 下午12:55
 * @since JDK 1.5
 */
public final class Pagetag implements Serializable {
    private static final long serialVersionUID = 1208542536262699297L;


    /**
     * 分页大小
     * 默认显示10条
     */
    private int pagingSize = 10;
    /**
     * 总记录数
     */
    private int total;


    /**
     * 当前查询的索引位置，可以对应页面的当前页
     */
    private int leaf;


    /**
     * 获取总记录数
     *
     * @return 总纪录数
     */
    public int getTotal() {
        return total;
    }

    /**
     * 设置总记录数
     *
     * @param total 总记录数
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * 获取分页大小
     *
     * @return 分页大小，默认为10
     */
    public int getPagingSize() {
        return pagingSize;
    }

    /**
     * 设置分页大小
     *
     * @param pagingSize 分页大小 默认为10
     */
    public void setPagingSize(int pagingSize) {
        this.pagingSize = pagingSize;
    }


    /**
     * 获取当前查询的索引位置，可以对应页面的当前页
     *
     * @return 取得当前查询的索引位置，可以对应页面的当前页
     */
    public int getLeaf() {
        return leaf;
    }

    /**
     * 设置当前查询的索引位置，可以对应页面的当前页
     *
     * @param leaf 当前查询的索引位置，可以对应页面的当前页
     */
    public void setLeaf(int leaf) {
        this.leaf = leaf;
    }
}
