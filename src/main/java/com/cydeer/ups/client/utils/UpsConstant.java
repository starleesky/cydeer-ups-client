package com.cydeer.ups.client.utils;

public interface UpsConstant {
	//用户模块节点类型：系统、模块组、模块、操作
	int TYPE_SYSTEM = 0;
	int TYPE_GROUP = 1;
	int TYPE_MODULE = 2;
	int TYPE_OPERATE = 3;
	//权限管理系统
	int SYSTEM_ID_UPS = 1;
	// 系统基础模块组id
	int BASE_MODULE_GROUP_ID = 1;

	int BASE_MODULE_GROUP_ID_WIHTOUT_ADMIN = 2;

	/**
	 * 用户在 SESSION 中的键值
	 */
	String USER_SESSION_KEY = UpsConstant.class.getName() + "_USER";

	/**
	 * 用户在 HTTP 请求头中的键值
	 */
	String USER_HEADER_KEY = UpsConstant.class.getName() + "_USER";

	/**
	 * 用户在 api 请求中的键值
	 */
	String USER_API_KEY = UpsConstant.class.getName() + "_API";

	String USER_ENV_KEY = UpsConstant.class.getName() + "_ENV";

	/**
	 * 用户在 HTTP 请求头中的键值
	 */
	String ENV_HEADER_KEY = UpsConstant.class.getName() + "_ENV";

	/**
	 * BM系统的 res 目录<br>
	 * 对于领域系统跳转到 BM 系统时，应该加此前缀；
	 */
	String SYSTEM_BM = "/bm";
	String SYSTEM_BM_RES = "/bm/res";
	String SYSTEM_UPS = "/ups";
	String SYSTEM_ADMIN = "/admin";
	String SYSTEM_INSADMIN = "/insadmin";
	String SYSTEM_TASK = "/task";
	String SYSTEM_OPS = "/ops";
	String SYSTEM_COMPASS = "/compass";
	String SYSTEM_RISK = "/risk";
	String SYSTEM_MARKET = "/market";
}
