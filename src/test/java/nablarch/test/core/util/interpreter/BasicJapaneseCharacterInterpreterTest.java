package nablarch.test.core.util.interpreter;

import org.junit.Test;

import nablarch.test.core.util.generator.CharacterGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * {@link BasicJapaneseCharacterInterpreter}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class BasicJapaneseCharacterInterpreterTest {

    /** テスト対象 */
    private BasicJapaneseCharacterInterpreter target = new BasicJapaneseCharacterInterpreter();

    /**
     * 日本語の特殊表記が記載されてた場合、その記述が置き換わること。
     */
    @Test
    public void testInterpret() {
        InterpretationContext ctx
                = new InterpretationContext("${半角英字,10}", target);

        String alphabet = target.interpret(ctx);
        assertNotNull(alphabet);
        assertEquals(10, alphabet.length());
        assertTrue(alphabet, alphabet.matches("[A-z]{10}"));
    }


    /**
     * 不明な文字種が指定された場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInterpretUnknownType() {

        InterpretationContext ctx
                = new InterpretationContext("${不明,10}", target);
        target.interpret(ctx);
    }

    /**
     * 解釈できない形式の場合（${文字種,文字数}でない）は、入力値そのままの値が返却されること。
     */
    @Test
    public void testInterpretNotResponsible() {
        InterpretationContext ctx
                = new InterpretationContext("解釈できない形式", target);
        String result = target.interpret(ctx);
        assertEquals("解釈できない形式", result);
    }

    /**
     * 文字列生成クラスの設定が行われていることを確認する。
     */
    @Test
    public void testSetCharcterGenerator() {
        target.setCharacterGenerator(new CharacterGenerator() {
            public String generate(String type, int length) {
                return "hoge";
            }
        });
        InterpretationContext ctx
                = new InterpretationContext("${不明,10}", target);
        assertEquals("hoge", target.interpret(ctx));
    }

}
