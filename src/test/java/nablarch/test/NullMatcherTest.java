package nablarch.test;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * {@link NullMatcher}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class NullMatcherTest {

    /**
     * nullにマッチした場合、真を返却すること。
     */
    @Test
    public void testMatches() {
        assertThat(new NullMatcher<String>().matches(null), is(true));
        assertThat(new NullMatcher<String>().matches(""), is(false));
    }
}
