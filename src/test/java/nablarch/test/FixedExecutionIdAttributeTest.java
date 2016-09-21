package nablarch.test;

import java.util.Arrays;
import org.junit.Test;

import nablarch.common.handler.threadcontext.ThreadContextAttribute;
import nablarch.common.handler.threadcontext.ThreadContextHandler;
import nablarch.core.ThreadContext;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ryo TANAKA
 */
public class FixedExecutionIdAttributeTest {

    @Test
    public void testFixedValue() throws Exception {
        FixedExecutionIdAttribute attribute = new FixedExecutionIdAttribute();
        attribute.setExecutionId("executionId");
        assertThat((String) attribute.getValue(null, null), is("executionId"));

        new ThreadContextHandler().setAttributes(Arrays.asList((ThreadContextAttribute) attribute)).handle(null,
                new ExecutionContext().addHandler(new Handler<Object, Object>() {
            @Override
            public Object handle(Object o, ExecutionContext context) {
                return null;
            }
        }));
        assertThat(ThreadContext.getExecutionId(), is("executionId"));
    }
}
