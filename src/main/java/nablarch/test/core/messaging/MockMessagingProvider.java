package nablarch.test.core.messaging;

import nablarch.fw.messaging.MessagingContext;
import nablarch.fw.messaging.MessagingProvider;
import nablarch.fw.messaging.provider.MessagingExceptionFactory;

/**
 * モックのMessagingContextを返却するMessagingProvider。
 * <p>
 * 単体テスト時に、外接システム（キュー）とやり取りすることなく、モックを使用してテストを行うたために使用する。
 * </p>
 * @author Masato Inoue
 */
public class MockMessagingProvider implements MessagingProvider {
    
    /**
     * モックのMessagingContextを返却する。
     * @return MessagingContext モックのMessagingContext
     */
    public MessagingContext createContext() {
        return new MockMessagingContext();
    }

    /**{@inheritDoc}
     * この実装では何も行わない。
     */
    public MessagingProvider setDefaultResponseTimeout(long timeout) {
        // nop
        return this;
    }

    /**{@inheritDoc}
     * この実装では何も行わない。
     */
    public MessagingProvider setDefaultTimeToLive(long timeToLive) {
        // nop
        return this;
    }

    /**{@inheritDoc}
     * この実装では何も行わない。
     */
    public MessagingProvider setMessagingExceptionFactory(MessagingExceptionFactory messagingExceptionFactory) {
        // nop
        return this;
    }

}
