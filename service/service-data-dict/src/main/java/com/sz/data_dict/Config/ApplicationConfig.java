package com.sz.data_dict.Config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;

@SpringBootConfiguration
@MapperScan("com.sz.data_dict.Mapper")
public class ApplicationConfig {

}
