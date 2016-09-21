package nablarch.test.core.util.generator;

import org.junit.Test;

import nablarch.core.validation.validator.japanese.BasicJapaneseCharacterChecker;

import static nablarch.test.TestUtil.assertMatches;
import static nablarch.test.TestUtil.assertNotMatches;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/**
 * {@link BasicJapaneseCharacterGenerator}のテスト
 *
 * @author T.Kawasaki
 */
public class BasicJapaneseCharacterGeneratorTest {

    private CharacterGenerator target = new BasicJapaneseCharacterGenerator();

    /**
     * {@link BasicJapaneseCharacterGenerator#generate(String, int)}が成功するテストケース。
     * 定義した文字種と文字集合の組み合わせが全て取得できることを確認する。
     */
    @Test
    public void testGenerate() {

        assertMatches("[A-Za-z]{10}", target.generate("半角英字", 10));
        assertMatches("[0-9]{10}", target.generate("半角数字", 10));
        assertMatches("[\\p{InBasic_Latin}&&[^0-9A-Za-z]]{10}", target.generate("半角記号", 10));
        assertMatches("[｡-ﾟ]{10}", target.generate("半角カナ", 10));
        assertMatches("[Ａ-ｚ]{10}", target.generate("全角英字", 10));
        assertMatches("[０-９]{10}", target.generate("全角数字", 10));
        assertMatches("(\\p{InHiragana}|ー){10}", target.generate("全角ひらがな", 10));
        assertMatches("(\\p{InKatakana}|ー){10}", target.generate("全角カタカナ", 10));
        assertMatches("\\p{InCJK_Unified_Ideographs}{10}", target.generate("全角漢字", 10));

        String kigou = target.generate("全角記号その他", 10);
        assertNotMatches("\\p{InCJK_Unified_Ideographs}{10}", kigou); // 漢字でない
        assertNotMatches("\\p{InBasic_Latin}{10}", kigou);            // ASCIIでない
        assertNotMatches("[｡-ﾟ]{10}", kigou);                         // 半角カナでない
        assertNotMatches("(\\p{InHiragana}|ー){10}", kigou);          // ひらがなでない
        assertNotMatches("(\\p{InKatakana}|ー){10}", kigou);          // カタカナでない
        assertNotMatches("[Ａ-ｚ]{10}", kigou);                       // 全角英字でない
        assertNotMatches("[０-９]{10}", kigou);                       // 全角数字でない
        BasicJapaneseCharacterChecker checker = new BasicJapaneseCharacterChecker();
        checker.initialize();
        assertTrue(checker.checkZenkakuCharOnly(kigou));             // 全角文字である

        String gaiji = target.generate("外字", 1);
        assertNotMatches("\\p{InCJK_Unified_Ideographs}{10}", gaiji);
        assertFalse(gaiji, checker.checkZenkakuCharOnly(gaiji));
        assertTrue(gaiji, checker.checkZenkakuCharAndGaijiCharOnly(gaiji));  // 外字である

    }

    /** 不明な文字種が指定された場合のテストケース。 */
    @Test(expected = IllegalArgumentException.class)
    public void testGenerateWithUnknownType() {
        target.generate("unknown", 1);
    }



}
