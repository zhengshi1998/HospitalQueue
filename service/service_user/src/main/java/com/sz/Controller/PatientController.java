package com.sz.Controller;

import com.sz.Service.Impl.PatientServiceImpl;
import com.sz.commonutils.Result.Result;
import com.sz.model.User.Patient;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/patient")
public class PatientController {
    @Autowired
    private PatientServiceImpl patientService;

    @ApiOperation("获取就诊人List")
    @GetMapping("/auth/findAllPatients")
    public Result findAllPatients(String userId){
        List<Patient> ls = patientService.findAll(userId);
        return Result.ok(ls);
    }

    @ApiOperation("添加就诊人")
    @PostMapping("/auth/savePatient")
    public Result savePatient(@RequestBody Patient patient){
        patient.setUserId(1L);
        patientService.save(patient);
        return Result.ok();
    }

    @ApiOperation("获取就诊人信息")
    @GetMapping("/auth/getPatientById/{id}")
    public Result getPatientById(@PathVariable("id") Long id){
        Patient patient = patientService.findById(id);
        return Result.ok();
    }

    @ApiOperation("根据id获取就诊人信息，供远程调用")
    @GetMapping("/inner/getPatientByIdForOrder/{id}")
    public Patient getPatientByIdForOrder(@PathVariable("id") Long id){
        Patient patient = patientService.findById(id);
        return patient;
    }

    @ApiOperation("更新就诊人信息")
    @PostMapping("/auth/updatePatient")
    public Result updatePatient(@RequestBody Patient patient){
        patientService.updateById(patient);
        return Result.ok();
    }

    @ApiOperation("删除就诊人信息")
    @GetMapping("/auth/deletePatientById/{id}")
    public Result deletePatientById(@PathVariable("id") Long id){
        patientService.removeById(id);
        return Result.ok();
    }
}
