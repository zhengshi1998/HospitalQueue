package com.sz.service_hospital.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sz.model.Hospital.Schedule;
import com.sz.model.VO.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ScheduleService {
    Boolean save(Map<String, Object> stringObjectMap);

    Page<Schedule> page(int page, int limit, String hoscode);

    Boolean remove(String hoscode, String scheduleId);

    Map<String, Object> findScheduleByHoscodeAndDepcode(long page, long limit, String hoscode, String depcode);

    List<Schedule> findScheduleByHoscodeAndDepcodeAndDate(String hoscode, String depcode, String date);

    Map<String, Object> pageScheduleByHoscodeAndDepcode(Integer page, Integer limit, String hoscode, String depcode);

    Schedule findById(String id);

    ScheduleOrderVo findByIdForOrder(String id);

    Boolean updateScheduleByMQ(Schedule schedule);
}
