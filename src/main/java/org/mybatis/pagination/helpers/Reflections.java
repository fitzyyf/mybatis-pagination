/*
 * Copyright (c) 2012-2013, Poplar Yfyang 杨友峰 (poplar1123@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mybatis.pagination.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.base.Preconditions;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

/**
 * <p>
 * 参考 Springside中的反射工具.
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 上午10:13
 * @since JDK 1.5
 */
public class Reflections {

	/** CGLIB */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";
	/** An instance of Log to use for logging in this class. */
	private static final Log log = LogFactory.getLog(Reflections.class);

	private Reflections() {
	}

	/** 调用Getter方法. */
	public static Object invokeGetter(Object obj, String propertyName) {
		String getterMethodName = "get" + StringHelper.capitalize(propertyName);
		return invokeMethod(obj, getterMethodName, new Class[]{}, new Object[]{});
	}

	/** 调用Setter方法.使用value的Class来查找Setter方法. */
	public static void invokeSetter(Object obj, String propertyName, Object value) {
		invokeSetter(obj, propertyName, value, null);
	}

	/**
	 * 调用Setter方法.
	 *
	 * @param propertyType 用于查找Setter方法,为空时使用value的Class替代.
	 */
	public static void invokeSetter(Object obj, String propertyName, Object value, Class<?> propertyType) {
		Class<?> type = propertyType != null ? propertyType : value.getClass();
		String setterMethodName = "set" + StringHelper.capitalize(propertyName);
		invokeMethod(obj, setterMethodName, new Class[]{type}, new Object[]{value});
	}

	/** 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数. */
	public static Object getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);
        Preconditions.checkNotNull(field, "Could not find field [" + fieldName + "] on target [" + obj + "]");

		Object result = null;
		try {
			result = field.get(obj);
		} catch (IllegalAccessException e) {
			log.error("不可能抛出的异常!", e);
		}
		return result;
	}

	/** 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数. */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
		Field field = getAccessibleField(obj, fieldName);
        Preconditions.checkNotNull(field, "Could not find field [" + fieldName + "] on target [" + obj + "]");

        try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			log.error("不可能抛出的异常!", e);
		}
	}

	/** 对于被cglib AOP过的对象, 取得真实的Class类型. */
	public static Class<?> getUserClass(Class<?> clazz) {
		if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 用于一次性调用的情况.
	 */
	public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
									  final Object[] args) {
		Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        Preconditions.checkNotNull(method, "Could not find method [" + methodName + "] on target [" + obj + "]");
		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField,	 并强制设置为可访问.
	 * <p/>
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
        Preconditions.checkNotNull(obj, "object can't be null");
        Preconditions.checkArgument(obj.toString().length() < 1, "fieldName can't be blank");
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException e) {//NOSONAR
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
	 * 如向上转型到Object仍无法找到, 返回null.
	 * <p/>
	 * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
	 */
	public static Method getAccessibleMethod(final Object obj, final String methodName,
											 final Class<?>... parameterTypes) {

        Preconditions.checkNotNull(obj, "object can't be null");
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Method method = superClass.getDeclaredMethod(methodName, parameterTypes);

				method.setAccessible(true);

				return method;

			} catch (NoSuchMethodException e) {//NOSONAR
				// Method不在当前类定义,继续向上转型
			}
		}
		return null;
	}

	/**
	 * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
	 * 如无法找到, 返回Object.class.
	 * eg.
	 * public UserDao extends HibernateDao<User>
	 *
	 * @param clazz The class to introspect
	 * @return the first generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static <T> Class<T> getSuperClassGenricType(final Class clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
	 * 如无法找到, 返回Object.class.
	 * <p/>
	 * 如public UserDao extends HibernateDao<User,Long>
	 *
	 * @param clazz clazz The class to introspect
	 * @param index the Index of the generic ddeclaration,start from 0.
	 * @return the index generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings("rawtypes")
	public static Class getSuperClassGenricType(final Class clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType.getClass().isAssignableFrom(ParameterizedType.class))) {
			log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			log.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index].getClass().isAssignableFrom(Class.class))) {
			log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}

		return (Class) params[index];
	}

	/**
	 * 执行反射执行
	 *
	 * @param className 类型
	 * @return 类的实例
	 */
	@SuppressWarnings("rawtypes")
	public static Object instance(String className) {
		try {
			Class dialectCls = Class.forName(className);
			return dialectCls.newInstance();
		} catch (ClassNotFoundException e) {
			log.error("无法找到方言类", e);
			return null;
		} catch (InstantiationException e) {
			log.error("实例化方言错误", e);
			return null;
		} catch (IllegalAccessException e) {
			log.error("实例化方言错误", e);
			return null;
		}
	}

	/** 将反射时的checked exception转换为unchecked exception. */
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException) {
			return new IllegalArgumentException(e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException(((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException("Unexpected Checked Exception.", e);
	}
}
