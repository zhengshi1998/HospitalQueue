package com.sz.model.Hospital;

import com.baomidou.mybatisplus.annotation.*;
import com.sz.model.Base.BaseEntity;
import lombok.Data;

@Data
@TableName("hospital_settings")
public class HospitalSetting extends BaseEntity {

    @TableField(value = "hospital_name")
    private String hospitalName;

    @TableField(value = "hospital_code")
    private String hospitalCode;

    @TableField(value = "api_url")
    private String apiURL;

    @TableField(value = "sign_key")
    private String signKey;

    @TableField(value = "contacts_name")
    private String contactsName;

    @TableField(value = "contacts_phone")
    private String contactsPhone;

    @TableField(value = "status", fill = FieldFill.INSERT)
    private Integer status;
}
