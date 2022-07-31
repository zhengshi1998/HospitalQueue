package com.sz.service_hospital.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sz.model.Hospital.HospitalSetting;
import com.sz.service_hospital.Mapper.HospitalSettingsMapper;
import com.sz.service_hospital.Service.HospitalSettingService;
import org.springframework.stereotype.Service;

@Service
public class HospitalSettingServiceImpl extends ServiceImpl<HospitalSettingsMapper, HospitalSetting> implements HospitalSettingService {
}
