package com.sz.service_hospital.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sz.commonutils.Result.Result;
import com.sz.commonutils.VO.HospitalSettingsVO.HospitalSettingNameAndCode;
import com.sz.model.Hospital.HospitalSetting;
import com.sz.service_hospital.Service.HospitalSettingService;
import com.sz.service_hospital.Service.Impl.HospitalSettingServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/admin/hosp/hospitalSetting")
public class HospitalSettingController {
    @Autowired
    private HospitalSettingServiceImpl hospitalSettingService;

    @ApiOperation(value = "获取所有医院设置")
    @GetMapping("/findAll")
    public Result findAll(){
        List<HospitalSetting> list = hospitalSettingService.list();
        if(list != null) return Result.ok(list);
        else return Result.fail();
    }

    @ApiOperation(value = "删除医院设置")
    @DeleteMapping("/{id}")
    public Result removeById(@PathVariable("id") Long id){
        if(hospitalSettingService.removeById(id)) return Result.ok();
        else return Result.fail();
    }

    @ApiOperation(value = "条件查询医院设置")
    @PostMapping("/page/{currentPage}/{size}")
    public Result findByConditionAndPage(@PathVariable("currentPage") int currentPage,
                                         @PathVariable("size") int size,
                                         @RequestBody(required = false) HospitalSettingNameAndCode hospitalVO){
        String hospitalName = hospitalVO.getName();
        String hospitalCode = hospitalVO.getCode();

        Page<HospitalSetting> page = new Page<>(currentPage, size);
        QueryWrapper<HospitalSetting> queryWrapper = new QueryWrapper<>();

        if(hospitalName != null && !hospitalName.equals("")){
            queryWrapper.like("hospital_name", hospitalName);
        }

        if(hospitalCode != null && !hospitalCode.equals("")){
            queryWrapper.eq("hospital_code", hospitalCode);
        }
        Page<HospitalSetting> queryResult = hospitalSettingService.page(page, queryWrapper);
        if(queryResult != null){
            return Result.ok(queryResult);
        }else return Result.fail();
    }

    @ApiOperation(value = "添加医院设置")
    @PostMapping("/save")
    public Result save( @RequestBody(required = false) HospitalSetting hospitalSetting){
        Random random = new Random();
        String key = System.currentTimeMillis() + "" + random.nextInt(1000);
        hospitalSetting.setSignKey(MD5Encoder.encode(key.getBytes()));
        if(hospitalSettingService.save(hospitalSetting)){
            return Result.ok();
        }else return Result.fail();
    }

    @ApiOperation(value = "根据ID获取医院设置")
    @GetMapping("/getById/{id}")
    public Result findById(@PathVariable("id") Long id){
        HospitalSetting queryResult = hospitalSettingService.getById(id);
        if(queryResult != null){
            return Result.ok(queryResult);
        }else return Result.fail();
    }

    @ApiOperation("根据id更新医院设置信息")
    @PostMapping("/update")
    public Result update(@RequestBody(required = false) HospitalSetting hospitalSetting){
        if(hospitalSettingService.updateById(hospitalSetting)) return Result.ok();
        else return Result.fail();
    }

    @ApiOperation("根据ID批量删除")
    @PostMapping("/deleteBatch")
    public Result deleteBatchByIds(@RequestBody(required = false) List<Long> ids){
        if(hospitalSettingService.removeBatchByIds(ids)) return Result.ok();
        else return Result.fail();
    }

    @ApiOperation("更新状态")
    @PutMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable("id") Long id,
                               @PathVariable("status") Integer status){
        HospitalSetting h = hospitalSettingService.getById(id);
        h.setStatus(status);
        if(hospitalSettingService.updateById(h)) return Result.ok();
        else return Result.fail();
    }

    @ApiOperation("发送秘钥")
    @PutMapping("/sendKey/{id}")
    public Result sendKey(@PathVariable("id") Long id){
        try{
            HospitalSetting hospitalSetting = hospitalSettingService.getById(id);
            String key = hospitalSetting.getSignKey();
            String code = hospitalSetting.getHospitalCode();
            return Result.ok();
        }catch (Exception e){
            return Result.fail();
        }
    }


}
