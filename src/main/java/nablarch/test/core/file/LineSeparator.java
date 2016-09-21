package nablarch.test.core.file;

/**
 * 改行コードを列挙するクラス。
 *
 * @author T.Kawasaki
 */
public enum LineSeparator {

    /** なし */
    NONE(""),
    /** CR */
    CR("\r"),
    /** LF */
    LF("\n"),
    /** CRLF */
    CRLF("\r\n");

    /** 実際の改行コードの値 */
    private final String value;

    /**
     * コンストラクタ。
     *
     * @param value 実際の値
     */
    private LineSeparator(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     *
     * @return そのインスタンスが表す改行コード
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * 改行コードの表現を評価する。<br/>
     * 本列挙クラスの要素名である場合、すなわち <code>LineSeparator#valueOf(String)</code>が値を返却する場合は
     * その値が持つ改行コードを返却する。それ以外の場合は、与えられた表現がそれ自身改行コードとみなして、
     * その引数をそのまま返却する。
     * <p>
     * (例)
     * <code>
     * LineSeparator.evaluate("CRLF"); // --> "\r\n"が返却される。
     * LineSeparator.evaluate(":");    // --> ":"が返却される。
     * </code>
     * </p>
     *
     * @param expression 改行コード表現
     * @return 評価された値
     */
    public static String evaluate(String expression) {
        try {
            LineSeparator ls = LineSeparator.valueOf(expression);
            return ls.toString();
        } catch (IllegalArgumentException e) {
            // 合致しない場合、それ自身が改行コード
            return expression;
        }
    }
}
