package nablarch.test.core.reader;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author T.Kawasaki
 */
public class HeaderLineTest {

    private HeaderLine target = new HeaderLine(
            asList("[no]", "name", "address", "[comment]"));

    @Test
    public void testExcludeMarkerColumns() {
        List<String> excluded = target.excludeMarkerColumns(
                asList("1", "武田信玄", "山梨県", "甲斐の虎"));

        assertThat(excluded, is(asList("武田信玄", "山梨県")));
    }

    @Test
    public void testGetEffectiveColumnNames() {
        assertThat(target.getEffectiveColumnNames(),
                   is(new String[]{"name", "address"}));
    }

    @Test
    public void testGetMapExcludingMarkerColumns() {
        Map<String, String> actual
                = target.getMapExcludingMarkerColumns(
                asList("1", "上杉謙信", "新潟県", "越後の龍"));
        Map<String, String> expected = new HashMap<String, String>() {
            {
                put( "name", "上杉謙信");
                put("address", "新潟県");
            }
        };
        assertThat(actual, is(expected));
    }

    @Test
    public void testNullValue() {
        HeaderLine target = new HeaderLine(null);
        assertThat(target.getEffectiveColumnNames().length, is(0));
        List<String> markerColumns = target.excludeMarkerColumns(asList("[no]", "hoge", "fuga"));
        assertThat(markerColumns.isEmpty(), is(true));
    }


    @Test
    public void testExcludeMarkerColumnsShort() {
        List<String> actual
                = target.excludeMarkerColumns(asList("1", "武田信玄"));
        assertThat("もともと2要素しかないので、3要素目は空文字列が補われること",
                actual, is(asList("武田信玄", "")));

    }

    @Test
    public void testHeaderContainsNull() {
        HeaderLine target = new HeaderLine(asList("aaa", null, "[bbb]"));
        String[] names = target.getEffectiveColumnNames();
        assertThat(names, is(new String[] {
                "aaa", null
        }));

    }
}
