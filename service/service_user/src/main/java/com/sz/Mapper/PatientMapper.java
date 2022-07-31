package com.sz.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sz.model.User.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}
