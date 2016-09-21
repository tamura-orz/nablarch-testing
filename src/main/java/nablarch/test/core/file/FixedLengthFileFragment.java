package nablarch.test.core.file;

import static nablarch.core.util.Builder.concat;

import java.util.Map;

import nablarch.core.dataformat.FieldDefinition;
import nablarch.core.dataformat.FixedLengthDataRecordFormatter;
import nablarch.core.dataformat.LayoutDefinition;
import nablarch.core.dataformat.convertor.ConvertorFactorySupport;
import nablarch.core.dataformat.convertor.FixedLengthConvertorSetting;
import nablarch.core.dataformat.convertor.datatype.Bytes;
import nablarch.core.dataformat.convertor.datatype.DataType;
import nablarch.core.util.BinaryUtil;
import nablarch.test.core.util.MapCollector;

/**
 * 固定長ファイルの断片を表すクラス。<br/>
 *
 * @author T.Kawasaki
 */
public class FixedLengthFileFragment extends DataFileFragment {

    /**
     * コンストラクタ。
     *
     * @param container 本インスタンスが所属するファイル
     */
    public FixedLengthFileFragment(FixedLengthFile container) {
        super(container);
    }

    /**
     * レコード先頭からのバイト位置を表す。
     * フィールド定義を作成するごとにフィールド長の分だけ増加する。
     * @see #createFieldDefinition(int)
     */
    private int bytePosition = 1;

    /**
     * {@inheritDoc}
     * 値はパディングされる。
     */
    @Override
    protected Map<String, Object> convertForDataRecord(Map<String, String> value) {

        // フォーマッタを使わないとパディング処理ができないのでダミーのフォーマッタを生成
        final FixedLengthDataRecordFormatter dummy = new FixedLengthDataRecordFormatter();
        LayoutDefinition layout = container.createLayout();
        dummy.initializeField(layout.getDirective());
        return new MapCollector<Object, String, String>() {
            @Override
            protected Object evaluate(String key, String value) {
                Object converted = convertValue(key, value);
                return removePadding(key, converted, dummy);
            }
        }
        .collect(value);
    }


    /**
     * {@inheritDoc}
     * フィールドがバイナリの場合はバイト列に変換する。
     */
    @Override
    protected Object convertValue(String fieldName, String stringExpression) {
        if (FIRST_FIELD_NO.equals(fieldName)) {  // No列は実際のデータではないのでパディング除去対象外
            return stringExpression;
        }
        FieldDefinition fieldDefinition = getFieldDefinition(fieldName);
        DataType dataType = fieldDefinition.getDataType();
        int fieldLength = getLengthOf(fieldName);
        if (dataType == null) {
            // フォーマッタを使用しない場合、FieldDefinitionにDataTypeが設定されないので
            String typeSymbol = getTypeOf(fieldName);
            FieldDefinition field = getFieldDefinition(fieldName);
            int length = getLengthOf(fieldName);
            dataType = getDataType(typeSymbol, field, length);
        }

        if (dataType instanceof Bytes) {
            // バイト列に変換
            return toBytes(fieldDefinition, stringExpression, fieldLength);
        }
        // そのまま返却
        return stringExpression;
    }

    /** {@inheritDoc} */
    @Override
    protected FieldDefinition createFieldDefinition(int fieldIndex) {
        String name = names.get(fieldIndex);
        String typeSymbol = getTypeForTest(fieldIndex);
        int length = Integer.parseInt(lengths.get(fieldIndex));  // 固定長はフィールド長が必要
        FieldDefinition field = new FieldDefinition()
                .setPosition(bytePosition)                       // レコード先頭からのバイト位置
                .setName(name)
                .setEncoding(container.getEncodingFromDirectives());
        field.addConvertorSetting(typeSymbol, new Object[]{length});
        bytePosition += length;
        return field;
    }

    /** {@inheritDoc} */
    @Override
    protected ConvertorFactorySupport getConvertorFactorySupport() {
        return FixedLengthConvertorSetting.getInstance().getConvertorFactory();
    }
    
    /**
     * バイト列に変換する。<br/>
     *
     * @param fieldDefinition  フィールド定義
     * @param stringExpression 処理対象フィールドの文字列表現
     * @param fieldLength      フィールド長
     * @return バイト列（変換後のバイト列がフィールド長が満たない場合は0埋めされる。）
     * @throws IllegalStateException 変換後のバイトサイズがフィールド長を超過した場合
     * @see nablarch.core.util.BinaryUtil#convertToBytes(String, java.nio.charset.Charset)
     */
    private byte[] toBytes(FieldDefinition fieldDefinition, String stringExpression, int fieldLength)
            throws IllegalStateException {

        // バイト列に変換
        byte[] converted = BinaryUtil.convertToBytes(
                stringExpression, fieldDefinition.getEncoding());
        if (converted.length < fieldLength) {
            // 0埋め
            return BinaryUtil.fillZerosRight(converted, fieldLength);
        } else if (converted.length > fieldLength) {
            // フィールド長超過
            throw new IllegalStateException(concat(
                    "value size overflowed. field=[", fieldDefinition.getName(), "]",
                    ", value=[" + stringExpression, "]",
                    ", size limit=[", fieldLength, "]"));
        }
        return converted;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean isSizeValid() {
        return (names.size() != types.size() || names.size() != lengths.size());
    }
}
