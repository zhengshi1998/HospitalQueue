package com.sz.service_hospital.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.sz.client.DataDictFeignClient;
import com.sz.model.DataDict.DataDictInfo;
import com.sz.model.Hospital.Hospital;
import com.sz.service_hospital.Service.HospitalManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HospitalManageServiceImpl implements HospitalManageService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private DataDictFeignClient dataDictFeignClient;

    @Override
    public Hospital findByHospitalCode(String hoscode) {
        Query query = new Query(Criteria.where("hoscode").is(hoscode));
        return mongoTemplate.findOne(query, Hospital.class);
    }

    @Override
    public boolean save(Map<String, Object> parameterMap) {
        // 构建hospital对象
        String s = JSONObject.toJSONString(parameterMap);
        Hospital hospital = JSONObject.parseObject(s, Hospital.class);

        String hoscode = hospital.getHoscode();
        Query q1 = new Query(Criteria.where("hoscode").is(hoscode));
        List<Hospital> hospitals = mongoTemplate.find(q1, Hospital.class);
        Hospital hospitalByHoscode = hospitals.size() > 0? hospitals.get(0) : null;

        if(hospitalByHoscode == null){
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            mongoTemplate.insert(hospital);
        } else {
            hospital.setStatus(hospitalByHoscode.getStatus());
            hospital.setCreateTime(hospitalByHoscode.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            mongoTemplate.insert(hospital);
        }
        return true;
    }

    @Override
    public Boolean updateStatus(String id, int status) {
        Query query = new Query(
                Criteria.where("id").is(id)
        );
        Update update = new Update();
        update.set("status", status);
        mongoTemplate.upsert(query, update, Hospital.class);
        return true;
    }

    @Override
    public Page<Hospital> findWithPagingAndConditions(int page, int limit, Hospital hospital) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Query query = new Query();

        if(hospital.getHosname() != null && !hospital.getHosname().equals("")){
            query.addCriteria(Criteria.where("hosname").regex(".*" + hospital.getHosname() + ".*"));
        }

        if(hospital.getHoscode() != null && !hospital.getHoscode().equals("")){
            query.addCriteria(Criteria.where("hoscode").regex(".*" + hospital.getHoscode() + ".*"));
        }

        long count = mongoTemplate.count(query, Hospital.class);
        List<Hospital> hospitals = mongoTemplate.find(query.with(pageable), Hospital.class);

        hospitals.stream().forEach(curHospital -> {
            curHospital = this.setType(curHospital);
        });
        return new PageImpl<Hospital>(hospitals, pageable, count);
    }

    @Override
    public Hospital findById(String id) {
        return mongoTemplate.findById(id, Hospital.class);
    }

    @Override
    public List<Hospital> findByHosname(String hosname) {
        Query query = new Query(
                Criteria.where("hosname").is(hosname)
        );
        return mongoTemplate.find(query, Hospital.class);
    }

    private Hospital setType(Hospital hospital){
        DataDictInfo hostype = dataDictFeignClient.getInfo(hospital.getHostype(), "Hostype").getData();
        DataDictInfo province = dataDictFeignClient.getInfo(hospital.getProvinceCode()).getData();
        DataDictInfo city = dataDictFeignClient.getInfo(hospital.getCityCode()).getData();
        hospital.getParam().put("hostypeString", hostype.getName());
        hospital.getParam().put("provinceString", province.getName());
        hospital.getParam().put("cityString", city.getName());
        return hospital;
    }


}
