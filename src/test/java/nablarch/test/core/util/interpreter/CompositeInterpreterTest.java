package nablarch.test.core.util.interpreter;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import nablarch.test.FixedSystemTimeProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author T.Kawasaki
 */
public class CompositeInterpreterTest {
    /** テスト対象クラス */
    private CompositeInterpreter target = new CompositeInterpreter();

    /**
     * 前準備。
     * 解釈クラスを複数設定する。
     */
    @Before
    public void setUp() {
        DateTimeInterpreter dateTimeInterpreter = new DateTimeInterpreter();
        FixedSystemTimeProvider provider = new FixedSystemTimeProvider();
        provider.setFixedDate("20110411012345");
        dateTimeInterpreter.setSystemTimeProvider(provider);

        target.setInterpreters(Arrays.<TestDataInterpreter>asList(
                new BasicJapaneseCharacterInterpreter(),
                dateTimeInterpreter));
    }

    /**
     * 単独の特殊記法が与えられた場合に、全体が置き換わること
     */
    @Test
    public void testExpression() {
        // 入力値が100文字の半角英字に置き換わる
        InterpretationContext context = new InterpretationContext("${半角英字,100}", target);
        String alphabet = context.invokeNext();
        assertNotNull(alphabet);
        assertEquals(100, alphabet.length());
        assertTrue(alphabet, alphabet.matches("[A-z]{100}"));
    }

    /**
     * 複数の特殊記法が与えられた場合に、それぞれの記法が置き換わること
     */
    @Test
    public void testCombinationOfNotations() {
        // 入力値が「半角英字10文字,半角数字10文字」の文字列に置き換わること
        InterpretationContext context = new InterpretationContext("${半角英字,10},${半角数字,10}", target);
        String alphanumeric = context.invokeNext();
        assertNotNull(alphanumeric);
        assertEquals(21, alphanumeric.length());
        String[] e = alphanumeric.split(",");
        String alpha = e[0];
        String numeric = e[1];
        assertTrue(alphanumeric, alpha.matches("[A-z]{10}"));
        assertTrue(alphanumeric, numeric.matches("[0-9]{10}"));
    }


    /**
     * 複数の特殊記法が与えられ、記法の解釈クラスが異なる場合、それぞれの記法が置き換わること
     */
    @Test
    public void testCombinationOfInterpreters() {
        // 入力値が「半角英字10文字,半角数字10文字」の文字列に置き換わること
        InterpretationContext context = new InterpretationContext("${半角英字,10},${systemTime},${半角数字,10}", target);
        String result = context.invokeNext();
        assertNotNull(result);
        assertEquals((10 + 1 + "yyyy-MM-dd hh:mm:ss.f".length() + 1 + 10), result.length());
        String[] e = result.split(",");
        String alpha = e[0];
        String systemTime = e[1];
        String numeric = e[2];
        assertTrue(result, alpha.matches("[A-z]{10}"));
        assertEquals("2011-04-11 01:23:45.0", systemTime);
        assertTrue(result, numeric.matches("[0-9]{10}"));
    }

    /**
     * 特殊記法でない文字列が変換されないこと
     */
    @Test
    public void testLiteral() {
        InterpretationContext context = new InterpretationContext("あいうえお", target);
        assertEquals("あいうえお", context.invokeNext());
    }
}
