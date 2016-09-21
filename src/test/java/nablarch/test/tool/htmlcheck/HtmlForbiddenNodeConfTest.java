package nablarch.test.tool.htmlcheck;

import nablarch.test.tool.htmlcheck.HtmlForbiddenNodeConf;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * HtmlForbiddenNodeConfのテストを行う。
 * 
 * @author Tomokazu Kagawa
 */
public class HtmlForbiddenNodeConfTest {

    /**
     * コンストラクタテスト<br>
     * 正常系
     * 
     * @see HtmlForbiddenNodeConf#HtmlForbiddenNodeConf(String)
     */
    @Test
    public void testCheckConfigNormal() {

        // 許可されていない属性情報が設定されている場合
        HtmlForbiddenNodeConf confForbiddenAttr = new HtmlForbiddenNodeConf(
                "src/test/java/nablarch/test/tool/htmlcheck/conf/HtmlForbiddenNodeConfConstructor.csv");
        assertTrue(confForbiddenAttr.isForbiddenAttr("test", "test2"));

        // タグ自身が許可されていないタグ情報が設定されている場合
        HtmlForbiddenNodeConf confForbiddenTag = new HtmlForbiddenNodeConf(
                "src/test/java/nablarch/test/tool/htmlcheck/conf/HtmlForbiddenNodeConfConstructor.csv");
        assertTrue(confForbiddenTag.isForbiddenTag("test3"));

        // 対となるタグが同一である属性が2つ以上の登録されている場合
        HtmlForbiddenNodeConf confForbidden2AttrFor1Tag = new HtmlForbiddenNodeConf(
                "src/test/java/nablarch/test/tool/htmlcheck/conf/HtmlForbiddenNodeConfConstructor.csv");
        assertTrue(confForbidden2AttrFor1Tag.isForbiddenAttr("test4", "test5"));
        assertTrue(confForbidden2AttrFor1Tag.isForbiddenAttr("test4", "test6"));

    }

    /**
     * コンストラクタテスト。<br>
     * 異常系
     * 
     * @see HtmlForbiddenNodeConf#HtmlForbiddenNodeConf(String)
     */
    @Test
    public void testCheckConfigBadFormat() {

        // 設定ファイルパスがnullの場合
        try {
            new HtmlForbiddenNodeConf(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("argument confFilePath must not be null.", e.getMessage());
        }

        // 設定ファイルパスの指定がファイルを指定していない場合
        try {
            new HtmlForbiddenNodeConf("src/test/java/nablarch/test/tool/htmlcheck/");
            fail();
        } catch (RuntimeException e) {
            assertEquals("can't read file [src/test/java/nablarch/test/tool/htmlcheck/]", e.getMessage());
        }

        // 対となるタグが指定されていない属性が存在する場合
        try {
            new HtmlForbiddenNodeConf("src/test/java/nablarch/test/tool/htmlcheck/conf/configBadFormatNoTag.csv");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "tag name (1st column) must not be empty.config file = [src/test/java/nablarch/test/tool/htmlcheck/conf/configBadFormatNoTag.csv] line = [1]",
                    e.getMessage());
        }

        // カンマの内行が存在する場合
        try {
            new HtmlForbiddenNodeConf("src/test/java/nablarch/test/tool/htmlcheck/conf/"
                    + "configBadFormatNoComma.csv");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "each line must have exactly two elements.config file = [src/test/java/nablarch/test/tool/htmlcheck/conf/configBadFormatNoComma.csv] line = [1]",
                    e.getMessage());
        }
    }

    /**
     * includeTagメソッドのテストを行う。
     * 
     * @see HtmlForbiddenNodeConf#contains(String)
     */
    @Test
    public void testIncludeTag() {

        HtmlForbiddenNodeConf conf = new HtmlForbiddenNodeConf("src/test/java/nablarch/test/tool/htmlcheck/conf/HtmlForbiddenNodeConfNormal.csv");

        // 設定ファイルに記載されているタグをチェック
        assertTrue(conf.contains("test"));
        assertTrue(conf.contains("test3"));

        // 属性欄に記載されている要素をチェック
        assertFalse(conf.contains("test2"));

        // 設定ファイルに記載されていないタグをチェック
        assertFalse(conf.contains("test99"));

    }

    /**
     * isForbiddenAttrメソッドのテストを行う。
     * 
     * @see HtmlForbiddenNodeConf#isForbiddenAttr(String, String)
     */
    @Test
    public void testIsForbiddenAttr() {

        HtmlForbiddenNodeConf conf = new HtmlForbiddenNodeConf("src/test/java/nablarch/test/tool/htmlcheck/conf/HtmlForbiddenNodeConfNormal.csv");

        // 設定ファイルに記載されている属性をチェック
        assertTrue(conf.isForbiddenAttr("test", "test2"));

        // 設定ファイルに記載されていない属性をチェック
        // 対となる属性が記載されていないタグ
        assertFalse(conf.isForbiddenAttr("test3", "test2"));
        assertFalse(conf.isForbiddenAttr("test3", "test98"));
        // 設定ファイルに記載されていないタグ
        assertFalse(conf.isForbiddenAttr("test99", "test2"));
        assertFalse(conf.isForbiddenAttr("test99", "test97"));

    }

    /**
     * isForbiddenTagメソッドのテストを行う。
     * 
     * @see HtmlForbiddenNodeConf#isForbiddenTag(String)
     */
    @Test
    public void testIsForbiddenTag() {
        HtmlForbiddenNodeConf conf = new HtmlForbiddenNodeConf("src/test/java/nablarch/test/tool/htmlcheck/conf/HtmlForbiddenNodeConfNormal.csv");

        // 設定ファイルに記載されているタグをチェック
        assertTrue(conf.isForbiddenTag("test3"));

        // 属性欄に記載されている要素をチェック
        assertFalse(conf.isForbiddenTag("test2"));
        // 設定ファイルに記載されていないタグをチェック
        // 対となる属性が記載されているタグ
        assertFalse(conf.isForbiddenTag("test"));
        // 設定ファイルに記載されていないタグ
        assertFalse(conf.isForbiddenTag("test99"));

    }
}
