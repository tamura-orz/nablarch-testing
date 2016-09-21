package nablarch.test.core.reader;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DataTypeTest {

    private DataType target = DataType.DEFAULT;

    /** 列挙型の名前を取得できること。 */
    @Test
    public void testGetName() {
        assertThat(target.getName(), is("DEFAULT"));
    }

    /** 列挙型の種類を取得できること。 */
    @Test
    public void testGetType() {
        assertThat(target.getType(), is(0));
    }

}