package com.sz.service_hospital.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.DeleteResult;
import com.sz.model.Hospital.Department;
import com.sz.model.Hospital.Hospital;
import com.sz.model.VO.hosp.DepartmentVo;
import com.sz.service_hospital.Service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Boolean save(Map<String, Object> stringObjectMap) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(stringObjectMap), Department.class);
        Query query = new Query(
                Criteria.where("hoscode")
                        .is(department.getHoscode())
                        .and("depcode")
                        .is(department.getDepcode()));

        Department res = mongoTemplate.findOne(query, Department.class);

        if(res != null){
            res.setUpdateTime(new Date());
            res.setIsDeleted(0);
            mongoTemplate.save(res);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            mongoTemplate.save(department);
        }

        return true;
    }

    @Override
    public Boolean remove(String hoscode, String depcode) {
        Query query = new Query(
                Criteria.where("hoscode")
                        .is(hoscode)
                        .and("depcode")
                        .is(depcode)
        );
        mongoTemplate.remove(query, Department.class);
        return true;
    }

    @Override
    public Page<Department> page(int page, int limit, String hoscode) {

        Pageable pageable = PageRequest.of(page - 1, limit);
        Query query = new Query(
                Criteria.where("hoscode")
                        .is(hoscode)
        );

        long count = mongoTemplate.count(query, Department.class);

        List<Department> departments = mongoTemplate.find(query.with(pageable), Department.class);

        return new PageImpl<>(departments, pageable, count);
    }

    @Override
    public Department findDepByDepcode(String depcode) {
        Query q = new Query(
                Criteria.where("depcode").is(depcode)
        );

        return mongoTemplate.findOne(q, Department.class);
    }

    @Override
    public List<DepartmentVo> findDepList(String hoscode) {
        Query query = new Query(
                Criteria.where("hoscode").is(hoscode)
        );

        List<Department> departments = mongoTemplate.find(query, Department.class);
        List<DepartmentVo> ret = new ArrayList<>();

        if(departments != null){
            for(Department department : departments){
                boolean flag = false;
                for(DepartmentVo departmentVo : ret){
                    if(departmentVo.getDepcode().equals(department.getBigcode())
                    && departmentVo.getDepname().equals(department.getBigname())){
                        flag = true;
                        departmentVo.getChildren().add(
                                new DepartmentVo(department.getDepcode(), department.getDepname())
                        );
                        break;
                    }
                }
                if(!flag){
                    ret.add(new DepartmentVo(department.getBigcode(), department.getBigname(), new ArrayList<>()));
                    ret.get(ret.size()-1).getChildren().add(new DepartmentVo(department.getDepcode(), department.getDepname()));
                }
            }
            return ret;
        } else return null;
    }
}
