package nablarch.test.core.reader;

import nablarch.test.core.util.interpreter.TestDataInterpreter;

import java.util.List;

/**
 * グループ化されたデータを解析するためのテンプレートクラス。<br/>
 *
 * @param <RET> 解析後の型
 * @author T.Kawasaki
 */
abstract class GroupDataParsingTemplate<RET> extends TestDataParsingTemplate<RET> {

    /**
     * コンストラクタ。
     *
     * @param reader       リーダ
     * @param interpreters 解釈クラス
     * @param targetType   処理対象のデータ型
     */
    GroupDataParsingTemplate(TestDataReader reader, List<TestDataInterpreter> interpreters, DataType targetType) {
        super(reader, interpreters, targetType);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * 読み込み中の行が以下の条件を満たす場合に真を返却する。
     * <ul>
     * <li>対象の{@link DataType}で開始している。</li>
     * <li>グループIDが一致している。</li>
     * </ul>
     */
    @Override
    final boolean isTargetType(List<String> line, String groupId) {
        String first = line.get(0);
        if (first == null) {
            return false;
        }
        String expected = getTargetType().getName() + groupId + '=';
        return first.startsWith(expected);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * 複数のデータを収集するため、常に偽を返却する。
     */
    @Override
    final boolean shouldStopOnNextOne() {
        return false;
    }
}
