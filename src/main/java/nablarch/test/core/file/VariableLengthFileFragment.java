package nablarch.test.core.file;

import java.util.Map;

import nablarch.core.dataformat.FieldDefinition;
import nablarch.core.dataformat.convertor.ConvertorFactorySupport;
import nablarch.core.dataformat.convertor.VariableLengthConvertorSetting;
import nablarch.test.core.util.MapCollector;

/**
 * 可変長ファイルの断片を表すクラス。<br/>
 *
 * @author T.Kawasaki
 */
public class VariableLengthFileFragment extends DataFileFragment {

    /**
     * コンストラクタ。
     *
     * @param container 本インスタンスが所属するファイル
     */
    public VariableLengthFileFragment(VariableLengthFile container) {
        super(container);
    }

    /** レコード先頭からのフィールド位置 */
    private int fieldPosition = 1;

    /** {@inheritDoc} */
    @Override
    protected Map<String, Object> convertForDataRecord(Map<String, String> value) {
        return new MapCollector<Object, String, String>() {
            @Override
            protected Object evaluate(String key, String value) {
                return convertValue(key, value);
            }
        }
        .collect(value);
    }

    /** {@inheritDoc} */
    @Override
    protected Object convertValue(String fieldName, String stringExpression) {
        return stringExpression;
    }

    /** {@inheritDoc} */
    @Override
    protected FieldDefinition createFieldDefinition(int fieldIndex) {
        String name = names.get(fieldIndex);
        String typeSymbol = getTypeForTest(fieldIndex);
        FieldDefinition field = new FieldDefinition()
                .setName(name)
                .setPosition(fieldPosition)   // レコード先頭からの順番
                .setEncoding(container.getEncodingFromDirectives());
        field.addConvertorSetting(typeSymbol, new Object[]{fieldPosition});
        fieldPosition++;
        return field;
    }

    /** {@inheritDoc} */
    @Override
    protected ConvertorFactorySupport getConvertorFactorySupport() {
        return VariableLengthConvertorSetting.getInstance().getConvertorFactory();
    }
    /** {@inheritDoc} */
    @Override
    protected boolean isSizeValid() {
        return names.size() != types.size();
    }
}
