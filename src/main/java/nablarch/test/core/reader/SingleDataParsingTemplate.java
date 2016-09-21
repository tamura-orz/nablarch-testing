package nablarch.test.core.reader;

import nablarch.test.core.util.interpreter.TestDataInterpreter;

import java.util.List;

/**
 * 単一のデータを解析するテンプレートクラス。
 *
 * @param <RET> 解析後の型
 * @author T.Kawasaki
 * @see GroupDataParsingTemplate
 */
abstract class SingleDataParsingTemplate<RET> extends TestDataParsingTemplate<RET> {

    /**
     * コンストラクタ。
     *
     * @param reader       リーダ
     * @param interpreters 解釈クラス
     * @param targetType   処理対象のデータ型
     */
    SingleDataParsingTemplate(TestDataReader reader, List<TestDataInterpreter> interpreters, DataType targetType) {
        super(reader, interpreters, targetType);
    }

    /**
     * {@inheritDoc}
     *
     * @return データ型およびIDが一致する場合に真を返却する。
     */
    @Override
    boolean isTargetType(List<String> line, String id) {
        String first = line.get(0);
        if (first == null) {
            return false;
        }
        DataType dataType = getDataType(first);
        String typeValue = getTypeValue(line);
        return getTargetType() == dataType && typeValue.equals(id);
    }

    /**
     * {@inheritDoc}
     * 本クラスは単一のデータを読み取るので、次のデータは読み取らない。
     * よって本メソッドは常に真を返却する。
     *
     * @return 常に真
     */
    @Override
    boolean shouldStopOnNextOne() {
        return true;
    }
}

