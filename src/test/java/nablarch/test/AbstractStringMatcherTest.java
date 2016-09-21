package nablarch.test;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Ryo TANAKA
 */
public class AbstractStringMatcherTest {
    AbstractStringMatcher target = new AbstractStringMatcher("A String") {
        @Override
        protected boolean doMatches(String actual) {
            return getExpected().equals(actual);
        }
    };

    @Test
    public void testMatches() throws Exception {
        assertThat(target.matches(new Object()), is(false));
    }
}
