package nablarch.test.core.messaging.sample;

import nablarch.fw.ExecutionContext;
import nablarch.fw.messaging.action.MessagingAction;
import nablarch.fw.messaging.FwHeader;
import nablarch.fw.messaging.RequestMessage;
import nablarch.fw.messaging.ResponseMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * FWヘッダをデフォルトから切り替えた時のテスト用Action
 *
 * @author T.Kawasaki
 */
public class ChangeFwHd extends MessagingAction {

    /** {@inheritDoc} */
    @Override
    protected ResponseMessage onReceive(RequestMessage request, ExecutionContext context) {
        FwHeader fwHeader = request.getFwHeader();
        // このヘッダ情報はFWヘッダが切り替わっていないと存在しない。
        String msg = (String) fwHeader.get("message");
        ResponseMessage response = request.reply();
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("message", msg);
        response.addRecord(res);
        return response;
    }
}
