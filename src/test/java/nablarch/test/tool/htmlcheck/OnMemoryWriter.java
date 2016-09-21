package nablarch.test.tool.htmlcheck;

import java.util.ArrayList;
import java.util.List;

import nablarch.core.log.Logger;
import nablarch.core.log.basic.LogWriterSupport;

public class OnMemoryWriter extends LogWriterSupport {
    
    private static final List<String> LOG = new ArrayList<String>();
    
    protected void onWrite(String formattedMessage) {
        LOG.add(formattedMessage.replace(Logger.LS, ""));
    }
    
    public static void clearLog() {
        LOG.clear();
    }
    
    public static List<String> getLog() {
        return LOG;
    }
}
