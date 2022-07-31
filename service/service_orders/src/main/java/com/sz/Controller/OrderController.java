package com.sz.Controller;

import com.sz.Service.Impl.OrderServiceImpl;
import com.sz.commonutils.Result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderController {
    @Autowired
    private OrderServiceImpl orderService;

    @ApiOperation("创建订单")
    @PostMapping("/auth/createOrder/{scheduleId}/{patientId}")
    public Result createOrder(@PathVariable("scheduleId") String scheduleId, @PathVariable("patientId") Long patientId){
        Long orderId = orderService.createOrder(scheduleId, patientId);
        return Result.ok(orderId);
    }
}
