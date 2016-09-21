package nablarch.test.core.messaging.sample;

import nablarch.fw.ExecutionContext;
import nablarch.fw.messaging.action.MessagingAction;
import nablarch.fw.messaging.RequestMessage;
import nablarch.fw.messaging.ResponseMessage;

/**
 * @author T.Kawasaki
 */
public class Error extends MessagingAction {
    @Override
    protected ResponseMessage onReceive(RequestMessage request, ExecutionContext context) {
        ResponseMessage response = request.reply();
        return response;
    }
}
