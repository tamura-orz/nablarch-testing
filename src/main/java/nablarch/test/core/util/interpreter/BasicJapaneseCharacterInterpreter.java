package nablarch.test.core.util.interpreter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nablarch.test.core.util.generator.BasicJapaneseCharacterGenerator;
import nablarch.test.core.util.generator.CharacterGenerator;

/**
 * 日本語文字表現を解釈する基本実装クラス。<br/>
 * ${文字種,文字数}という表現を解釈して、文字列に変換する。
 * 例えば、{$全角英字, 10}という表現は10文字の全角英字に変換される。
 * 使用可能な文字種については、{@link BasicJapaneseCharacterGenerator}を参照。
 *
 * @author T.Kawasaki
 * @see BasicJapaneseCharacterGenerator
 */
public class BasicJapaneseCharacterInterpreter implements TestDataInterpreter {

    /** 委譲先の文字生成クラス */
    private CharacterGenerator delegate = new BasicJapaneseCharacterGenerator();

    /** パターン ${文字種,文字数} */
    private static final Pattern PTN = Pattern.compile("\\$\\{(\\W+)\\s*,\\s*([0-9]+)\\}");


    /** {@inheritDoc} */
    public String interpret(InterpretationContext context) {
        String value = context.getValue();
        Matcher m = PTN.matcher(value);
        if (m.matches()) {
            String type = m.group(1);
            int length = Integer.parseInt(m.group(2));
            return delegate.generate(type, length);
        }
        return context.invokeNext();
    }

    /**
     * 委譲先の文字生成クラスを設定する。
     * @param delegate 文字生成クラス
     */
    public void setCharacterGenerator(CharacterGenerator delegate) {
        this.delegate = delegate;
    }
}
