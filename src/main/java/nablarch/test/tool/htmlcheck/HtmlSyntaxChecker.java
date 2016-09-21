package nablarch.test.tool.htmlcheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.nio.charset.Charset;

import nablarch.test.tool.htmlcheck.parser.ParseException;
import nablarch.test.tool.htmlcheck.parser.Parser;
import nablarch.test.tool.htmlcheck.parser.SimpleNode;
import nablarch.test.tool.htmlcheck.parser.TokenMgrError;
import nablarch.test.tool.htmlcheck.util.FileUtil;

/**
 * HTML構文チェックを行うユーティリティを呼び出す。
 * 
 * @author TIS Tomokazu Kagawa
 */
public class HtmlSyntaxChecker {

    /**
     * デフォルトコンストラクタ
     */
    public HtmlSyntaxChecker() {
    }

    /**
     * HTML構文チェックを行うユーティリティを呼び出す。
     * 
     * @param htmlFile チェック対象Htmlファイル
     * @param encoding 指定文字コード
     * @return Htmlファイルをパースした結果である構文木
     * @throws InvalidHtmlException  チェック結果がNGの場合
     */
    public SimpleNode check(File htmlFile, Charset encoding) throws InvalidHtmlException {

        Reader reader = null;
        try {

            reader = FileUtil.open(htmlFile, encoding);
            return new Parser(reader).document();

        } catch (TokenMgrError e) {
            throw new InvalidHtmlException(e);
        } catch (ParseException e) {
            throw new InvalidHtmlException(e);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } finally {
            FileUtil.closeQuietly(reader);
        }
    }
}
