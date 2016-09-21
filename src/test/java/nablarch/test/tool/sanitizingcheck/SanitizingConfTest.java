package nablarch.test.tool.sanitizingcheck;

import java.nio.charset.Charset;

import junit.framework.TestCase;

/**
 * SanitizingConfクラスのテストクラス
 * 
 * @author Tomokazu Kagawa
 * @see SanitizingConf
 */
public class SanitizingConfTest extends TestCase {

    /**
     * コンストラクタテスト
     */
    public void testSanitizingConf() {

        // 設定ファイルパスがnullの場合
        try {
            new SanitizingConf(null, Charset.forName("UTF-8"));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("enter configuration path.", e.getMessage());
        }

        // 設定ファイルパスが空文字の場合
        try {
            new SanitizingConf("", Charset.forName("UTF-8"));
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("enter configuration path.", e.getMessage());
        }

        SanitizingConf target = new SanitizingConf(
                "src/test/java/nablarch/test/tool/sanitizingcheck/testSanitizingConf.txt", Charset
                        .forName("UTF-8"));
        
        // 設定フィルに記載されているタグが設定されていること
        assertFalse(target.isForbidden("<%--"));

        // コメント行が設定されないことの確認
        assertTrue(target.isForbidden("--test"));

        // trimがなされていることの確認
        assertFalse(target.isForbidden("space"));

    }

    /**
     * isForbiddenメソッドのテスト
     * 
     * @see SanitizingConf#isForbidden(String)
     */
    public void testIsForbidden() {

        SanitizingConf target = new SanitizingConf(
                "src/test/java/nablarch/test/tool/sanitizingcheck/testIsForbidden.txt", Charset.forName("UTF-8"));

        // JSPアクションタグの場合
        assertFalse(target.isForbidden("<jsp:attribute"));

        assertFalse(target.isForbidden("<n:confirm"));

        assertTrue(target.isForbidden("<n:write"));

        // アクションタグではない場合
        assertFalse(target.isForbidden("<%--"));

        // 設定ファイルに記載されていない場合
        assertTrue(target.isForbidden("forbidden"));
    }

}
