package nablarch.test.core.reader;

import nablarch.test.core.util.interpreter.TestDataInterpreter;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@link SingleDataParsingTemplate}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class SingleDataParsingTemplateTest {

    /** 解釈クラス（本テストでは使用しないため空） */
    private static List<TestDataInterpreter> interpreters = Collections.emptyList();

    /**
     * 単一のデータを解析する場合、同じタイプ同じIDのデータが連続していても
     * 最初に発見したデータのみ読み取られること。
     *
     */
    @Test
    public void testParseSingleData() {
        // 入力値
        List<List<String>> data = asList(
                asList("LIST_MAP=hoge"),
                asList("key1", "key2"),
                asList("value1", "value2"),
                asList("LIST_MAP=hoge"),    // 同じタイプ、同じID
                asList("key3", "key4"),
                asList("value3", "value4")
        );
        ListMapParser target = new ListMapParser(new MockTestDataReader(data), interpreters);

        // 解析実行
        target.parse(getClass().getName(), "testParseSingleData", "hoge");
        List<Map<String, String>> result = target.getResult();

        // 最初の1件のみ読み取られていること
        assertThat(result.size(), is(1));
        Map<String, String> expected = new HashMap<String, String>() {
            {
                put("key1", "value1");
                put("key2", "value2");
            }
        };
        assertThat(result.get(0), is(expected));
    }
}
