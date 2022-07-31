package com.sz.service_hospital.Controller;

import com.sz.commonutils.Result.Result;
import com.sz.model.Hospital.Hospital;
import com.sz.service_hospital.Service.Impl.HospitalManageServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalControllerRemote {
    @Autowired
    private HospitalManageServiceImpl hospitalManageService;

    @ApiOperation(value = "远程调用获取医院信息")
    @GetMapping("/list/{page}/{limit}")
    public Result listHospital(@PathVariable("page") int page,
                               @PathVariable("limit") int limit,
                               Hospital hospital){
        Page<Hospital> res = hospitalManageService.findWithPagingAndConditions(page, limit, hospital);
        return Result.ok(res);
    }
}
