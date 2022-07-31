package com.sz.service_hospital.Controller;

import com.sz.commonutils.Result.Result;
import com.sz.model.Hospital.Schedule;
import com.sz.service_hospital.Service.Impl.ScheduleServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleServiceImpl scheduleService;

    @ApiOperation("根据科室编号、医院编号查询排班数据")
    @GetMapping("/getSchedule/{page}/{limit}/{hoscode}/{depcode}")
    public Result getSchedule(@PathVariable("page") long page, @PathVariable("limit") long limit,
                              @PathVariable("hoscode") String hoscode, @PathVariable("depcode") String depcode){
        Map<String, Object> map = scheduleService.findScheduleByHoscodeAndDepcode(page, limit, hoscode, depcode);
        return Result.ok(map);
    }

    @ApiOperation("根据科室编号、医院编号、日期查询详细排班数据")
    @GetMapping("/getScheduleDetail/{hoscode}/{depcode}/{date}")
    public Result getScheduleDetail(@PathVariable("date") String date, @PathVariable("hoscode") String hoscode,
                                    @PathVariable("depcode") String depcode){
        List<Schedule> schedules = scheduleService.findScheduleByHoscodeAndDepcodeAndDate(hoscode, depcode, date);
        return Result.ok(schedules);
    }
}
