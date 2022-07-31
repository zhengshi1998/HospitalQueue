import com.alibaba.excel.EasyExcel;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ArrayList;
import java.util.List;

public class Test {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public static void main(String[] args) {
        Test t = new Test();

        ValueOperations<Object, Object> objectObjectValueOperations = t.redisTemplate.opsForValue();
        objectObjectValueOperations.setIfAbsent("test", 123);


    }


    public static void write(){
        List<Person> ls = new ArrayList<>();
        for(int i=0;i<10;i++){
            Person person = new Person();
            person.setAge(i + 20);
            person.setName("施政" + i);
            ls.add(person);
        }
        String fileName = "F:\\Course\\Intern Project\\test.xlsx";
        EasyExcel.write(fileName, Person.class)
                .sheet("测试")
                .doWrite(ls);
    }

    public static void read(){
        String fileName = "F:\\Course\\Intern Project\\test.xlsx";
        EasyExcel.read(fileName, Person.class, new ExcelListener())
                .sheet()
                .doRead();
    }
}
