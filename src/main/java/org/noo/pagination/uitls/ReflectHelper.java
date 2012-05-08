/*
 * Copyright (c) 2010-2011 NOO. All Rights Reserved.
 * [Id:ReflectHelper.java  2011-11-18 下午1:05 poplar.yfyang ]
 */
package org.noo.pagination.uitls;

import java.lang.reflect.Field;

/**
 * <p>
 * 反射工具类.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2011-11-18 下午1:05
 * @since JDK 1.5
 */
public class ReflectHelper {
    /**
     * 获取obj对象fieldName的Field
     *
     * @param obj 指定对象
     * @param fieldName 属性名称
     * @return 指定对象的属性字段
     */
    public static Field getFieldByFieldName(Object obj, String fieldName) {
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass
                .getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                e.fillInStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取obj对象fieldName的属性值
     *
     * @param obj
     * @param fieldName
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object getValueByFieldName(Object obj, String fieldName)
            throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field field = getFieldByFieldName(obj, fieldName);
        Object value = null;
        if (field != null) {
            if (field.isAccessible()) {
                value = field.get(obj);
            } else {
                field.setAccessible(true);
                value = field.get(obj);
                field.setAccessible(false);
            }
        }
        return value;
    }

    /**
     * 设置obj对象fieldName的属性值
     *
     * @param obj 设置的对象
     * @param fieldName 字段名称
     * @param value 属性值
     * @throws SecurityException  由安全管理器抛出的异常，指示存在安全侵犯
     * @throws NoSuchFieldException 类不包含指定名称的字段时产生的信号。
     * @throws IllegalArgumentException 抛出的异常表明向方法传递了一个不合法或不正确的参数。
     * @throws IllegalAccessException 执行的方法无法访问指定类、字段、方法或构造方法的定义时，抛出 IllegalAccessException。
     */
    public static void setValueByFieldName(Object obj, String fieldName,
                                           Object value) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        if (field.isAccessible()) {
            field.set(obj, value);
        } else {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        }
    }

}
