package nablarch.test.core.reader;


import nablarch.core.dataformat.FixedLengthDataRecordFormatter.FixedLengthDirective;
import nablarch.test.core.file.FixedLengthFile;
import nablarch.test.core.util.interpreter.TestDataInterpreter;

import java.util.List;

/**
 * 固定長ファイルのテストデータを解析するクラス。
 *
 * @author T.Kawasaki
 */
public class FixedLengthFileParser extends DataFileParser<FixedLengthFile> {

    /**
     * コンストラクタ
     *
     * @param reader       リーダ
     * @param interpreters 解釈クラス
     * @param targetType   解析対象のデータタイプ
     */
    public FixedLengthFileParser(TestDataReader reader, List<TestDataInterpreter> interpreters, DataType targetType) {
        super(reader, interpreters, targetType);
    }

    /** {@inheritDoc} */
    @Override
    protected FixedLengthFile createNewFile(String filePath) {
        return new FixedLengthFile(filePath);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isDirective(String key) {
        return FixedLengthDirective.VALUES.containsKey(key);
    }
}
