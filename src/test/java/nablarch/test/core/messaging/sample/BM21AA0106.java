package nablarch.test.core.messaging.sample;

import java.util.HashMap;

import nablarch.core.dataformat.DataRecord;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.FileBatchAction;
import nablarch.fw.messaging.MessageSender;
import nablarch.fw.messaging.SyncMessage;

/**
 * 何もしないAction。
 * @author Masato Inoue
 */
public class BM21AA0106 extends FileBatchAction {
    @Override
    public String getDataFileName() {
        return "test3.dat";
    }
    @Override
    public String getFormatFileName() {
        return "test";
    }
    
    public Result doData(DataRecord record, ExecutionContext ctx) {
        MessageSender.sendSync(new SyncMessage("RM21AA0104_01").addDataRecord(new HashMap<String, Object>()));
        return new Result.Success();
    }
    
    
}

