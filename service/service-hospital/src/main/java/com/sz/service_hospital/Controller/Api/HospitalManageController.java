package com.sz.service_hospital.Controller.Api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sz.commonutils.Result.Result;
import com.sz.hospital_manage.util.HttpRequestHelper;
import com.sz.hospital_manage.util.MD5;
import com.sz.model.Hospital.Hospital;
import com.sz.model.Hospital.HospitalSetting;
import com.sz.service_hospital.Service.Impl.DepartmentServiceImpl;
import com.sz.service_hospital.Service.Impl.HospitalManageServiceImpl;
import com.sz.service_hospital.Service.Impl.HospitalSettingServiceImpl;
import com.sz.service_hospital.Service.Impl.ScheduleServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class HospitalManageController {
    @Autowired
    private HospitalManageServiceImpl hospitalService;

    @Autowired
    private HospitalSettingServiceImpl hospitalSettingService;

    @Autowired
    DepartmentServiceImpl departmentService;

    @Autowired
    ScheduleServiceImpl scheduleService;

    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        String inputSign = (String) stringObjectMap.get("sign");

        QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hospital_code", stringObjectMap.get("hoscode"));
        HospitalSetting res = hospitalSettingService.getOne(queryWrapper);

        if(res == null || !MD5.encrypt(res.getSignKey()).equals(inputSign)){
            return Result.fail();
        }

        String hoscode = (String) stringObjectMap.get("hoscode");
        String scheduleId = (String) stringObjectMap.get("hosScheduleId");

        return Result.ok(scheduleService.remove(hoscode, scheduleId));
    }

    @PostMapping("/schedule/list")
    public Result listSchedules(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        String inputSign = (String) stringObjectMap.get("sign");

        QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hospital_code", stringObjectMap.get("hoscode"));
        HospitalSetting res = hospitalSettingService.getOne(queryWrapper);

        if(res == null || !MD5.encrypt(res.getSignKey()).equals(inputSign)){
            return Result.fail();
        }

        String hoscode = (String) stringObjectMap.get("hoscode");
//        String depcode = (String) stringObjectMap.get("depcode");
        int page = StringUtils.isEmpty((String) stringObjectMap.get("page"))? 1 : Integer.parseInt((String) stringObjectMap.get("page"));
        int limit = StringUtils.isEmpty((String) stringObjectMap.get("limit"))? 10 : Integer.parseInt((String) stringObjectMap.get("limit"));
        return Result.ok(scheduleService.page(page, limit, hoscode));
    }

    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        String inputSign = (String) stringObjectMap.get("sign");

        QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hospital_code", stringObjectMap.get("hoscode"));
        HospitalSetting res = hospitalSettingService.getOne(queryWrapper);

        if(res == null || !MD5.encrypt(res.getSignKey()).equals(inputSign)){
            return Result.fail();
        }

        if(scheduleService.save(stringObjectMap)) return Result.ok();
        else return Result.fail();
    }

    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        String inputSign = (String) stringObjectMap.get("sign");

        QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hospital_code", stringObjectMap.get("hoscode"));
        HospitalSetting res = hospitalSettingService.getOne(queryWrapper);

        if(res == null || !MD5.encrypt(res.getSignKey()).equals(inputSign)){
            return Result.fail();
        }

        departmentService.remove((String) stringObjectMap.get("hoscode"), (String) stringObjectMap.get("depcode"));
        return Result.ok();
    }

    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);
        String inputSign = (String) stringObjectMap.get("sign");

        QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hospital_code", stringObjectMap.get("hoscode"));
        HospitalSetting res = hospitalSettingService.getOne(queryWrapper);

        if(res == null || !MD5.encrypt(res.getSignKey()).equals(inputSign)){
            return Result.fail();
        }

        departmentService.save(stringObjectMap);
        return Result.ok();
    }

    @PostMapping("/department/list")
    public Result listDepartments(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

        String inputSign = (String) stringObjectMap.get("sign");

        QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hospital_code", stringObjectMap.get("hoscode"));
        HospitalSetting res = hospitalSettingService.getOne(queryWrapper);

        if(res == null || !MD5.encrypt(res.getSignKey()).equals(inputSign)){
            return Result.fail();
        }

        String hoscode = (String) stringObjectMap.get("hoscode");
        int page = StringUtils.isEmpty((String) stringObjectMap.get("page"))? 1 : Integer.parseInt((String) stringObjectMap.get("page"));
        int limit = StringUtils.isEmpty((String) stringObjectMap.get("limit"))? 10 : Integer.parseInt((String) stringObjectMap.get("limit"));

        return Result.ok(departmentService.page(page, limit, hoscode));
    }

    @PostMapping("/hospital/show")
    public Result showHospital(HttpServletRequest request){
        try{
            Map<String, String[]> parameterMap = request.getParameterMap();
            Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

            String inputSign = (String) stringObjectMap.get("sign");

            QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("hospital_code", stringObjectMap.get("hoscode"));
            HospitalSetting res = hospitalSettingService.getOne(queryWrapper);
            if(res != null && MD5.encrypt(res.getSignKey()).equals(inputSign)){
                Hospital result = hospitalService.findByHospitalCode((String) stringObjectMap.get("hoscode"));
                return Result.ok(result);
            } else {
                return Result.fail();
            }
        }catch(Exception e){
            e.printStackTrace();
            return Result.fail();
        }
    }

    @PostMapping("/saveHospital")
    public Result saveHosp(HttpServletRequest httpServletRequest){
        try{
            Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
            Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(parameterMap);

            String inputSign = (String) stringObjectMap.get("sign");

            QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("hospital_code", stringObjectMap.get("hoscode"));
            HospitalSetting res = hospitalSettingService.getOne(queryWrapper);

            if(res != null && MD5.encrypt(res.getSignKey()).equals(inputSign)){
                String logoData = (String) stringObjectMap.get("logoData");
                String newLogoData = logoData.replaceAll(" ", "+");
                stringObjectMap.put("logoData", newLogoData);
                hospitalService.save(stringObjectMap);
                return Result.ok();
            } else {
                return Result.fail();
            }
        }catch(Exception e){
            e.printStackTrace();
            return Result.fail();
        }
    }

    @ApiOperation("更新状态")
    @GetMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable("id") String id, @PathVariable("status") int status){
        Boolean res = hospitalService.updateStatus(id, status);
        if(res) return Result.ok();
        else return Result.fail();
    }

    @ApiOperation("查询详情")
    @GetMapping("/showHospitalDetails/{id}")
    public Result showHospitalDetails(@PathVariable("id") String id){
        Hospital byId = hospitalService.findById(id);
        return Result.ok(byId);
    }

}
