package com.sz.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sz.model.Order.OrderInfo;

public interface OrderService extends IService<OrderInfo> {
    Long createOrder(String scheduleId, Long patientId);
}
