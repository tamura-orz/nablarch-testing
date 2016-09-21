package nablarch.test.core.reader;

import nablarch.core.util.StringUtil;
import nablarch.test.core.file.FixedLengthFile;
import nablarch.test.core.file.MockMessages;
import nablarch.test.core.util.interpreter.TestDataInterpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * メッセージ（同期送信）を解析するクラス。
 * @author Masato Inoue
 */
public class SendSyncMessageParser extends MessageParser {

    /** テストデータ上で、タイムアウトエラーを表す文字列 */
    public static final String ERROR_MODE_TIMEOUT = "errorMode:timeout";

    /** テストデータ上で、メッセージ送受信エラーを表す文字列 */
    public static final String ERROR_MODE_MSG_EXCEPTION = "errorMode:msgException";

    /**
     * コンストラクタ
     * @param reader       リーダ
     * @param interpreters 解釈クラス
     * @param targetType   処理対象のデータ型
     */
    public SendSyncMessageParser(TestDataReader reader,
        List<TestDataInterpreter> interpreters, DataType targetType) {
        super(reader, interpreters, targetType);
    }

    /**
     * MessageParserが提供するFWヘッダの解析機能は使用しないので、
     * このメソッドが呼ばれた場合は例外をスローする。
     *
     * @return 返却されない(必ず例外が発生する)
     */
    @Override
    Map<String, String> getFwHeader() {
        throw new UnsupportedOperationException("unsupported method was called.");
    }
    /**
     * エラー処理モードのEnum
     * @author Masato Inoue
     */
    public static enum ErrorMode {

        /** タイムアウトエラーの場合を想定し、nullを返却するモード */
        TIMEOUT(ERROR_MODE_TIMEOUT),
        
        /** メッセージ送受信エラーの場合を想定し、MessagingExceptionが発生するモード */
        MSG_EXCEPTION(ERROR_MODE_MSG_EXCEPTION);

        /**
         * コンストラクタ。
         * @param value モードを有効にする場合に、セルに記載する値
         */
        private ErrorMode(String value) {
            this.value = value;
        }

        /** モードを有効にする場合に、セルに記載する値 */
        private final String value;
        
        /**
         * モードを有効にする場合に、セルに記載する値を取得する
         * @return モードを有効にする場合に、セルに記載する値
         */
        public String getValue() {
            return value;
        }

        /**
         * 指定された文字列表現のモードが、この列挙型の要素の文字列表現と一致するか否か。
         *
         * もし、列挙型の要素の中でどれか1つでも一致するものがあれば、{@code true}を返す。
         * @param mode 文字列表現のエラーモード
         * @return エラーモードを表す文字列の場合はtrue
         */
        public static boolean isErrorMode(String mode) {
            for (ErrorMode e : ErrorMode.values()) {
                if (e.getValue().equals(mode)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    /**
     * エラー処理モードが格納される列の番号（番号0はnoで、その次のカラムをエラー処理モードが設定されると認識する）。
     */
    private static final int ERROR_MODE_COLUMN_NUMBER = 1;

    /** NO列の値が格納されている列番号 */
    private static final int NO_COLUMN_NUMBER = 0;

    /**
     * {@inheritDoc}
     * <p>
     * この実装では、タイムアウトエラーおよびメッセージ送受信エラー時の対応を行う。
     * </p>
     * <p>
     * タイムアウトエラーおよびメッセージ送受信エラーの場合は、最初のフィールド以外のフィールドはパースしない。
     * </p>
     */
    protected FixedLengthFileParser createFixedLengthFileParser(
            TestDataReader reader, List<TestDataInterpreter> interpreters,
            DataType targetType) {
        
        return new FixedLengthFileParser(reader, interpreters, targetType) {
            @Override
            protected void onReadingValues(List<String> line) {
                if (StringUtil.isNullOrEmpty(line)) {
                    return;  // 空行の場合
                }

                ArrayList<String> temp = new ArrayList<String>(line);

                if (temp.size() > ERROR_MODE_COLUMN_NUMBER) {
                    String errorMode = temp.get(ERROR_MODE_COLUMN_NUMBER);
                    if (ErrorMode.isErrorMode(errorMode)) {
                        // エラー系のテストの場合
                        ArrayList<String> list = new ArrayList<String>();
                        list.add(temp.get(ERROR_MODE_COLUMN_NUMBER));
                        currentFragment.addValue(list);
                        return;
                    }
                }
                // データ行の場合
                currentFragment.addValueWithId(temp, temp.remove(NO_COLUMN_NUMBER));
            }

            /** {@inheritDoc} */
            @Override
            protected FixedLengthFile createNewFile(String filePath) {
                return new MockMessages(filePath);
            }
        };
    }
}

