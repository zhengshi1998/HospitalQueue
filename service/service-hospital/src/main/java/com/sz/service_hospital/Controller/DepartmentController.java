package com.sz.service_hospital.Controller;

import com.sz.commonutils.Result.Result;
import com.sz.model.VO.hosp.DepartmentVo;
import com.sz.service_hospital.Service.Impl.DepartmentServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {
    @Autowired
    private DepartmentServiceImpl departmentService;

    @ApiOperation("根据医院编号查询科室列表")
    @GetMapping("/getDepList/{hoscode}")
    public Result getDepList(@PathVariable("hoscode") String hoscode){
        List<DepartmentVo> departmentVoList = departmentService.findDepList(hoscode);
        return Result.ok(departmentVoList);
    }
}
