package nablarch.test.core.util.interpreter;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.date.SystemTimeProvider;

/**
 * 日時に関する記法を解釈するクラス。<br/>
 * <p>
 * テストデータでシステム時刻を表す場合に使用する。
 * テストデータの値として、<pre>${systemTime}</pre>と記述されていた場合、
 * その値をシステム時刻と解釈し、{@link SystemTimeProvider}
 * 実装クラスから取得したシステム時刻に変換する。
 *
 * 例えば、以下のような記述があった場合、
 * <pre>
 *  +---------------------+
 *  |updateDate           |
 *  +---------------------+
 *  |${systemTime}        |
 *  +---------------------+
 * </pre>
 * ${systemTime}という値は、システム時刻に変換されて、
 * <pre>
 *  +---------------------+
 *  |updateDate           |
 *  +---------------------+
 *  |2011-04-11 01:23:45.0|
 *  +---------------------+
 * </pre>
 * となる。
 * </p>
 * <p>
 * 使用可能な表記法を下記に示す。
 * <pre>
 * | 表記          | 変換後の値                             | 使用例                                               |
 * | ${systemTime} | システム日時                           |                                                      |
 * | ${setUpTime}  | データベースセットアップ時の値         | データベースの準備データのタイムスタンプカラムの値   |
 * | ${updateTime} | データベース更新時の値（システム日時） | データベース更新後のタイムスタンプ期待値             |
 * </pre>
 * </p>
 *
 * @author T.Kawasaki
 */
public class DateTimeInterpreter implements TestDataInterpreter {

    /** システム時刻表現 */
    private static final String SYSTEM_TIME_EXPRESSION = "${systemTime}";

    /** レコード更新日時の期待値 */
    private static final String UPDATE_TIME_EXPRESSION = "${updateTime}";

    /** レコード登録時の時刻表現 */
    private static final String SETUP_TIME_EXPRESSION = "${setUpTime}";

    /**
     * 各種日時を格納するマップ。
     * キー：日時を表す記法（${systemTime}など）
     * 値；対応する日時
     */
    private final Map<String, String> dateTimeValues = new HashMap<String, String>();

    /**
     * {@link SystemTimeProvider}実装クラスを設定する。<br/>
     * 本クラスが返却するシステム時刻は、このメソッドで設定されたクラスが持つシステム時刻が使用される。
     *
     * @param systemTimeProvider {@link SystemTimeProvider}実装クラス
     */
    public void setSystemTimeProvider(SystemTimeProvider systemTimeProvider) {
        if (systemTimeProvider == null) {
            throw new IllegalArgumentException("systemTimeProvider must not be null.");
        }
        String systemTime = systemTimeProvider.getTimestamp().toString();
        dateTimeValues.put(SYSTEM_TIME_EXPRESSION, systemTime);
        dateTimeValues.put(UPDATE_TIME_EXPRESSION, systemTime);
    }

    /**
     * データベースセットアップ日時を設定する。
     * 本クラスが返却するデータベースセットアップ日時は、このメソッドで設定された値がそのまま使用される。
     * 引数の形式は、JDBCタイムスタンプ書式（yyyy-mm-dd hh:mm:ss.f...）に合致しなければならない。
     *
     * @param setUpDateTime データベースセットアップ日時
     */
    public void setSetUpDateTime(String setUpDateTime) {
        if (setUpDateTime == null
                || !setUpDateTime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+")) {
            throw new IllegalArgumentException(
                    "setUpDate must match yyyy-mm-dd hh:mm:ss.f... "
                    + "but was [" + setUpDateTime + "]");
        }
        dateTimeValues.put(SETUP_TIME_EXPRESSION, setUpDateTime);
    }


    /** {@inheritDoc} */
    public String interpret(InterpretationContext context) {
        String result = dateTimeValues.get(context.getValue());
        return (result != null)
                ? result
                : context.invokeNext();
    }

}
