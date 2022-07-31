package com.sz.commonutils.VO;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class DataDictVO {
    @ExcelProperty("ID")
    private Long id;

    @ExcelProperty("上级ID")
    private Long parentId;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("值")
    private Long value;

    @ExcelProperty("代码")
    private String dictCode;
}
