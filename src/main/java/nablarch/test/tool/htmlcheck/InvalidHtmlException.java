package nablarch.test.tool.htmlcheck;

import nablarch.core.util.annotation.Published;

/**
 * HTMLファイルチェック中に何らかの例外が生じたことを示す例外.
 * 
 * @author Tomokazu Kagawa
 */
@Published(tag = "architect")
public class InvalidHtmlException extends RuntimeException {

    /**
     * @param message エラーメッセージ
     * @see Throwable#Throwable(String)
     */
    public InvalidHtmlException(String message) {
        super(message);
    }

    /**
     * @param e エラー
     * @see Throwable#Throwable(Throwable)
     */
    public InvalidHtmlException(Throwable e) {
        super(e);
    }

    /**
     * @param message メッセージ
     * @param cause エラー
     * @see Throwable#Throwable(String, Throwable)
     */
    public InvalidHtmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
