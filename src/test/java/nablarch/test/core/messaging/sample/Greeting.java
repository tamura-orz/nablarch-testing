package nablarch.test.core.messaging.sample;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.ExecutionContext;
import nablarch.fw.messaging.action.MessagingAction;
import nablarch.fw.messaging.RequestMessage;
import nablarch.fw.messaging.ResponseMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * @author T.Kawasaki
 */
public class Greeting extends MessagingAction {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(Greeting.class);


    @Override
    protected ResponseMessage onReceive(RequestMessage request, ExecutionContext context) {

        Object sayHelloTo = request.getParam("message");
        LOGGER.logInfo("--- MESSAGE RECEIVED ---");
        ResponseMessage response = request.reply();
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("message", "Hello, " + sayHelloTo);
        response.addRecord(res);
        return response;
    }
}
