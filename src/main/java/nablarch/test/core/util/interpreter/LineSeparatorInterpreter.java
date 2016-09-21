package nablarch.test.core.util.interpreter;

import java.util.regex.Pattern;

import nablarch.core.util.StringUtil;
import nablarch.test.core.file.LineSeparator;

/**
 * 改行コードを解釈するクラス。
 * 本クラスは、Excelのセル内に改行コードCRを記入できない問題に対処する。
 * Excelセル内の改行コードはLF(0x0A)である。
 * そのため、テストデータでCR(0x0D)やCRLF(0x0D0A）を表すことができない。
 * この問題に対処するため、任意の文字列を改行コードに置き換える処理を行う。
 * <br/>
 * 例えば、以下の様に記述した場合、
 * <pre>
 * +--------------+
 * |こんにちは\n   |
 * |さようなら     |
 * +--------------+
 * </pre>
 * 文字列"\n"がCRに置き換えられ、
 * <pre>こんにちは、\r\nさようなら</pre>
 * となる（デフォルト設定の場合）。
 *
 * @author T.Kawasaki
 */
public class LineSeparatorInterpreter implements TestDataInterpreter {

    /** デフォルトの置換対象のパターン */
    private static final String DEFAULT_PATTERN = "\\\\r";

    /** デフォルトの改行コード */
    private static final String DEFAULT_LINE_SEP = LineSeparator.CR.toString();

    /** 置換対象のパターン */
    private Pattern pattern = Pattern.compile(DEFAULT_PATTERN);

    /** 置換後の改行コード */
    private String lineSeparator = DEFAULT_LINE_SEP;

    /** {@inheritDoc} */
    @Override
    public String interpret(InterpretationContext context) {
        String orig = context.getValue();
        String result = replaceLineSeparator(orig);
        context.setValue(result);
        return context.invokeNext();
    }


    /**
     * 改行コードの置換を行う。
     * {@link #setMatchPattern(String)}で設定したパターンにマッチした箇所を
     * {@link #setMatchPattern(String)}で設定した改行コードに置換する。
     *
     * @param orig 置換前の文字列
     * @return 置換後の文字列
     */
    String replaceLineSeparator(String orig) {
        if (StringUtil.isNullOrEmpty(orig)) {
            return orig;
        }
        return pattern.matcher(orig).replaceAll(lineSeparator);
    }

    /**
     * 改行コードを設定する。
     * {@link #setMatchPattern(String)}にマッチした箇所は、
     * 本メソッドで設定された改行コードで置き換えられる。
     *
     * @param expression 改行コード表現 (NONE/CR/LF/CRLF)
     * @see nablarch.test.core.file.LineSeparator#evaluate(String)
     */
    public void setLineSeparator(String expression) {
        this.lineSeparator = LineSeparator.evaluate(expression);
    }

    /**
     * 改行コードを表すパターンを設定する。
     * 例: "\r"で改行コードを表現する場合、"\\\\r"を設定する。
     *
     * @param pattern ラインセパレータを表すパターン(Java正規表現)
     * @see java.util.regex.Pattern#compile(String)
     */
    public void setMatchPattern(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }
}
