package com.sz.model.DataDict;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sz.model.Base.BaseEntity;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("data_dict")
public class DataDictInfo extends BaseEntity {
    @TableField(value = "parent_id")
    private Long parentId;

    @TableField(value = "name")
    private String name;

    @TableField(value = "value")
    private Long value;

    @TableField(value = "dict_code")
    private String dictCode;

    @TableField(exist = false)
    private boolean hasChildren;
}
