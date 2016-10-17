package com.cydeer.ups.client;

import com.cydeer.core.utils.IUtils;
import com.cydeer.ups.client.dto.NodeDto;
import com.cydeer.ups.client.dto.SystemDto;
import com.cydeer.ups.client.dto.UserDto;

import java.util.List;

/**
 * @author zhangsong.
 * @date 2016/10/16 下午9:49
 */
public interface UpsClient extends IUtils {

	UserDto getUser();

	SystemDto getCurrSystem();

	List<NodeDto> getBreadCrumb();

	NodeDto checkPermission();

	NodeDto checkPermission(String urlPath);
}
