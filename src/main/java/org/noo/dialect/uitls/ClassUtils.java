package org.noo.dialect.uitls;

/**
 * <p>
 * .
 * </p>
 *
 * @author poplar.yfyang
 * @version 1.0 2012-05-08 上午11:25
 * @since JDK 1.5
 */
public class ClassUtils {

    public static boolean equalClass(Class cls,Class target){
        return cls.isAssignableFrom(target);
    }


    public static boolean equalObject(Object obj,Class target){
        return equalClass(obj.getClass(),target);
    }


}
