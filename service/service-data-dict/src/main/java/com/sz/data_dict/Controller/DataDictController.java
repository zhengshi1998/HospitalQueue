package com.sz.data_dict.Controller;

import com.sz.commonutils.Result.Result;
import com.sz.data_dict.Service.DataDictService;
import com.sz.model.DataDict.DataDictInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/admin/cmn/dict")
public class DataDictController {
    @Autowired
    private DataDictService dataDictService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("根据parent id查询子数据")
    @GetMapping("/findSubDataWithParentId/{parentId}")
    public Result findSubDataWithParentId(@PathVariable("parentId") Long parentId){
        List<DataDictInfo> withParentId = dataDictService.findWithParentId(parentId);
        if(withParentId != null){
            return Result.ok(withParentId);
        }else return Result.fail();
    }

    @ApiOperation("根据dict_code查询子数据")
    @GetMapping("/findSubDataWithDictcode/{dictCode}")
    public Result findSubDataWithDictcode(@PathVariable("dictCode") String dictCode){

        DataDictInfo byDictcode = dataDictService.findByDictcode(dictCode);

        if(byDictcode != null){
            return Result.ok(this.findSubDataWithParentId(byDictcode.getId()));
        }else return Result.fail();

    }

    @ApiOperation("获取信息Excel表")
    @GetMapping("/download")
    public void downloadExcel(HttpServletResponse response){
        dataDictService.download(response);
    }

    @ApiOperation("上传Excel信息表")
    @PostMapping("/upload")
    public Result uploadEcel(MultipartFile multipartFile){
        if(dataDictService.upload(multipartFile)) return Result.ok();
        else return Result.fail();
    }

    @GetMapping("/getInfo/{value}/{dictCode}")
    public Result<DataDictInfo> getInfo(@PathVariable("value") int value, @PathVariable("dictCode") String dictCode){
        return Result.ok(dataDictService.findWithValueAndCode(value, dictCode));
    }

    @GetMapping("/getInfo/{value}")
    public Result<DataDictInfo> getInfo(@PathVariable("value") int value){
        return Result.ok(dataDictService.findWithValueAndCode(value, ""));
    }
}
