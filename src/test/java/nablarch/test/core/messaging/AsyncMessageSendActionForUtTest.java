package nablarch.test.core.messaging;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nablarch.fw.ExecutionContext;
import nablarch.fw.launcher.CommandLine;
import static org.junit.Assert.fail;

/**
 * @author Ryo TANAKA
 */
public class AsyncMessageSendActionForUtTest {

    private AsyncMessageSendActionForUt target = new AsyncMessageSendActionForUt();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testErrorCase() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("this is abnormal end case");

        CommandLine commandLine = new CommandLine(
                "-diConfig", "",
                "-userId", "",
                "-requestPath", "",
                "-messageRequestId", "",
                "-errorCase", "true");
        ExecutionContext context = new ExecutionContext();
        target.initialize(commandLine, context);
        target.handle(null, context);
        fail();
    }
}
