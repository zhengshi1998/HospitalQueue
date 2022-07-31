package com.sz.data_dict.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sz.model.DataDict.DataDictInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataDictMapper extends BaseMapper<DataDictInfo> {

}
