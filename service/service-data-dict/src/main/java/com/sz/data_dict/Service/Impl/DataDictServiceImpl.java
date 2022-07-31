package com.sz.data_dict.Service.Impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sz.commonutils.VO.DataDictVO;
import com.sz.data_dict.Listener.ExcelListener;
import com.sz.data_dict.Mapper.DataDictMapper;
import com.sz.data_dict.Service.DataDictService;
import com.sz.model.DataDict.DataDictInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataDictServiceImpl extends ServiceImpl<DataDictMapper, DataDictInfo> implements DataDictService {

    @Autowired
    private DataDictMapper dataDictMapper;

    @Override
    @Cacheable(value = "data-dict", keyGenerator = "keyGenerator")
    public List<DataDictInfo> findWithParentId(Long parentId) {
        QueryWrapper<DataDictInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        List<DataDictInfo> dataDictInfos = baseMapper.selectList(queryWrapper);
        for(DataDictInfo cur : dataDictInfos){
            cur.setHasChildren(hasChildrenData(cur.getId()));
        }
        return dataDictInfos;
    }

    @Override
    public Boolean hasChildrenData(Long id) {
        QueryWrapper<DataDictInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        Long num = baseMapper.selectCount(queryWrapper);
        return num > 0;
    }

    @Override
    public Boolean download(HttpServletResponse httpServletResponse) {
        httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        httpServletResponse.setCharacterEncoding("utf-8");
        String filename = "CommonInfo";
        httpServletResponse.setHeader("Content-disposition",
                "attachment;filename=" + filename + ".xlsx");


        List<DataDictInfo> ls = baseMapper.selectList(null);
        List<DataDictVO> ret = new ArrayList<>();

        for(DataDictInfo dataDictInfo : ls){
            DataDictVO cur = new DataDictVO();
            cur.setDictCode(dataDictInfo.getDictCode());
            cur.setId(dataDictInfo.getId());
            cur.setParentId(dataDictInfo.getParentId());
            cur.setName(dataDictInfo.getName());
            cur.setValue(dataDictInfo.getValue());
            ret.add(cur);
        }

        try{
            EasyExcel.write(httpServletResponse.getOutputStream(), DataDictVO.class)
                    .sheet("公共信息")
                    .doWrite(ret);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    @CacheEvict(value = "data-dict", allEntries = true)
    public Boolean upload(MultipartFile multipartFile) {
        try {
            EasyExcel.read(multipartFile.getInputStream(), DataDictVO.class, new ExcelListener(dataDictMapper))
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public DataDictInfo findWithValueAndCode(int value, String dictCode) {
        QueryWrapper<DataDictInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("value", value);

        if(dictCode != null && !"".equals(dictCode)) {
            DataDictInfo parent = dataDictMapper.selectOne(
                    new QueryWrapper<DataDictInfo>().eq("dict_code", dictCode)
            );
            if(parent != null){
                Long parentId = parent.getId();
                queryWrapper.eq("parent_id", parentId);
            } else return null;
        }

        return dataDictMapper.selectOne(queryWrapper);
    }

    @Override
    public DataDictInfo findByDictcode(String dictCode) {
        QueryWrapper<DataDictInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        return dataDictMapper.selectOne(queryWrapper);
    }
}
