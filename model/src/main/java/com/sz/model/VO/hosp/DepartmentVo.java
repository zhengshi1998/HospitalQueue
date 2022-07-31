package com.sz.model.VO.hosp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Department")
public class DepartmentVo {

	@ApiModelProperty(value = "科室编号")
	private String depcode;

	@ApiModelProperty(value = "科室名称")
	private String depname;

	@ApiModelProperty(value = "下级节点")
	private List<DepartmentVo> children;

	public DepartmentVo(String depcode, String depname) {
		this.depcode = depcode;
		this.depname = depname;
	}

	public DepartmentVo(String depcode, String depname, List<DepartmentVo> children) {
		this.depcode = depcode;
		this.depname = depname;
		this.children = children;
	}
}

