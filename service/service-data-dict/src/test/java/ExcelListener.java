import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

public class ExcelListener extends AnalysisEventListener<Person> {
    @Override
    public void invoke(Person person, AnalysisContext analysisContext) {
        System.out.println(person);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        System.out.println("书写完毕！");
    }
}
