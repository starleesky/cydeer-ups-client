package com.cydeer.ups.client;

import com.cydeer.core.utils.Utils;
import com.cydeer.ups.client.dto.NodeDto;
import com.cydeer.ups.client.dto.SystemDto;
import com.cydeer.ups.client.dto.UserDto;

import java.util.List;

/**
 * @author zhangsong.
 * @date 2016/10/17 下午6:42
 */
public class UpsClientApi {
	
	public static UserDto getUser() {
		return Utils.get(UpsClient.class).getUser();
	}

	public static SystemDto getCurrSystem() {
		return Utils.get(UpsClient.class).getCurrSystem();
	}

	public static List<NodeDto> getBreadCrumb() {
		return Utils.get(UpsClient.class).getBreadCrumb();
	}

	public static NodeDto checkPermission() {
		return Utils.get(UpsClient.class).checkPermission();
	}

	public static NodeDto checkPermission(String urlPath) {
		return Utils.get(UpsClient.class).checkPermission(urlPath);
	}

}
