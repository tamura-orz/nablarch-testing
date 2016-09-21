package nablarch.test.core.file;

import nablarch.core.dataformat.FixedLengthDataRecordFormatter;
import nablarch.test.core.reader.SendSyncMessageParser;

/**
 * テストデータから擬似的に作成されるメッセージを複数保持するクラス。<br/>
 * パディングの除去処理(MockMessage#removePadding(String, Object, nablarch.core.dataformat.FixedLengthDataRecordFormatter)
 * が継承元と異なる。
 * 固定長ファイルとメッセージはis-a関係ではないが、
 * 固定長ファイルの機能がほぼそのまま流用できるので継承をしている。
 * (固定長ファイルにおける1レコードが、メッセージングにおける1件のメッセージに相当する。)
 *
 * @author T.Kawasaki
 */
public class MockMessages extends FixedLengthFile {

    /**
     * コンストラクタ。<br/>
     *
     * @param path ファイルパス
     */
    public MockMessages(String path) {
        super(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataFileFragment createNewFragment() {
        return new MockMessage(this);
    }

    /**
     * テストデータから擬似的に作成されるメッセージを表すクラス。<br/>
     * <p>
     * エラーを発生させるケースでは、
     * {@value SendSyncMessageParser#ERROR_MODE_MSG_EXCEPTION}や
     * {@value SendSyncMessageParser#ERROR_MODE_TIMEOUT}が記載される。
     * これらは、本来は値が格納されるべきセルに記載されるため、
     * パディング除去処理をスキップする。
     * （そうしないと、数値型のフィールドに文字が入力されている等の誤動作が発生する）
     * </p>
     *
     * @author T.Kawasaki
     */
    private static class MockMessage extends FixedLengthFileFragment {

        /**
         * コンストラクタ。
         *
         * @param container 本インスタンスが所属するファイル
         */
        public MockMessage(FixedLengthFile container) {
            super(container);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object removePadding(String fieldName, Object value, FixedLengthDataRecordFormatter formatter) {
            if (value.equals(SendSyncMessageParser.ERROR_MODE_MSG_EXCEPTION)
             || value.equals(SendSyncMessageParser.ERROR_MODE_TIMEOUT)) {
                // 元々データが入るべきカラムに、エラー発生指示を埋め込んでいるのでパディングを回避する。
                // （元のフィールドが数値型だったり、文字列でもlengthが短い場合、エラーになる）
                return value;
            }
            return super.removePadding(fieldName, value, formatter);
        }
    }
}
