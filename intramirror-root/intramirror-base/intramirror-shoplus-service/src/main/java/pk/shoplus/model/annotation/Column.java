package pk.shoplus.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author OC
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

	/**
	 * 
	 * @return
	 */
	Class<?> type() default String.class;

	/**
	 * 
	 * @return
	 */
	int length() default 0;
}
