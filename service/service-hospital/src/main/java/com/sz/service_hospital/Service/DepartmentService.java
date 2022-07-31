package com.sz.service_hospital.Service;

import com.sz.model.Hospital.Department;
import com.sz.model.VO.hosp.DepartmentVo;
import com.sz.service_hospital.Service.Impl.DepartmentServiceImpl;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    Boolean save(Map<String, Object> stringObjectMap);

    Boolean remove(String hoscode, String depcode);
    Page<Department> page(int page, int limit, String hoscode);

    Department findDepByDepcode(String depcode);

    List<DepartmentVo> findDepList(String hoscode);
}
