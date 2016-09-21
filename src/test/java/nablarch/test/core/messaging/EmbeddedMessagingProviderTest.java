package nablarch.test.core.messaging;

import nablarch.fw.messaging.MessagingContext;
import nablarch.fw.messaging.ReceivedMessage;
import nablarch.fw.messaging.SendingMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author T.Kawasaki
 */
public class EmbeddedMessagingProviderTest {

    private MessagingContext context;

    @Before
    public void stopServer() {
        if (context != null) {
            context.close();
        }
        EmbeddedMessagingProvider.stopServer();
    }

    /**
     * サーバの起動・終了ができること。
     *
     * @throws InterruptedException 予期しない例外
     */
    @Test
    public void testStartAndStopServer() throws InterruptedException {
        // サーバ起動
        EmbeddedMessagingProvider provider = new EmbeddedMessagingProvider();
        EmbeddedMessagingProvider.waitUntilServerStarted();
        // キュー名を設定
        provider.setQueueNames(Arrays.asList("QUEUE"));
        context = provider.createContext();
        assertNotNull(context);
        // PUT
        context.send(new SendingMessage()
                             .setDestination("QUEUE")
                             .setMessageId("999"));
        // GET
        ReceivedMessage message = context.receiveSync("QUEUE", 100);
        assertThat(message.getMessageId(), is("999"));

        // サーバ終了
        context.close();
        EmbeddedMessagingProvider.stopServer();  // stop
        EmbeddedMessagingProvider.stopServer();  // stopping twice is ok.
    }

    @Test
    public void testStartServerFail() throws InterruptedException {
        new EmbeddedMessagingProvider();
        EmbeddedMessagingProvider.waitUntilServerStarted();
        new EmbeddedMessagingProvider();

    }

}
