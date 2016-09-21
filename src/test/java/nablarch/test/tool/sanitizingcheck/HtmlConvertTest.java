package nablarch.test.tool.sanitizingcheck;


import org.junit.Test;

/**
 * HtmlConvertテスト
 * 
 * @author Tomokazu Kagawa
 */
public class HtmlConvertTest extends SanitizingCheckTestSupport {

    /**
     * mainメソッドテスト
     */
    @Test
    public void testMain() {

        // 引数が2の場合
        String[] args = new String[2];
        args[0] = "java/nablarch/test/tool/sanitizingcheck/testHtmlConvert.xml";
        args[1] = "java/nablarch/test/tool/sanitizingcheck/TransformToHTML.xsl";
        try {
            HtmlConvert.main(args);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("enter paths of xml, xslt and html.", e.getMessage());
        }
        
        // 引数が3の場合
        args = new String[3];
        args[0] = "src/test/java/nablarch/test/tool/sanitizingcheck/testHtmlConvert.xml";
        args[1] = "src/test/java/nablarch/test/tool/sanitizingcheck/TransformToHTML.xsl";
        args[2] = "src/test/java/nablarch/test/tool/sanitizingcheck/actual/actualHtml.html";
        HtmlConvert.main(args);
        assertFile("src/test/java/nablarch/test/tool/sanitizingcheck/expected/expectedHtml.html", args[2]);
        
        // 引数が4の場合
        args = new String[4];
        args[0] = "src/test/java/nablarch/test/tool/sanitizingcheck/testHtmlConvert.xml";
        args[1] = "src/test/java/nablarch/test/tool/sanitizingcheck/TransformToHTML.xsl";
        args[2] = "src/test/java/nablarch/test/tool/sanitizingcheck/actual/actualHtml.html";
        args[3] = "test";
        try {
            HtmlConvert.main(args);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("enter paths of xml, xslt and html.", e.getMessage());
        }
        
        // xsltファイルが存在しない場合
        args = new String[3];
        args[0] = "src/test/java/nablarch/test/tool/sanitizingcheck/testHtmlConvert.xml";
        args[1] = "noFile";
        args[2] = "src/test/java/nablarch/test/tool/sanitizingcheck/actual/actualHtml.html";
        try {
            HtmlConvert.main(args);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Xslt file doesn't exist.", e.getMessage());
        }
        
        // xmlファイルが存在しない場合
        args = new String[3];
        args[0] = "nofile";
        args[1] = "src/test/java/nablarch/test/tool/sanitizingcheck/TransformToHTML.xsl";
        args[2] = "src/test/java/nablarch/test/tool/sanitizingcheck/actual/actualHtml.html";
        try {
            HtmlConvert.main(args);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Check result file doesn't exist.", e.getMessage());
        }
    }
}
