package com.sz.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sz.model.Order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
}
