package nablarch.test.core.messaging;

import org.junit.Test;

/**
 * @author Ryo TANAKA
 */
public class MockMessagingProviderTest {

    private MockMessagingProvider target = new MockMessagingProvider();

    @Test
    public void coverNopMethod() {
        target.setDefaultResponseTimeout(0);
        target.setMessagingExceptionFactory(null);
        target.setDefaultTimeToLive(0);
    }
}
