package nablarch.test.core.messaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import nablarch.fw.messaging.MessagingContext;
import nablarch.fw.messaging.SendingMessage;
import nablarch.fw.messaging.logging.MessagingLogUtil;
import nablarch.test.core.log.LogVerifier;

/**
 * @author Ryo TANAKA
 */
public class RequestTestingMessagingProviderTest {

    private RequestTestingMessagingProvider provider = new RequestTestingMessagingProvider();
    private MessagingContext context = provider.createContext();

    @Test
    public void coverNopMethods() throws Exception {
        provider.setDefaultResponseTimeout(100);
        provider.setDefaultTimeToLive(100);
        provider.setDefaultResponseTimeout(100);
        provider.setMessagingExceptionFactory(null);
        provider.initialize();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMessagingContext_receiveMessage() throws Exception {
        context.receiveMessage("dummyQueue", "dummyMessageId", 0);
    }

    @Test
    public void testSend() throws Exception {
        SendingMessage message = new SendingMessage();

        Map<String, String> logInfo = new HashMap<String, String>();
        logInfo.put("logLevel", "INFO");
        logInfo.put("message1", MessagingLogUtil.getSentMessageLog(message));
        List<Map<String,String>> expectedLog = Arrays.asList(logInfo);

        LogVerifier.setExpectedLogMessages(expectedLog);
        context.send(message);
        LogVerifier.verify("Failed!");

        LogVerifier.clear();

        LogVerifier.setExpectedLogMessages(expectedLog);
        context.sendMessage(message);
        LogVerifier.verify("Failed!");

        context.close();
    }
}
