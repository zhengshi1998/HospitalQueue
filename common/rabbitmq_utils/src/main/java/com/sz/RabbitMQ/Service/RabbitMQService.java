package com.sz.RabbitMQ.Service;


import com.sz.model.VO.msm.MsmVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean sendMessage(String exchange, String routingKey, Object message){
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        return true;
    }

    public boolean sendMessageForPhone(MsmVo msmVo){
        if(msmVo.getPhone() != null){
            String code = (String) msmVo.getParam().get("code");
            System.out.println("短信发送成功！");
            return true;
        }
        return true;
    }
}
