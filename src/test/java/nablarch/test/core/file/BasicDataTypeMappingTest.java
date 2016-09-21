package nablarch.test.core.file;

import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@link BasicDataTypeMapping}のテストクラス。<br/>
 *
 * @author T.Kawasaki
 */
public class BasicDataTypeMappingTest {

    /** テスト対象 */
    private BasicDataTypeMapping target = BasicDataTypeMapping.getDefault();

    /** 設計書上のデータ型がフレームワークのシンボルに変換できること。 */
    @Test
    public void testConvertToFrameworkExpression() {

        assertThat(target.convertToFrameworkExpression("半角英字"), is("X"));
        assertThat(target.convertToFrameworkExpression("半角数字"), is("X")); // 半角数字は数値ではない
        assertThat(target.convertToFrameworkExpression("半角記号"), is("X"));
        assertThat(target.convertToFrameworkExpression("半角カナ"), is("X"));
        assertThat(target.convertToFrameworkExpression("半角英数字"), is("X"));
        assertThat(target.convertToFrameworkExpression("半角英数字記号"), is("X"));
        assertThat(target.convertToFrameworkExpression("半角"), is("X"));
        assertThat(target.convertToFrameworkExpression("全角英字"), is("N"));
        assertThat(target.convertToFrameworkExpression("全角数字"), is("N"));
        assertThat(target.convertToFrameworkExpression("全角ひらがな"), is("N"));
        assertThat(target.convertToFrameworkExpression("全角カタカナ"), is("N"));
        assertThat(target.convertToFrameworkExpression("全角漢字"), is("N"));
        assertThat(target.convertToFrameworkExpression("全角"), is("N"));
        assertThat(target.convertToFrameworkExpression("全半角"), is("XN"));
        assertThat(target.convertToFrameworkExpression("数値"), is("Z"));       // ゾーン10進数は数値扱い
        assertThat(target.convertToFrameworkExpression("符号無ゾーン10進数"), is("Z"));
        assertThat(target.convertToFrameworkExpression("符号付ゾーン10進数"), is("SZ"));
        assertThat(target.convertToFrameworkExpression("符号無数値"), is("X9"));
        assertThat(target.convertToFrameworkExpression("符号付数値"), is("SX9"));
        assertThat(target.convertToFrameworkExpression("バイナリ"), is("B"));
    }

    /** 変換ができない（対応するデータ型が登録されていない場合）に例外が発生すること。 */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertToFrameworkExpressionFail() {
        target.convertToFrameworkExpression("notRegisteredDataType");
    }

    /** 引数がnullの場合に例外が発生すること。 */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertToFrameworkExpressionNull() {
        target.convertToFrameworkExpression(null);
    }

    /** マッピング表を設定した場合、そのマッピング表を用いて変換ができること。 */
    @Test
    public void testSetMappingTable() {
        BasicDataTypeMapping target = new BasicDataTypeMapping();
        target.setMappingTable(new HashMap<String, String>() {
            {
                put("newExpression", "X");
            }
        });
        assertThat(target.convertToFrameworkExpression("newExpression"), is("X"));
    }

    /** 引数がnullのとき例外が発生すること */
    @Test(expected = IllegalArgumentException.class)
    public void testSetMappingTableNull() {
        target.setMappingTable(null);
    }
}
