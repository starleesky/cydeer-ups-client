package com.cydeer.ups.client.support;

import com.cydeer.core.utils.compress.StringCompressor;
import com.cydeer.core.utils.encrypt.Base64;
import com.cydeer.core.utils.jackson.Jackson;
import com.cydeer.ups.client.UpsClient;
import com.cydeer.ups.client.dto.NodeDto;
import com.cydeer.ups.client.dto.SystemDto;
import com.cydeer.ups.client.dto.UserDto;
import com.cydeer.ups.client.utils.UpsConstant;
import org.apache.commons.io.Charsets;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

import static com.cydeer.ups.client.utils.UpsConstant.USER_SESSION_KEY;

/**
 * @author zhangsong.
 * @date 2016/10/17 下午6:48
 */
public class UpsClientImpl implements UpsClient, InvocationHandler {

	private static final String INITED_KEY = UpsClientImpl.class.getName() + "_INITED";
	private static final String RECOVER_KEY = UpsClientImpl.class.getName() + "_RECOVER";
	private static final String USER_KEY = UpsClientImpl.class.getName() + "_USER";
	private static final String CURR_NODE_KEY = UpsClientImpl.class.getName() + "_NODE";
	private static final String CURR_SYSTEM_KEY = UpsClientImpl.class.getName() + "_SYSTEM";
	private static final String BREAD_CRUM = UpsClientImpl.class.getName() + "_BREAD_CRUM";
	private static ThreadLocal<HttpServletRequest> requestLocal = new ThreadLocal<HttpServletRequest>();
	private Map<String, Pattern> pathPatternMap = new HashMap<>();
	private static Pattern URI_PATH_PATTERN = Pattern.compile("[\\.;#].*$");

