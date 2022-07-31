package com.sz.service_hospital.Service;

import com.sz.model.Hospital.Hospital;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalManageService {

    Hospital findByHospitalCode(String hoscode);
    boolean save(Map<String, Object> parameterMap);

    Boolean updateStatus(String id, int status);
    Page<Hospital> findWithPagingAndConditions(int page, int limit, Hospital hospital);


    Hospital findById(String id);

    List<Hospital> findByHosname(String hosname);
}
