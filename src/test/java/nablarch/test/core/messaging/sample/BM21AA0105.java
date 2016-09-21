package nablarch.test.core.messaging.sample;

import nablarch.core.dataformat.DataRecord;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.FileBatchAction;
/**
 * 何もしないAction。
 * @author Masato Inoue
 */
public class BM21AA0105 extends FileBatchAction {
    @Override
    public String getDataFileName() {
        return "test3.dat";
    }
    @Override
    public String getFormatFileName() {
        return "test";
    }
    
    public Result doData(DataRecord record, ExecutionContext ctx) {
        return new Result.Success();
    }
    
    
}

