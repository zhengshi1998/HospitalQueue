package com.sz.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sz.Mapper.OrderMapper;
import com.sz.PatientFeignClient;
import com.sz.RabbitMQ.Constant.RabbitMQConstant;
import com.sz.RabbitMQ.Service.RabbitMQService;
import com.sz.ScheduleFeignClient;
import com.sz.Service.OrderService;
import com.sz.commonutils.Result.Result;
import com.sz.hospital_manage.util.HttpRequestHelper;
import com.sz.model.Order.OrderInfo;
import com.sz.model.User.Patient;
import com.sz.model.VO.hosp.ScheduleOrderVo;
import com.sz.model.VO.msm.MsmVo;
import com.sz.model.VO.order.OrderMqVo;
import com.sz.model.VO.order.SignInfoVo;
import io.swagger.models.auth.In;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private ScheduleFeignClient scheduleFeignClient;

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Override
    public Long createOrder(String scheduleId, Long patientId) {
        ScheduleOrderVo scheduleOrderVo = scheduleFeignClient.getScheduleIdForOrder(scheduleId);
        Patient patient = patientFeignClient.getPatientByIdForOrder(patientId);

//        if(new DateTime(scheduleOrderVo.getStartTime()).isAfterNow() ||
//            new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()){
//            return null;
//        }
        System.out.println(scheduleOrderVo);
        System.out.println(patient);
        SignInfoVo signInfoVo = scheduleFeignClient.getSignKeyForOrder(scheduleOrderVo.getHoscode());
        System.out.println(signInfoVo);
        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setAmount(scheduleOrderVo.getAmount());
        orderInfo.setDepcode(scheduleOrderVo.getDepcode());
        orderInfo.setHoscode(scheduleOrderVo.getHoscode());
        orderInfo.setHosname(scheduleOrderVo.getHosname());

        String outTradeNumber = System.currentTimeMillis() + "" + new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNumber);
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(0);
        orderInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        orderInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        orderInfo.setDeleted(0);
        baseMapper.insert(orderInfo);
        System.out.println("passed insert orderInfo");

        Map<String, Object> map = new HashMap<>();

        map.put("hoscode", orderInfo.getHoscode());
        map.put("depcode", orderInfo.getDepcode());
        map.put("hosScheduleId", orderInfo.getScheduleId());
        map.put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        map.put("reserveTime", orderInfo.getReserveTime());
        map.put("amount", orderInfo.getAmount());
        map.put("name", patient.getName());
        map.put("certificatesType", patient.getCertificatesType());
        map.put("certificatesNo", patient.getContactsCertificatesNo());
        map.put("sex", patient.getSex());
        map.put("birthdate", patient.getBirthdate());
        map.put("phone", patient.getPhone());
        map.put("isMarry", patient.getIsMarry());
        map.put("provinceCode", patient.getProvinceCode());
        map.put("cityCode", patient.getCityCode());
        map.put("districtCode", patient.getDistrictCode());
        map.put("address", patient.getAddress());
        map.put("contactsName", patient.getContactsName());
        map.put("contactsCertificatesType", patient.getContactsCertificatesType());
        map.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        map.put("contactsPhone", patient.getContactsPhone());
        map.put("timestamp", HttpRequestHelper.getTimestamp());
        map.put("sign", signInfoVo.getSignKey());

        System.out.println("send request");
        JSONObject jsonObject = HttpRequestHelper.sendRequest(map, signInfoVo.getApiUrl() + "/order/submitOrder");
        System.out.println(jsonObject +  " passed request");

        if(jsonObject.getInteger("code") == 200){
            Map<String, Object> jsonMap = jsonObject.getJSONObject("data");
            // 得到预约HosRecord对象主键
            Integer hosRecordId = (Integer) jsonMap.get("hosRecordId");
            Integer number = (Integer) jsonMap.get("number");
            String fetchTime = (String) jsonMap.get("fetchTime");
            String fetchAddress = (String) jsonMap.get("fetchAddress");

            orderInfo.setHosRecordId(hosRecordId + "");
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            orderInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            baseMapper.updateById(orderInfo);
            System.out.println("passed updateById");
            // 更新可预约信息
            Integer reservedNumber = (Integer) jsonMap.get("reservedNumber");
            Integer availableNumber = (Integer) jsonMap.get("availableNumber");
            System.out.println(reservedNumber + " " + availableNumber);
            // 调用MQ发短信、更新预约信息
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);
            orderMqVo.setScheduleId(scheduleId);

            // 创建msmVo对象，记录短信发送的信息，供短信发送模块使用
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            msmVo.setTemplateCode("SMS-123123123");
            Map<String, Object> param = new HashMap<>();
            param.put("name", orderInfo.getPatientName());
            param.put("amount", orderInfo.getAmount());
            param.put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
//            param.put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") +
//                                                 (orderInfo.getReserveTime() == 0? "上午" : "下午"));
//            param.put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            msmVo.setParam(map);
            orderMqVo.setMsmVo(msmVo);

            // 发送数据给MQ
            rabbitMQService.sendMessage(RabbitMQConstant.EXCHANGE_DIRECT_ORDER,
                                        RabbitMQConstant.ROUTING_ORDER, orderMqVo);

            return orderInfo.getId();
        } else {
            return null;
        }
    }
}
