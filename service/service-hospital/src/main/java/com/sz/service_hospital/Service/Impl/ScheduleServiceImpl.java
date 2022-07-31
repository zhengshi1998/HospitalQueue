package com.sz.service_hospital.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sz.model.Hospital.BookingRule;
import com.sz.model.Hospital.Department;
import com.sz.model.Hospital.Hospital;
import com.sz.model.Hospital.Schedule;
import com.sz.model.VO.hosp.BookingScheduleRuleVo;
import com.sz.model.VO.hosp.ScheduleOrderVo;
import com.sz.service_hospital.Service.ScheduleService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalManageServiceImpl hospitalService;

    @Autowired
    private DepartmentServiceImpl departmentService;

    @Override
    public Boolean save(Map<String, Object> stringObjectMap) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(stringObjectMap), Schedule.class);
        Query query = new Query(
                Criteria.where("hoscode")
                        .is(schedule.getHoscode())
                        .and("hosScheduleId")
                        .is(schedule.getHosScheduleId())
        );
        Schedule res = mongoTemplate.findOne(query, Schedule.class);

        if(res == null){
            schedule.setUpdateTime(new Date());
            schedule.setCreateTime(new Date());
            schedule.setIsDeleted(0);
            schedule.setStatus(1);
            mongoTemplate.save(schedule);
        } else {
            Update update = new Update();
            update.set("updateTime", new Date());
            update.set("status", 1);
            update.set("isDeleted", 0);
            mongoTemplate.upsert(query, update, Schedule.class, "Schedule");
        }
        return true;
    }

    @Override
    public Page<Schedule> page(int page, int limit, String hoscode) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Query query = new Query(
                Criteria.where("hoscode")
                        .is(hoscode)
        );

        long count = mongoTemplate.count(query, Schedule.class);

        List<Schedule> schedules = mongoTemplate.find(query.with(pageable), Schedule.class);

        return new PageImpl<>(schedules, pageable, count);
    }

    @Override
    public Boolean remove(String hoscode, String scheduleId) {
        Query query = new Query(
                Criteria.where("hoscode")
                        .is(hoscode)
                        .and("hosScheduleId")
                        .is(scheduleId)
        );
        mongoTemplate.remove(query, Schedule.class);
        return true;
    }

    @Override
    public Map<String, Object> findScheduleByHoscodeAndDepcode(long page, long limit, String hoscode, String depcode) {
        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                                    .and("depcode").is(depcode);

        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(criteria),
            Aggregation.group("workDate")
                    .first("workDate").as("workDate")
                    .count().as("docCount")
                    .sum("reservedNumber").as("reservedNumber")
                    .sum("availableNumber").as("availableNumber"),
            Aggregation.sort(Sort.Direction.DESC, "workDate"),
            Aggregation.skip((page - 1) * limit),
            Aggregation.limit(limit)

        );
        AggregationResults<BookingScheduleRuleVo> aggRes = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggRes.getMappedResults();

        Aggregation aggTotal = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );

        AggregationResults<BookingScheduleRuleVo> aggTotalRes = mongoTemplate.aggregate(aggTotal, Schedule.class, BookingScheduleRuleVo.class);
        int totalSize= aggTotalRes.getMappedResults().size();

        for(BookingScheduleRuleVo b : mappedResults){
            b.setDayOfWeek(this.getDayByDate(new DateTime(b.getWorkDate())));
        }


        Query q = new Query(
                Criteria.where("hoscode").is(hoscode)
        );

        String hosname = mongoTemplate.findOne(q, Hospital.class).getHosname();

        // 最终数据返回
        Map<String, Object> map = new HashMap<>();
        map.put("schedules", mappedResults);
        map.put("total", totalSize);
        map.put("hosname", hosname);

        return map;
    }

    @Override
    public List<Schedule> findScheduleByHoscodeAndDepcodeAndDate(String hoscode, String depcode, String date) {
        Query query = new Query(
                Criteria.where("hoscode").is(hoscode)
                        .and("depcode").is(depcode)
                        .and("workDate").is(new DateTime(date).toDate())
        );
        List<Schedule> schedules = mongoTemplate.find(query, Schedule.class);

        schedules.forEach(this::addInfoForSchedule);

        return schedules;
    }

    @Override
    public Map<String, Object> pageScheduleByHoscodeAndDepcode(Integer page, Integer limit, String hoscode, String depcode) {
        Hospital hospital = hospitalService.findByHospitalCode(hoscode);
        Department department = departmentService.findDepByDepcode(depcode);
        if(hospital == null) return null;
        BookingRule bookingRule = hospital.getBookingRule();
        List<Date> records = this.getListDate(page, limit, bookingRule).getRecords();

        Criteria criteria = Criteria.where("hoscode").is(hoscode)
                                    .and("depcode").is(depcode)
                                    .and("workDate").in(records);

        Aggregation aggre = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("wordDate").as("workDate")
                        .count().as("docCount")
                        .sum("availableNumber").as("availableNumber")
                        .sum("reservedNumber").as("reservedNumber")
        );
        AggregationResults<BookingScheduleRuleVo> aggResult = mongoTemplate.aggregate(aggre, Schedule.class, BookingScheduleRuleVo.class);
        Map<Date, BookingScheduleRuleVo> dateToBookingrule = new HashMap<>();
        List<BookingScheduleRuleVo> mappedResults = aggResult.getMappedResults();

        for(BookingScheduleRuleVo b : mappedResults){
            if(!dateToBookingrule.containsKey(b.getWorkDate())){
                dateToBookingrule.put(b.getWorkDate(), b);
            }
        }

        for(int i=0;i<records.size();i++){
            Date date = records.get(i);
            BookingScheduleRuleVo curBookingrule = dateToBookingrule.get(date);
            if(curBookingrule == null){
                curBookingrule = new BookingScheduleRuleVo();
                curBookingrule.setDocCount(0);
                curBookingrule.setAvailableNumber(-1);
            }
            curBookingrule.setWorkDate(date);
            curBookingrule.setWorkDateMd(date);
            curBookingrule.setDayOfWeek(this.getDayByDate(new DateTime(date)));

            if(i==0 && page == 1){
                String dateTimeStr = new DateTime(new Date()).toString("yyyy-MM-dd") + " " + bookingRule.getStopTime();
                DateTime stopTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeStr);
                if(stopTime.isBeforeNow()){
                    curBookingrule.setStatus(-1);
                }
            }
        }

        Map<String, Object> ret = new HashMap<>();
        Map<String, Object> baseInfo = new HashMap<>();
        ret.put("bookingSchedules", mappedResults);
        baseInfo.put("hospital", hospital);
        baseInfo.put("department", department);
        ret.put("baseInfo", baseInfo);
        return ret;
    }

    @Override
    public Schedule findById(String id) {
        Schedule schedule = mongoTemplate.findById(id, Schedule.class);
        if(schedule != null){
            this.addInfoForSchedule(schedule);
        }
        return schedule;
    }

    @Override
    public ScheduleOrderVo findByIdForOrder(String id) {
        Schedule schedule = mongoTemplate.findById(id, Schedule.class);

        if(schedule != null){
            this.addInfoForSchedule(schedule);
        } else return null;

        String hoscode = schedule.getHoscode();
        Hospital hospital = hospitalService.findByHospitalCode(hoscode);
        BookingRule bookingRule = hospital.getBookingRule();

        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        scheduleOrderVo.setHoscode(hoscode);
        scheduleOrderVo.setHosname(hospital.getHosname());
        scheduleOrderVo.setDepcode(schedule.getDepcode());
        scheduleOrderVo.setDepname(departmentService.findDepByDepcode(schedule.getDepcode()).getDepname());
        scheduleOrderVo.setHosScheduleId(schedule.getHosScheduleId());
        scheduleOrderVo.setAvailableNumber(schedule.getAvailableNumber());
        scheduleOrderVo.setTitle(schedule.getTitle());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        scheduleOrderVo.setAmount(schedule.getAmount());

        int quitDay = bookingRule.getQuitDay();
        DateTime quitTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(quitDay).toDate(),
                bookingRule.getQuitTime());
        scheduleOrderVo.setQuitTime(quitTime.toDate());

        DateTime startTime = this.getDateTime(new Date(), bookingRule.getReleaseTime());
        scheduleOrderVo.setStartTime(startTime.toDate());

        DateTime endTime = this.getDateTime(new DateTime().plusDays(bookingRule.getCycle()).toDate(),
                bookingRule.getStopTime());
        scheduleOrderVo.setEndTime(endTime.toDate());

        DateTime stopTime = this.getDateTime(new Date(), bookingRule.getStopTime());
        scheduleOrderVo.setStopTime(stopTime.toDate());

        System.out.println(schedule);
        System.out.println(scheduleOrderVo);
        return scheduleOrderVo;
    }

    @Override
    public Boolean updateScheduleByMQ(Schedule schedule) {
        schedule.setUpdateTime(new Date());
        mongoTemplate.save(schedule);
        return true;
    }

    private DateTime getDateTime(Date date, String time){
        String dateTimeStr = new DateTime(date).toString("yyyy-MM-dd") + " " + time;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeStr);
        return dateTime;
    }

    private IPage<Date> getListDate(Integer page, Integer limit, BookingRule bookingRule) {
        // 获取放号时间：yyyy-MM-dd HH:mm
        String dateTimeStr = new DateTime(new Date()).toString("yyyy-MM-dd") + " " + bookingRule.getReleaseTime();
        DateTime releaseTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeStr);

        Integer cycle = bookingRule.getCycle();

        if(releaseTime.isBeforeNow()){
            cycle += 1;
        }

        // ls 中存放所有可预约的日期Date对象
        List<Date> ls = new ArrayList<>();

        for(int i=0;i<cycle;i++){
            DateTime dateTime = new DateTime().plusDays(i);
            String s = dateTime.toString("yyyy-MM-dd");
            ls.add(new DateTime(s).toDate());
        }

        // 前端一页最多显示7个，pageList 中当前页中可预约日期Date对象
        List<Date> pageList = new ArrayList<>();

        int start = (page-1) * limit;
        int end = start + limit;

        if(end > ls.size()){
            end = ls.size();
        }

        for(int i=start;i<end;i++){
            pageList.add(ls.get(i));
        }

        IPage<Date> ret = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, 7, ls.size());
        ret.setRecords(pageList);
        return ret;
    }

    private void addInfoForSchedule(Schedule curSche) {
        Hospital hosp = hospitalService.findByHospitalCode(curSche.getHoscode());
        if(hosp != null){
            curSche.getParam().put("hosname", hosp.getHosname());
        }

        Department dep = departmentService.findDepByDepcode(curSche.getDepcode());
        if(dep != null){
            curSche.getParam().put("depcode", dep.getDepname());
        }

        curSche.getParam().put("dayOfWeek", this.getDayByDate(new DateTime(curSche.getWorkDate())));
    }

    private String getDayByDate(DateTime dateTime){
        String ret = "";

        switch (dateTime.getDayOfWeek()){
            case DateTimeConstants.MONDAY:
                ret = "星期一";
                break;
            case DateTimeConstants.TUESDAY:
                ret = "星期二";
                break;
            case DateTimeConstants.WEDNESDAY:
                ret = "星期三";
                break;
            case DateTimeConstants.THURSDAY:
                ret = "星期四";
                break;
            case DateTimeConstants.FRIDAY:
                ret = "星期五";
                break;
            case DateTimeConstants.SATURDAY:
                ret = "星期六";
                break;
            case DateTimeConstants.SUNDAY:
                ret = "星期天";
                break;
            default:
                break;
        }
        return ret;
    }
}
