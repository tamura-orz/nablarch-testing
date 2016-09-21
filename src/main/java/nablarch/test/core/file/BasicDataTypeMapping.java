package nablarch.test.core.file;

import nablarch.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import static nablarch.core.util.Builder.concat;

/**
 * {@link DataTypeMapping}の基本実装クラス。<br/>
 * デフォルトの対応表を持っているが、マッピング表を外部から設定することもできる。
 *
 * @author T.Kawasaki
 */
public class BasicDataTypeMapping implements DataTypeMapping {

    /** デフォルト設定のインスタンス */
    private static final BasicDataTypeMapping DEFAULT_INSTANCE = new BasicDataTypeMapping();

    /**
     * デフォルト設定のインスタンスを取得する。
     *
     * @return デフォルト設定のインスタンス
     */
    static BasicDataTypeMapping getDefault() {
        return DEFAULT_INSTANCE;
    }

    /** デフォルトのマッピング表 */
    private static final Map<String, String> DEFAULT_TABLE = new HashMap<String, String>() {
        {
            put("半角英字", "X");
            put("半角数字", "X");
            put("半角記号", "X");
            put("半角カナ", "X");
            put("半角英数字", "X");
            put("半角英数字記号", "X");
            put("半角", "X");
            put("全角英字", "N");
            put("全角数字", "N");
            put("全角ひらがな", "N");
            put("全角カタカナ", "N");
            put("全角漢字", "N");
            put("全角", "N");
            put("全半角", "XN");
            put("数値", "Z");
            put("符号無ゾーン10進数", "Z");
            put("符号付ゾーン10進数", "SZ");
            put("符号無パック10進数", "P");
            put("符号付パック10進数", "SP");
            put("符号無数値", "X9");
            put("符号付数値", "SX9");
            put("バイナリ", "B");
        }
    };

    /** マッピング表 */
    private Map<String, String> mappingTable;

    /** {@inheritDoc} */
    public String convertToFrameworkExpression(String expressionInDesign) {
        if (expressionInDesign == null) {
            throw new IllegalArgumentException("argument must not null or empty.");
        }
        String expressionInFramework = getMappingTable().get(expressionInDesign);
        if (StringUtil.isNullOrEmpty(expressionInFramework)) {
            throw new IllegalArgumentException(concat(
                    "can't convert value [", expressionInDesign, "]. ",
                    "convert table =", getMappingTable()));
        }
        return expressionInFramework;
    }

    /**
     * マッピング表を設定する。<br/>
     * 以下の要素を持つMapを設定する。
     * <ul>
     * <li>キーに外部インタフェース設計書上のデータ型記法を指定する。</li>
     * <li>値には、そのキーに対応するフレームワーク上のデータ型シンボルを設定する。</li>
     * </ul>
     *
     * @param mappingTable マッピング表
     */
    public void setMappingTable(Map<String, String> mappingTable) {
        if (mappingTable == null) {
            throw new IllegalArgumentException("mappingTable must not null.");
        }
        this.mappingTable = mappingTable;
    }

    /**
     * マッピング表を取得する。
     *
     * @return マッピング表
     */
    private Map<String, String> getMappingTable() {
        return mappingTable == null ? DEFAULT_TABLE : mappingTable;
    }
}
