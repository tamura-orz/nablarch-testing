package nablarch.test.core.util.interpreter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link NullInterpreter}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class NullInterpreterTest {

    private TestDataInterpreter target = new NullInterpreter();

    /**
     * 小文字のnullが、<code>null</code>に解釈されること。
     */
    @Test
    public void testInterpretNullLowerCase() {
        InterpretationContext ctx = new InterpretationContext("null", target);
        assertNull(ctx.invokeNext());
    }

    /**
     * 大文字のNULLが、<code>null</code>に解釈されること。
     */
    @Test
    public void testInterpretNullUpperCase() {
        InterpretationContext ctx = new InterpretationContext("NULL", target);
        assertNull(ctx.invokeNext());
    }

    /**
     * 大文字小文字混在のNullが、<code>null</code>に解釈されること。
     */
    @Test
    public void testInterpretNullCapitalized() {
        InterpretationContext ctx = new InterpretationContext("Null", target);
        assertNull(ctx.invokeNext());
    }

    /**
     * nullでない表現が与えられた場合、入力値がそのまま返却されること。
     */
    @Test
    public void testInterpretNotNullValue() {
        InterpretationContext ctx = new InterpretationContext("hoge", target);
        assertEquals("hoge", ctx.invokeNext());
    }

}
