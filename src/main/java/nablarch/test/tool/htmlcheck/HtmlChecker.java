package nablarch.test.tool.htmlcheck;

import java.io.File;

import nablarch.core.util.annotation.Published;

/**
 * HTML チェックを行うクラスのインタフェース。
 *
 * @author Koichi Asano 
 *
 */
@Published(tag = "architect")
public interface HtmlChecker {

    /**
     * Htmlファイルのチェックを行う。<br>
     * チェック内容は下記の通りである。<br>
     * <ul>
     * <li>正しい構文で記述されていること。</li>
     * <li>許可されていないタグが使用されていないこと。</li>
     * </ul>
     * 
     * @param html チェック対象HTMLファイル
     * @throws InvalidHtmlException チェック結果がNGの場合
     */
    void checkHtml(File html) throws InvalidHtmlException;

}
