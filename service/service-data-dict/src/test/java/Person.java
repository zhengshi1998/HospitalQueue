import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class Person {
    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("年龄")
    private Integer age;
}
