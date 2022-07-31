package com.sz.data_dict.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sz.model.DataDict.DataDictInfo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DataDictService extends IService<DataDictInfo> {
    List<DataDictInfo> findWithParentId(Long parentId);

    Boolean hasChildrenData(Long id);

    Boolean download(HttpServletResponse httpServletResponse);

    Boolean upload(MultipartFile multipartFile);

    DataDictInfo findWithValueAndCode(int value, String dictCode);

    DataDictInfo findByDictcode(String dictCode);
}
