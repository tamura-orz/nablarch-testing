package nablarch.test.core.messaging.sample;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.dataformat.DataRecord;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.FileBatchAction;
import nablarch.fw.messaging.MessageSender;
import nablarch.fw.messaging.SyncMessage;

/**
 * @author T.Kawasaki
 */
public class BM21AA0107 extends FileBatchAction {

    @Override
    public String getDataFileName() {
        return "test4.dat";
    }

    @Override
    public String getFormatFileName() {
        return "test";
    }

    public Result doData(DataRecord record, ExecutionContext ctx) {
        Map<String, Object> header = new HashMap<String, Object>();
        header.put("testCount", 1);
        String sendingMessageFormat = "RM21AA0101";
        SyncMessage message = new SyncMessage(sendingMessageFormat).setHeaderRecord(header)
                                                                   .addDataRecord(record);
        MessageSender.sendSync(message);
        return new Result.Success();
    }
}
