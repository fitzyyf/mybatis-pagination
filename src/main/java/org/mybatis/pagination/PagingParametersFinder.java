/*
 * Copyright © 2012-2013 mumu@yfyang. All Rights Reserved.
 */

package org.mybatis.pagination;

import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanMap;
import org.mybatis.pagination.dto.datatables.PagingCriteria;
import org.mybatis.pagination.helpers.StringHelper;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * Paging <code>PaginationCriteria</code> finds.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-12-09 7:22 PM
 * @since JDK 1.5
 */
public enum  PagingParametersFinder {

    instance;

    /**
     * The search parameters by use of interim storage of results.
     */
    private final Map<Object, String> search_map = Maps.newHashMap();

    /**
     * private constructor
     */
    private PagingParametersFinder() {
    }


    /**
     * from the formulation of the objects found in the paging parameters object.
     *
     * @param object object.
     * @return paging parameters.
     */
    public PagingCriteria findCriteria(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return findCriteriaFromObject(object);
        } finally {
            //cleanup query the value of the temporary Map.
            search_map.clear();
        }
    }

    /**
     * In the object to find whether contains <code>PaginationCriteria</code> objects.
     *
     * @param object parameter object.
     * @return PaginationCriteria
     */
    private PagingCriteria findCriteriaFromObject(Object object) {

        //如果已经寻找过这个对象，现在再来这里肯定是没找到。就直接返回NULL
        if (search_map.containsKey(object)) {
            return null;
        }
        //object class
        Class<?> obj_class = object.getClass();
        PagingCriteria pc;
        //primitive
        if (isPrimitiveType(obj_class)) {
            pc = null;
        } else if (object instanceof PagingCriteria) {
            pc = (PagingCriteria) object;
        } else if (object instanceof Map) {
            pc = findCriteriaFromMap((Map) object);
        } else if (object instanceof Collection) {
            pc = findCriteriaFromCollection((Collection) object);
        } else if (obj_class.isArray()) {
            pc = findCriteriaFromArray(object);
        } else {
            BeanMap map = new BeanMap(object);
            return findCriteriaFromMap(map);
        }


        search_map.put(object, StringHelper.EMPTY);
        return pc;
    }

    /**
     * In the array to find whether it contains the <code>PaginationCriteria</code> object.
     *
     * @param array the array.
     * @return PageQuery
     */
    private PagingCriteria findCriteriaFromArray(Object array) {
        if (search_map.containsKey(array)) {
            return null;
        }

        Object object;
        PagingCriteria pc;
        for (int i = 0; i < Array.getLength(array); i++) {
            object = Array.get(array, i);
            pc = findCriteriaFromObject(object);
            if (pc != null) {
                search_map.put(array, StringHelper.EMPTY);
                return pc;
            }
        }
        search_map.put(array, StringHelper.EMPTY);
        return null;
    }

    /**
     * In the Collection to find whether contains <code>PaginationCriteria</code> objects.
     *
     * @param collection parameter collection.
     * @return PageQuery
     */
    private PagingCriteria findCriteriaFromCollection(Collection collection) {
        if (search_map.containsKey(collection)) {
            return null;
        }
        PagingCriteria pc;

        for (Object e : collection) {
            pc = findCriteriaFromObject(e);
            if (pc != null) {
                search_map.put(collection, StringHelper.EMPTY);
                return pc;
            }
        }

        search_map.put(collection, StringHelper.EMPTY);
        return null;
    }

    /**
     * In the Map to find whether contains <code>PaginationCriteria</code> objects.
     *
     * @param map parameter map.
     * @return PaginationCriteria
     */
    private PagingCriteria findCriteriaFromMap(Map map) {
        if (search_map.containsKey(map)) {
            return null;
        }

        PagingCriteria pc;
        for (Object value : map.values()) {
            pc = findCriteriaFromObject(value);
            if (pc != null) {
                search_map.put(map, StringHelper.EMPTY);
                return pc;
            }
        }

        search_map.put(map, StringHelper.EMPTY);
        return null;
    }



    /**
     * 返回指定类型所对应的primitive类型。包含String类
     * <p/>
     * fixed:paramter string type.
     *
     * @param clazz 要检查的类型
     * @return 如果指定类型为<code>null</code>或不是primitive类型的包装类，则返回<code>false</code>，否则返回<code>true</code>。
     */
    public static boolean isPrimitiveType(Class clazz) {
        return clazz != null && (clazz.isPrimitive() || clazz.equals(Long.class) || clazz.equals(Integer.class)
                || clazz.equals(Short.class) || clazz.equals(Byte.class) || clazz.equals(Double.class)
                || clazz.equals(Float.class) || clazz.equals(Boolean.class) || clazz.equals(Character.class) || clazz.equals(String.class));

    }
}
