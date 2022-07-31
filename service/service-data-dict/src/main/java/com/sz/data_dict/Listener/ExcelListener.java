package com.sz.data_dict.Listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.sz.commonutils.VO.DataDictVO;
import com.sz.data_dict.Mapper.DataDictMapper;
import com.sz.model.DataDict.DataDictInfo;

public class ExcelListener extends AnalysisEventListener<DataDictVO> {

    private DataDictMapper dataDictMapper;

    public ExcelListener(DataDictMapper dataDictMapper) {
        this.dataDictMapper = dataDictMapper;
    }

    @Override
    public void invoke(DataDictVO dataDictVO, AnalysisContext analysisContext) {
        DataDictInfo cur = new DataDictInfo();
        cur.setDictCode(dataDictVO.getDictCode());
        cur.setName(dataDictVO.getName());
        cur.setValue(dataDictVO.getValue());
        cur.setParentId(dataDictVO.getParentId());
        cur.setId(dataDictVO.getId());
        dataDictMapper.insert(cur);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