	@Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (requestLocal.get() == null) {
			return null;
		}
		Boolean isInit = (Boolean) requestLocal.get().getAttribute(INITED_KEY);
		if (!isInit) {
			synchronized (UpsClientImpl.class) {
				isInit = (Boolean) requestLocal.get().getAttribute(INITED_KEY);
				if (!isInit) {
					initUserData();
				}
			}
		}
		Object object = method.invoke(this, args);
		return object;
	}

	private void initUserData() {
		PermissionInterceptor.UserRecover recover = (PermissionInterceptor.UserRecover) requestLocal.get().getAttribute(
				RECOVER_KEY);
		if (recover == null) {
			recover = PermissionInterceptor.UserRecover.HEADER;
		}
		UserDto user = recoverUser(recover);
		if (user == null) {
			return;
		}
		requestLocal.get().setAttribute(USER_KEY, user);
		List<NodeDto> nodeLinks = new ArrayList<>();
		NodeDto node = findNode(getFullUrlPath(requestLocal.get()), nodeLinks);
		requestLocal.get().setAttribute(CURR_NODE_KEY, node);
		requestLocal.get().setAttribute(BREAD_CRUM, nodeLinks);
		if (!CollectionUtils.isEmpty(nodeLinks)) {
			int currSystemId = nodeLinks.get(0).getId();
			for (SystemDto system : user.getSystems()) {
				if (system.getId() == currSystemId) {
					requestLocal.get().setAttribute(CURR_SYSTEM_KEY, system);
				}
			}
		}
		requestLocal.get().setAttribute(INITED_KEY, Boolean.TRUE);
	}

	private NodeDto findNode(String uriPath, List<NodeDto> nodeLinks) {
		if (StringUtils.isEmpty(uriPath)) {
			return null;
		}
		UserDto userDto = getUser();
		if (userDto == null) {
			return null;
		}
		List<SystemDto> systems = userDto.getSystems();
		if (CollectionUtils.isEmpty(systems)) {
			return null;
		}
		NodeDto nodeDto = null;
		for (SystemDto systemDto : systems) {
			nodeDto = findMatchNode(systemDto.getChildren(), uriPath, nodeLinks);
			if (nodeDto == null) {
				if (getPathPattern(systemDto.getPath()).matcher(uriPath).matches()) {
					nodeDto = new NodeDto();
					nodeDto.setId(systemDto.getId());
					nodeDto.setName(systemDto.getName());
					nodeDto.setPath(systemDto.getPath());
				}
			}
			if (nodeDto != null) {
				if (nodeLinks != null) {
					systemDto.setActive(true);
					nodeLinks.add(systemDto);
				}
				break;
			}
			if (nodeLinks != null
					&& org.apache.commons.lang3
					.StringUtils.equalsIgnoreCase(systemDto.getRootPath(), uriPath.replaceAll("(/[^/]+).*", "$1"))) {
				// 如果请求路径的根路径与系统的 RootPath 相同，认为当前系统属于激活状态
				systemDto.setActive(true);
			}
		}
		if (nodeLinks != null)
			Collections.reverse(nodeLinks);
		return nodeDto;
	}

	private NodeDto findMatchNode(List<NodeDto> children, String uriPath, List<NodeDto> nodeLinks) {
		for (NodeDto nodeDto : children) {
			NodeDto node = findMatchNode(nodeDto.getChildren(), uriPath, nodeLinks);
			if (node != null) {
				if (nodeLinks != null) {
					nodeDto.setActive(true);
					nodeLinks.add(nodeDto);
				}
				return node;
			}
			// 模块组 或者path为空不检查
			if (UpsConstant.TYPE_GROUP == nodeDto.getType() || StringUtils.isEmpty(nodeDto.getPath())) {
				continue;
			}
			if (getPathPattern(nodeDto.getPath()).matcher(uriPath).matches()) {
				if (nodeLinks != null) {
					nodeDto.setActive(true);
					nodeLinks.add(nodeDto);
				}
				return nodeDto;
			}

		}
		return null;
	}

	private String getFullUrlPath(HttpServletRequest request) {
		return request.getRequestURI();
	}

	private UserDto recoverUser(PermissionInterceptor.UserRecover recover) {
		if (PermissionInterceptor.UserRecover.HEADER.equals(recover)) {
			return recoverUserFromHeader();
		} else if (PermissionInterceptor.UserRecover.SESSION.equals(recover)) {
			return recoverUserFromSession();
		}
		return null;
	}

	private UserDto recoverUserFromSession() {
		return (UserDto) requestLocal.get().getSession().getAttribute(USER_SESSION_KEY);
	}

	private UserDto recoverUserFromHeader() {
		String userHeader = requestLocal.get().getHeader(UpsConstant.USER_HEADER_KEY);
		if (org.apache.commons.lang3.StringUtils.isNotBlank(userHeader)) {
			byte[] userData = Base64.decode(userHeader);
			if (userData != null) {
				String userJson = StringCompressor.gzipDecompress(userData, Charsets.UTF_8);
				return Jackson.mobile().readValue(userJson, UserDto.class);
			}
			return null;
		}
		return null;
	}

	/**
	 * <pre>
	 * 获取模块PATH的正则表达式，并缓存起来 URL格式有可能如下：（带后缀、带 jsessionid等） /xxx/xxx /xxx/xxx.htm
	 * /xxx/xxx.htm;jsessionid=28354807766A7EBDBF9B6402AA05B616
	 *
	 * @param path
	 * @return
	 */
	private Pattern getPathPattern(String path) {
		Pattern pattern = pathPatternMap.get(path);
		if (pattern == null) {
			synchronized (this) {
				if (pattern == null) {
					pattern = Pattern.compile(URI_PATH_PATTERN.matcher(path).replaceAll("") + "(/(get|do).*)?");
					pathPatternMap.put(path, pattern);
				}
			}
		}
		return pattern;
	}

	public static void setRequest(HttpServletRequest request, PermissionInterceptor.UserRecover recover) {
		requestLocal.set(request);
		if (recover == null) {
			return;
		}
		request.setAttribute(RECOVER_KEY, recover);
	}

	@Override public UserDto getUser() {
		return (UserDto) requestLocal.get().getAttribute(USER_KEY);
	}

	@Override public SystemDto getCurrSystem() {
		return (SystemDto) requestLocal.get().getAttribute(CURR_SYSTEM_KEY);
	}

	@Override public List<NodeDto> getBreadCrumb() {
		return (List<NodeDto>) requestLocal.get().getAttribute(BREAD_CRUM);
	}

	@Override public NodeDto checkPermission() {
		return (NodeDto) requestLocal.get().getAttribute(CURR_NODE_KEY);
	}

	@Override public NodeDto checkPermission(String urlPath) {
		return this.findNode(urlPath, null);
	}
}
