package nablarch.test.core.http;

import java.io.File;

import nablarch.test.tool.htmlcheck.HtmlChecker;

public class DummyHtmlChecker implements HtmlChecker {

    private boolean called = false;
    public void checkHtml(File html) {
        called = true;
    }

    public boolean isCalled() {
        return called;
    }
}
