package com.cydeer.ups.client.dto;

/**
 * @author zhangsong.
 * @date 2016/10/16 下午10:23
 * 一个系统也是一个节点，一个系统有很多节点，每个节点有多个子节点
 */
public class SystemDto extends NodeDto {
	private String rootPath;

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("\"SystemDto\":").append(super.toString());
		sb.append(", \"rootPath\":\"").append(rootPath).append('\"');
		sb.append('}');
		return sb.toString();
	}
}
