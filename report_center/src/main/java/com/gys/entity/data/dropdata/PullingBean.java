package com.gys.entity.data.dropdata;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PullingBean {
	/**
	 * 编号
	 */
	private String number;
	/**
	 * 名称
	 */
	private String name;
	public PullingBean(String number, String name){
		this.number = number;
		this.name = number + " " + name;
	}
	public PullingBean(String number, String name, Integer type){
		this.number = number;
		this.name = name;
	}
}
