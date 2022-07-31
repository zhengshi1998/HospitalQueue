package com.sz.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sz.Mapper.PatientMapper;
import com.sz.Service.PatientService;
import com.sz.client.DataDictFeignClient;
import com.sz.model.User.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DataDictFeignClient dataDictFeignClient;

    @Override
    public List<Patient> findAll(String userId) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        List<Patient> patients = baseMapper.selectList(queryWrapper);

        patients.forEach(this::packagePatient);

        return patients;
    }

    @Override
    public Patient findById(Long id) {
        Patient patient = baseMapper.selectById(id);
        this.packagePatient(patient);
        return patient;
    }

    private void packagePatient(Patient patient){
        String certificate = dataDictFeignClient.getInfo(patient.getCertificatesType(), "CertificatesType").getData().getName();
        String contactCertificate = dataDictFeignClient.getInfo(patient.getContactsCertificatesType(), "CertificatesType").getData().getName();
        String province = dataDictFeignClient.getInfo(patient.getProvinceCode(), "Province").getData().getName();
        String city = dataDictFeignClient.getInfo(patient.getCityCode()).getData().getName();
        String district = dataDictFeignClient.getInfo(patient.getDistrictCode()).getData().getName();

        if(patient.getParam() == null) patient.setParam(new HashMap<String, Object>());

        patient.getParam().put("certificate", certificate);
        patient.getParam().put("contactCertificate", contactCertificate);
        patient.getParam().put("province", province);
        patient.getParam().put("city", city);
        patient.getParam().put("district", district);
    }
}
