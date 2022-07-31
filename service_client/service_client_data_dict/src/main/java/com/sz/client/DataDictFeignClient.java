package com.sz.client;


import com.sz.commonutils.Result.Result;
import com.sz.model.DataDict.DataDictInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-data-dict")
@Repository
public interface DataDictFeignClient {
    @GetMapping("/admin/cmn/dict/getInfo/{value}/{dictCode}")
    public Result<DataDictInfo> getInfo(@PathVariable("value") String value,
                                        @PathVariable("dictCode") String dictCode);

    @GetMapping("/admin/cmn/dict/getInfo/{value}")
    public Result<DataDictInfo> getInfo(@PathVariable("value") String value);
}
