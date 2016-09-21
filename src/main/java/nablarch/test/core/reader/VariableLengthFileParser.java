package nablarch.test.core.reader;


import nablarch.core.dataformat.VariableLengthDataRecordFormatter.VariableLengthDirective;
import nablarch.test.core.file.VariableLengthFile;
import nablarch.test.core.util.interpreter.TestDataInterpreter;

import java.util.List;

/**
 * 固定長ファイルのテストデータを解析するクラス。
 *
 * @author T.Kawasaki
 */
public class VariableLengthFileParser extends DataFileParser<VariableLengthFile> {

    /**
     * コンストラクタ
     *
     * @param reader       リーダ
     * @param interpreters 解釈クラス
     * @param targetType   解析対象のデータタイプ
     */
    public VariableLengthFileParser(TestDataReader reader, List<TestDataInterpreter> interpreters, DataType targetType) {
        super(reader, interpreters, targetType);
    }

    /** {@inheritDoc} */
    @Override
    protected VariableLengthFile createNewFile(String filePath) {
        return new VariableLengthFile(filePath);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isDirective(String key) {
        return VariableLengthDirective.VALUES.containsKey(key);
    }

    /** {@inheritDoc} */
    @Override
    protected void onReadingTypes(List<String> line) {
        super.onReadingTypes(line);
        // 可変長はフィールド長がないのでREADING_LENGTHS状態はスキップする。
        status = Status.READING_VALUES;
    }
}
