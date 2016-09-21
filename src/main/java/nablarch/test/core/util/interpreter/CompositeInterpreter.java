package nablarch.test.core.util.interpreter;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 複数のテストデータ表記を解釈するクラス。<br/>
 * 解釈対象の値が、複数のテストデータ表記から構成される場合、各要素を解釈した結果を返却する。
 * 例えば、${半角数字,4}-${半角数字,4}という記述は、1033-1222のような値に変換される。
 *
 * @author T.Kawasaki
 */
public class CompositeInterpreter implements TestDataInterpreter {

    /** 委譲先の{@link TestDataInterpreter} */
    private List<TestDataInterpreter> interpreters = Collections.emptyList();

    /** パターン ${...} */
    private static final Pattern PATTERN = Pattern.compile("\\$\\{[^\\}]+\\}");


    /** {@inheritDoc} */
    public String interpret(InterpretationContext context) {
        String original = context.getValue();
        Matcher m = PATTERN.matcher(original);
        StringBuffer result = new StringBuffer();
        while (m.find()) {
            String group = m.group();
            String interpreted = interpretElement(group);
            // 文字列に$が含まれているとグループ参照とみなされるのでエスケープする
            // java.util.regex.Matcher#appendReplacement(StringBuffer, String)を参照
            interpreted = Matcher.quoteReplacement(interpreted);
            m.appendReplacement(result, interpreted);
        }
        if (result.length() > 0) {
            m.appendTail(result);
            return result.toString();
        }
        return context.invokeNext();
    }

    /**
     * マッチした１要素を解釈する。
     *
     * @param element 要素
     * @return 解釈された値
     */
    private String interpretElement(String element) {
        InterpretationContext context
                = new InterpretationContext(element, interpreters);
        return context.invokeNext();
    }

    /**
     * 委譲先の{@link TestDataInterpreter}を設定する。
     *
     * @param interpreters {@link TestDataInterpreter}
     */
    public void setInterpreters(List<TestDataInterpreter> interpreters) {
        this.interpreters = interpreters;
    }
}
