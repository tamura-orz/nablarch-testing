package nablarch.test.core.reader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.core.dataformat.DataRecord;
import nablarch.test.core.file.VariableLengthFile;
import nablarch.test.core.util.interpreter.QuotationTrimmer;
import nablarch.test.core.util.interpreter.TestDataInterpreter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * {@link VariableLengthFileParser}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class VariableLengthFileParserTest {

    /** データ読み込み先ディレクトリ */
    private static final String DATA_DIR = "src/test/java/nablarch/test/core/reader/";

    /**
     * {@link TestDataInterpreter}実装クラス。
     * ""で空のセルを表すので、{@link QuotationTrimmer}を使用する。
     */
    private static List<TestDataInterpreter> interpreters
            = Arrays.<TestDataInterpreter>asList(new QuotationTrimmer());


    @Rule
    public TestName name= new TestName();

    /** テスト対象 */
    private VariableLengthFileParser target
            = new VariableLengthFileParser(new PoiXlsReader(),
                                           interpreters,
                                           DataType.EXPECTED_VARIABLE);
    /**
     * 単一項目の可変長ファイルで、空行を含むファイルを正しく読み込めること。
     */
    @Test
    public void testEmptyRowSingleItem() {

        target.parse(DATA_DIR, "VariableLengthFileParserTest/" + name.getMethodName(), "");
        List<VariableLengthFile> varFiles = target.getResult();
        assertThat(varFiles.size(), is(1));

        VariableLengthFile varFile = varFiles.get(0);
        List<DataRecord> records = varFile.toDataRecords();

        assertThat("空行を含めて2行読み込めていること",
                   records.size(), is(2));

        {
            DataRecord record = records.get(0);
            Map<String, Object> expected = new HashMap<String, Object>() {{
                put("レコード区分", "");
            }};
            assertThat("1行目は空行であること",
                       record, is(expected));
        }
        {
            DataRecord record = records.get(1);
            Map<String, Object> expected = new HashMap<String, Object>() {{
                put("レコード区分", "jkl");
            }};
            assertThat("2行目のデータが読み込めていること",
                       record, is(expected));
        }
    }

    /**
     * 複数項目の可変長ファイルで、空行を含むファイルを正しく読み込めること。
     */
    @Test
    public void testEmptyRowMultiItems() {
        target.parse(DATA_DIR, "VariableLengthFileParserTest/" + name.getMethodName(), "");
        List<VariableLengthFile> varFiles = target.getResult();
        assertThat(varFiles.size(), is(1));

        VariableLengthFile varFile = varFiles.get(0);
        List<DataRecord> records = varFile.toDataRecords();

        assertThat("空行を含めて3行読み込めていること",
                   records.size(), is(3));

        {
            DataRecord record = records.get(0);
            Map<String, Object> expected = new HashMap<String, Object>() {{
                put("漢字氏名", "山田");
                put("カナ氏名", "ヤマダ");
            }};
            assertThat("1行目のデータが読み込めていること",
                       record, is(expected));
        }

        {
            DataRecord record = records.get(1);
            Map<String, Object> expected = new HashMap<String, Object>() {{
                put("漢字氏名", "");
                put("カナ氏名", "");
            }};
            assertThat("2行目は空行であること",
                       record, is(expected));
        }
        {
            DataRecord record = records.get(2);
            Map<String, Object> expected = new HashMap<String, Object>() {{
                put("漢字氏名", "田中");
                put("カナ氏名", "タナカ");
            }};
            assertThat("3行目のデータが読み込めていること",
                       record, is(expected));
        }
    }

}