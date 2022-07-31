package com.sz;

import com.sz.commonutils.Result.Result;
import com.sz.model.VO.hosp.ScheduleOrderVo;
import com.sz.model.VO.order.SignInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-hospital")
@Repository
public interface ScheduleFeignClient {
    @GetMapping("/api/hosp/hospital/inner/getScheduleIdForOrder/{id}")
    public ScheduleOrderVo getScheduleIdForOrder(@PathVariable("id") String id);

    @GetMapping("/api/hosp/hospital/inner/getSignKey/{hoscode}")
    public SignInfoVo getSignKeyForOrder(@PathVariable("hoscode") String hoscode);
}
