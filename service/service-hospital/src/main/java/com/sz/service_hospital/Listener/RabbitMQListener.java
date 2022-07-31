package com.sz.service_hospital.Listener;

import com.rabbitmq.client.Channel;
import com.sz.RabbitMQ.Constant.RabbitMQConstant;
import com.sz.RabbitMQ.Service.RabbitMQService;
import com.sz.model.Hospital.Schedule;
import com.sz.model.VO.msm.MsmVo;
import com.sz.model.VO.order.OrderMqVo;
import com.sz.service_hospital.Service.Impl.ScheduleServiceImpl;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RabbitMQListener {
    @Autowired
    private ScheduleServiceImpl scheduleService;

    @Autowired
    private RabbitMQService rabbitMQService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMQConstant.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = RabbitMQConstant.EXCHANGE_DIRECT_ORDER),
            key = {RabbitMQConstant.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        // 更新预约数
        Schedule schedule = scheduleService.findById(orderMqVo.getScheduleId());
        schedule.setReservedNumber(orderMqVo.getReservedNumber());
        schedule.setAvailableNumber(orderMqVo.getAvailableNumber());

        System.out.println(orderMqVo);
        scheduleService.updateScheduleByMQ(schedule);

        // 发送短信
        MsmVo msmVo = orderMqVo.getMsmVo();
        if(msmVo != null){
            rabbitMQService.sendMessage(RabbitMQConstant.EXCHANGE_DIRECT_MSM,
                                        RabbitMQConstant.ROUTING_MSM_ITEM, msmVo);
        }
    }
}
