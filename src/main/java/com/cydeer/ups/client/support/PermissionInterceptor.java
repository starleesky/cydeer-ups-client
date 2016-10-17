package com.cydeer.ups.client.support;

import com.cydeer.core.utils.Utils;
import com.cydeer.ups.client.UpsClient;
import com.cydeer.ups.client.dto.UserDto;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangsong.
 * @date 2016/10/16 下午9:52
 */
public class PermissionInterceptor extends HandlerInterceptorAdapter implements InitializingBean {

	public enum UserRecover {
		HEADER, // 从HTTP头中还原
		SESSION, // 从SESSION中还原
		API      //从api的方式中还原
	}

	private UserRecover recover = UserRecover.HEADER;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (recover == null || !UserRecover.HEADER.equals(recover) || !UserRecover.SESSION.equals(recover)
				|| !UserRecover.API.equals(recover)) {
			throw new IllegalArgumentException("用户还原类型为指定");
		}
		if (UserRecover.API.equals(recover)) {
			// TODO: 2016/10/17 接口验证，就必须初始化接口
		}
	}

	@Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			// 静态资源不需要验证权限
			return true;
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		UpsClientImpl.setRequest(request, recover);

		// 默认都需要权限
		boolean shouldLogin = true;
		boolean shouldAuth = true;
		Permission permission = handlerMethod.getMethodAnnotation(Permission.class);
		if (permission == null) {
			permission = handlerMethod.getBeanType().getAnnotation(Permission.class);
		}
		if (permission != null) {
			shouldLogin = permission.login();
			shouldAuth = permission.auth();
		}
		if (!shouldLogin) {
			return true;
		}
		UserDto userDto = Utils.get(UpsClient.class).getUser();
		if (userDto == null) {
			doNoLogin(request, response, handler);
			return false;
		}
		if (!shouldAuth) {
			return true;
		}
		if (Utils.get(UpsClient.class).checkPermission() == null) {
			doNoPermission(request, response, handler);
			return false;
		}
		return true;
	}

	private void doNoPermission(HttpServletRequest request, HttpServletResponse response, Object handler) {
		// TODO: 2016/10/17 直接提示没有权限即可
	}

	private void doNoLogin(HttpServletRequest request, HttpServletResponse response, Object handler) {
		// TODO: 2016/10/17 跳转到登录页面
	}

	@Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) throws Exception {
		// TODO: 2016/10/17 清空一些线程变量信息 
		super.afterCompletion(request, response, handler, ex);
	}
}
