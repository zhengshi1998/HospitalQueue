package com.sz;

import com.sz.commonutils.Result.Result;
import com.sz.model.DataDict.DataDictInfo;
import com.sz.model.User.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("service-user")
@Repository
public interface PatientFeignClient {
    @GetMapping("/api/user/patient/inner/getPatientByIdForOrder/{id}")
    public Patient getPatientByIdForOrder(@PathVariable("id") Long id);

}
