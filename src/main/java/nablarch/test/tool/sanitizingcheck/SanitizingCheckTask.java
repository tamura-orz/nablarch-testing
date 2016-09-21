package nablarch.test.tool.sanitizingcheck;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import nablarch.test.tool.sanitizingcheck.out.SanitizingCheckResultOut;

/**
 * JSP検査ツール
 * 
 * @author Tomokazu Kagawa
 */
public final class SanitizingCheckTask {

    /**
     * コンストラクタ
     */
    private SanitizingCheckTask() {
    }

    /**
     * 文字コード指定（デフォルトはUTF-8）
     */
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    /**
     * 改行文字
     */
    private static String lineSeparator = System.getProperty("line.separator");

    /**
     * JSP検査を実施する。
     * <ul>
     * <li>チェック対象JSP名(ディレクトリ、ファイルの両方を選択可能)</li>
     * <li>出力用XMLファイル</li>
     * <li>設定ファイル</li>
     * <li>文字コード（省略可能、省略時はUTF-8が使用される）</li>
     * </ul>
     * 
     * @param args 引数(チェック対象JSP名、出力用XMLファイル、設定ファイル、文字コード、改行文字)
     */
    public static void main(String[] args) {

        validate(args);

        String jsp = args[0];
        String xmlPathForOut = args[1];
        String configuration = args[2];
        Charset charset = DEFAULT_CHARSET;

        if (!"".equals(args[3])) {
            charset = Charset.forName(args[3]);
        }

        if (!"".equals(args[4])) {
            lineSeparator = args[4];
        }

        List<String> additionalExts = Collections.emptyList();
        if (args.length >= 6) {
            additionalExts = splitComma(args[5]);
        }

        List<Pattern> excludePatterns = new ArrayList<Pattern>();
        if (args.length >= 7) {
            List<String> strings = splitComma(args[6]);
            for (String string : strings) {
                excludePatterns.add(Pattern.compile(string));
            }
        }

        SanitizingChecker checker = new SanitizingChecker(configuration, charset, additionalExts, excludePatterns);
        Map<String, List<String>> errorList = checker.checkSanitizing(jsp);

        SanitizingCheckResultOut.outToXml(errorList, xmlPathForOut);

    }

    /**
     * 文字列をカンマで分割し返却する。
     *
     * @param value 文字列
     * @return カンマで分割したリスト
     */
    static List<String> splitComma(String value) {
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(value.split(","));
    }

    /**
     * チェック対象JSP、出力先XMLファイル、設定ファイル、文字コード、改行が設定されていることを確認する。<br>
     * また、チェック対象JSP、設定ファイルが存在することを確認する。
     * 
     * @param args main関数に渡された引数
     */
    private static void validate(String[] args) {
        if (args.length < 5 || 7 < args.length) {
            throw new IllegalArgumentException(
                    "enter paths of jsp directory, xml and configuration. enter charset and lineseparator.");
        }

        if (!(new File(args[0])).exists()) {
            throw new IllegalArgumentException("confirm the existence of " + args[0] + ".");
        }

        if (!(new File(args[2])).isFile()) {
            throw new IllegalArgumentException(args[2] + " isn't a file.");
        }

        try {
            Charset.forName(args[3]);
        } catch (UnsupportedCharsetException e) {
            throw new IllegalArgumentException("confirm the charset " + args[3] + ".");
        }
    }
}
