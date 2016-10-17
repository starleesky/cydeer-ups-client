package com.cydeer.ups.client.dto;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangsong.
 * @date 2016/10/16 下午10:16
 */
public class UserDto implements Serializable {
	private Integer id;
	private String nickName;
	private String realName;
	private List<SystemDto> systems;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public List<SystemDto> getSystems() {
		return systems;
	}

	public void setSystems(List<SystemDto> systems) {
		this.systems = systems;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("  \"id\":").append(id);
		sb.append(", \"nickName\":\"").append(nickName).append('\"');
		sb.append(", \"realName\":\"").append(realName).append('\"');
		sb.append(", \"systems\":").append(systems);
		sb.append('}');
		return sb.toString();
	}
}
