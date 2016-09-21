package nablarch.test.tool.htmlcheck;

import java.io.File;
import java.nio.charset.Charset;

import nablarch.core.util.Builder;
import nablarch.test.tool.htmlcheck.parser.SimpleNode;

/**
 * HTML4相当のチェックを行う HTMLチェックツール
 * @author Tomokazu Kagawa
 */
public class Html4HtmlChecker implements HtmlChecker {

    /**
     * デフォルトエンコーディング
     */
    public static final Charset DEFALUT_ENCODING = Charset.forName("UTF-8");
    
    /**
     * 使用を許可しないタグ・属性情報
     */
    private final HtmlForbiddenNodeConf forbidden;

    /**
     * 規約上許可されていないタグ/属性が、HTML内で使われていないかをチェックするオブジェクト
     */
    private final HtmlForbiddenChecker forbiddenChecker;

    /**
     * 文法をチェックするオブジェクト
     */
    private final HtmlSyntaxChecker syntaxChecker = new HtmlSyntaxChecker();

    /**
     * コンストラクタ
     * 
     * @param confFilePath 設定ファイルパス
     */
    public Html4HtmlChecker(String confFilePath) {
        forbidden = new HtmlForbiddenNodeConf(confFilePath);
        forbiddenChecker = new HtmlForbiddenChecker(forbidden);
    }

    /**
     * Htmlファイルのチェックを行う。<br>
     * チェック内容は下記の通りである。<br>
     * <ul>
     * <li>正しい構文で記述されていること。</li>
     * <li>許可されていないタグが使用されていないこと。</li>
     * </ul>
     * @param html チェック対象HTMLファイル
     * @param encoding 指定文字コード
     * @throws InvalidHtmlException  チェック結果がNGの場合
     */
    protected void checkHtml(File html, Charset encoding) throws InvalidHtmlException {

        // 構文チェック
        SimpleNode node = doCheckSyntax(html, encoding);
        
        try {
            // 使用禁止タグ・属性を使用していないことのチェック
            forbiddenChecker.check(node);
        } catch (Exception e) {
            throw new InvalidHtmlException(Builder.concat(
                    "forbidden tag or attribute detected. file = [", html, "] : " + e.getMessage()),
                    e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * 本クラスでは、ファイルの文字コードにデフォルトの文字コード(UTF-8)を使用する。<br>
     * 
     * @see nablarch.test.tool.htmlcheck.HtmlChecker#checkHtml(java.io.File)
     */
    public void checkHtml(File html) throws InvalidHtmlException {

        checkHtml(html, DEFALUT_ENCODING);
    }

    /**
     * HTMLの構文チェックを行う。
     * 
     * @param html チェック対象HTMLファイル
     * @param encoding 指定文字コード
     * @return HTMLファイルをパースした結果である構文木
     * @throws InvalidHtmlException  チェック結果がNGの場合
     */
    private SimpleNode doCheckSyntax(File html, Charset encoding) throws InvalidHtmlException {
        
        try {
            return syntaxChecker.check(html, encoding);
        } catch (Exception e) {
            throw new InvalidHtmlException(
                    Builder.concat("syntax check failed. file = [", html, "]"),
                    e);
        }
    }
}
