package nablarch.test.core.util.generator;

import nablarch.core.util.annotation.Published;

/**
 * 文字生成インタフェース。<br/>
 * テスト用に、指定された文字種の文字を生成する。
 *
 * @author T.Kawasaki
 */
@Published(tag = "architect")
public interface CharacterGenerator {

    /**
     * 文字を生成する。
     *
     * @param type   文字種
     * @param length 文字列長
     * @return 生成された文字(列)
     */
    String generate(String type, int length);
}
