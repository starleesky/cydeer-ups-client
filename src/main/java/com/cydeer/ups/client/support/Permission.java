package com.cydeer.ups.client.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhangsong.
 * @date 2016/10/16 下午9:58
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	/**
	 * 是否需要登录
	 *
	 * @return
	 */
	boolean login() default true;

	/**
	 * 是否需要权限验证
	 *
	 * @return
	 */
	boolean auth() default true;
}
