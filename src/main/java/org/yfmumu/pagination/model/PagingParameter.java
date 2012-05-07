/*
 * Copyright (c) 2010-2011 NOO. All Rights Reserved.
 * [Id:PagingParameter.java  2011-11-18 下午12:55 poplar.yfyang ]
 */
package org.yfmumu.pagination.model;

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
public final class PagingParameter implements Serializable {
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
     * 查询开始纪录索引位置
     */
    private int beginIndex;

    /**
     * 当前查询的索引位置，可以对应页面的当前页
     */
    private int resultCurrentIndex;

    /**
     * true:需要分页的地方，传入的参数就是Page实体；
     * false:需要分页的地方，传入的参数所代表的实体拥有Page属性
     */
    private boolean entityOrField;


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
     * true:需要分页的地方，传入的参数就是Page实体；
     * false:需要分页的地方，传入的参数所代表的实体拥有Page属性
     *
     * @return true:需要分页的地方，传入的参数就是Page实体；
     *         false:需要分页的地方，传入的参数所代表的实体拥有Page属性
     */
    public boolean isEntityOrField() {
        return entityOrField;
    }

    /**
     * true:需要分页的地方，传入的参数就是Page实体；
     * false:需要分页的地方，传入的参数所代表的实体拥有Page属性
     *
     * @param entityOrField true:需要分页的地方，传入的参数就是Page实体；
     *                      false:需要分页的地方，传入的参数所代表的实体拥有Page属性
     */
    public void setEntityOrField(boolean entityOrField) {
        this.entityOrField = entityOrField;
    }

    /**
     * 查询开始纪录索引位置
     * 获取时会更具当前查询的位置进行重新计算开始纪录数。
     *
     * @return 查询开始纪录索引位置
     */
    public int getBeginIndex() {
        beginIndex = (getResultCurrentIndex() - 1) * getPagingSize();
        return beginIndex < 0 ? 0 : beginIndex;
    }

    /**
     * 查询开始纪录索引位置
     *
     * @param beginIndex 查询开始纪录索引位置
     */
    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }

    /**
     * 获取当前查询的索引位置，可以对应页面的当前页
     *
     * @return 取得当前查询的索引位置，可以对应页面的当前页
     */
    public int getResultCurrentIndex() {
        return resultCurrentIndex;
    }

    /**
     * 设置当前查询的索引位置，可以对应页面的当前页
     *
     * @param resultCurrentIndex 当前查询的索引位置，可以对应页面的当前页
     */
    public void setResultCurrentIndex(int resultCurrentIndex) {
        this.resultCurrentIndex = resultCurrentIndex;
    }
}
