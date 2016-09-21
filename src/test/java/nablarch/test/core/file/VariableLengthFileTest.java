package nablarch.test.core.file;

import nablarch.test.RepositoryInitializer;
import nablarch.test.Trap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static nablarch.core.dataformat.VariableLengthDataRecordFormatter.VariableLengthDirective.FIELD_SEPARATOR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * {@link VariableLengthFile}のテストクラス。<br/>
 *
 * @author T.Kawasaki
 */
public class VariableLengthFileTest {

    private VariableLengthFile target = new VariableLengthFile("");


    /** コンポーネント設定ファイルに記載されたデフォルトのディレクティブが設定されていること。 */
    @Test
    public void testPrepareDefaultDirectives() {
        RepositoryInitializer.initializeDefaultRepository();
        VariableLengthFile target = new VariableLengthFile("hoge");
        Map<String, Object> directives = target.directives;

        assertEquals("Windows-31J", directives.get("text-encoding"));
        assertEquals("\"", directives.get("quoting-delimiter"));
        assertEquals("\r\n", directives.get("record-separator"));
    }

    /** 区切り文字に\tが指定された場合、タブに変換されること。 */
    @Test
    public void testConvertTab() {
        Object actual = target.convertDirectiveValue(FIELD_SEPARATOR, "\\t");
        assertThat(actual.toString(), is("\t"));
    }

    /** 指定した区切り文字が\t以外の場合、変換がされないこと。*/
    @Test
    public void testConvertDirectiveValue() {
        Object actual = target.convertDirectiveValue(FIELD_SEPARATOR, ",");
        assertThat(actual.toString(), is(","));
    }

    /** 区切り文字に１文字以上の文字が指定された場合、例外が発生すること。 */
    @Test
    public void testConvertDirectiveValueFail() {
        new Trap() {
            @Override
            protected void shouldFail() throws Exception {
                target.convertDirectiveValue(FIELD_SEPARATOR, ",,");
            }
        }.capture(IllegalArgumentException.class)
         .whichMessageContains("field-separator must be one character");
    }

    /** 区切り文字に空文字が指定された場合、例外が発生すること。 */
    @Test
    public void testConvertDirectiveValueFail2() {
        new Trap() {
            @Override
            protected void shouldFail() throws Exception {
                target.convertDirectiveValue(FIELD_SEPARATOR, "");
            }
        }.capture(IllegalArgumentException.class)
         .whichMessageContains("field-separator must be one character");
    }
    
    /** フィールド長を設定しなくても、フィールド値設定が行えること*/
    @Test
    public void testAddValue() {
        DataFile dataFile = new VariableLengthFile("hoge");
        dataFile.setDirective("text-encoding", "UTF-8");
        DataFileFragment one = dataFile.getNewFragment();
        one.setNames(Arrays.asList("hoge", "foo"));
        one.setTypes(Arrays.asList("半角英字","半角英字"));
        one.addValue(Arrays.asList("123","12345"));
        assertThat(one.toString(), is("VariableLengthFileFragment{recordType=, values=[{hoge=123, foo=12345}], types=[X, X], lengths=}"));
    }
}
