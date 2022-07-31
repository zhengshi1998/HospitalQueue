package com.sz.service_hospital.Controller.Api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sz.commonutils.Result.Result;
import com.sz.model.Hospital.Hospital;
import com.sz.model.Hospital.HospitalSetting;
import com.sz.model.Hospital.Schedule;
import com.sz.model.VO.hosp.DepartmentVo;
import com.sz.model.VO.hosp.ScheduleOrderVo;
import com.sz.model.VO.order.SignInfoVo;
import com.sz.service_hospital.Service.Impl.DepartmentServiceImpl;
import com.sz.service_hospital.Service.Impl.HospitalManageServiceImpl;
import com.sz.service_hospital.Service.Impl.HospitalSettingServiceImpl;
import com.sz.service_hospital.Service.Impl.ScheduleServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp/hospital")
public class ApiHospitalController {
    @Autowired
    private HospitalManageServiceImpl hospitalManageService;

    @Autowired
    private DepartmentServiceImpl departmentService;

    @Autowired
    private ScheduleServiceImpl scheduleService;

    @Autowired
    private HospitalSettingServiceImpl hospitalSettingService;

    @ApiOperation("")
    @GetMapping("findHospList/{page}/{limit}")
    public Result findHospList(@PathVariable("page") int page, @PathVariable("limit") int limit,
                                Hospital hospital){
        Page<Hospital> res = hospitalManageService.findWithPagingAndConditions(page, limit, hospital);
        return Result.ok(res);
    }

    @ApiOperation("根据医院名称查询")
    @GetMapping("findHospByHosname/{hosname}")
    public Result findHospByHosname(@PathVariable("hosname") String hosname){
        List<Hospital> hospitalList = hospitalManageService.findByHosname(hosname);
        return Result.ok(hospitalList);
    }

    @ApiOperation("根据医院编号获取科室")
    @GetMapping("findDeptByHoscode/{hoscode}")
    public Result findDeptByHoscode(@PathVariable("hoscode") String hoscode){
        List<DepartmentVo> depList = departmentService.findDepList(hoscode);
        return Result.ok(depList);
    }

    @ApiOperation("根据医院编号获取预约挂号详情")
    @GetMapping("findHospDetailByHoscode/{hoscode}")
    public Result findHospDetailByHoscode(@PathVariable("hoscode") String hoscode){
        Hospital res = hospitalManageService.findByHospitalCode(hoscode);
        return Result.ok(res);
    }

    @ApiOperation("获取所有可预约的排班")
    @GetMapping("/pageSchedules/{page}/{limit}/{hoscode}/{depcode}")
    public Result pageSchedules(@PathVariable("page") Integer page, @PathVariable("limit") Integer limit,
                                @PathVariable("hoscode") String hoscode, @PathVariable("depcode") String depcode){
        Map<String, Object> stringObjectMap = scheduleService.pageScheduleByHoscodeAndDepcode(page, limit, hoscode, depcode);
        return Result.ok(stringObjectMap);
    }

    @ApiOperation("获取指定日期可预约的Schedule")
    @GetMapping("/getSchedulesByDate/{hoscode}/{depcode}/{date}")
    public Result getSchedulesByDate(@PathVariable("date") String date, @PathVariable("hoscode") String hoscode,
                                     @PathVariable("depcode") String depcode){
        List<Schedule> res = scheduleService.findScheduleByHoscodeAndDepcodeAndDate(hoscode, depcode, date);
        return Result.ok(res);
    }

    @ApiOperation("获取指定Id的Schedule")
    @GetMapping("/findScheduleById/{id}")
    public Result findScheduleById(@PathVariable("id") String id){
        Schedule schedule = scheduleService.findById(id);
        return Result.ok(schedule);
    }

    @ApiOperation("根据排班id获取数据, 供远程调用")
    @GetMapping("/inner/getScheduleIdForOrder/{id}")
    public ScheduleOrderVo getScheduleIdForOrder(@PathVariable("id") String id){
        ScheduleOrderVo schedule = scheduleService.findByIdForOrder(id);
        return schedule;
    }

    @ApiOperation("id获取医院的sign key")
    @GetMapping("/inner/getSignKey/{hoscode}")
    public SignInfoVo getSignKeyForOrder(@PathVariable("hoscode") String hoscode){
        QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hospital_code", hoscode);
        HospitalSetting hospital = hospitalSettingService.getOne(queryWrapper);

        if(hospital != null) {
            SignInfoVo signInfoVo = new SignInfoVo();
            signInfoVo.setSignKey(hospital.getSignKey());
            signInfoVo.setApiUrl(hospital.getApiURL());
            return signInfoVo;
        }
        else return null;
    }
}
