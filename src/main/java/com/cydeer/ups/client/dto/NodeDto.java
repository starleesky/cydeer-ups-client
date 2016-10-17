package com.cydeer.ups.client.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangsong.
 * @date 2016/10/16 下午10:30
 * 节点有名称，路径，类型，是否启用，以及子节点
 */
public class NodeDto implements Serializable {
	/**
	 * 节点id
	 */
	private Integer id;
	/**
	 * 节点名称
	 */
	private String name;
	/**
	 * 节点path
	 */
	private String path;
	/**
	 * 节点类型，含义：0-系统；1-模块组；2-模块；3-操作；
	 */
	private Integer type;
	/**
	 * 是否启用
	 */
	private boolean active;

	/**
	 * 子模块（节点）
	 */
	private List<NodeDto> children = new ArrayList<NodeDto>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<NodeDto> getChildren() {
		return children;
	}

	public void setChildren(List<NodeDto> children) {
		this.children = children;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("  \"id\":").append(id);
		sb.append(", \"name\":\"").append(name).append('\"');
		sb.append(", \"path\":\"").append(path).append('\"');
		sb.append(", \"type\":").append(type);
		sb.append(", \"active\":").append(active);
		sb.append(", \"children\":").append(children);
		sb.append('}');
		return sb.toString();
	}
}
