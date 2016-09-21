package nablarch.test.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static nablarch.test.Assertion.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * {@link ListWrapper}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class ListWrapperTest {

    /** ラップされるリスト  */
    private List<Number> wrapped = new ArrayList<Number>(Arrays.<Number>asList(0, 1L, null, 2.0D));

    /** テスト対象インスタンス */
    private ListWrapper<Number> target = ListWrapper.wrap(wrapped);

    /**
     * 格納された要素に合致するクラスのインデックスが取得されること。
     */
    @Test
    public void testIndexOf() {
        assertThat(target.indexOf(Integer.class), is(0));
        assertThat(target.indexOf(Long.class), is(1));
        assertThat(target.indexOf(Double.class), is(3));
        assertThat(target.indexOf(Short.class), is(-1));
    }

    /**
     * 格納された要素に合致するクラスのインデックスが取得されること。
     */
    @Test
    public void testIndexOfRequired() {
        // 合致する要素が存在しない場合で必須フラグが偽の時、-1が返却されること
        assertThat(target.indexOf(Short.class, false), is(-1));
        try {
            target.indexOf(Short.class, true);
        } catch (IllegalArgumentException e) {
            // 合致する要素が存在しない場合で必須フラグが真の時、例外が発生すること
            return;
        }
        fail();
    }

    /**
     * 指定された条件に合致する要素のインデックス一覧が取得されること。
     */
    @Test
    public void testIndicesOf() {
        ListWrapper<String> fooBarBuz = ListWrapper.wrap(Arrays.asList("foo", "bar", "buz"));
        // 部分的に合致する。
        assertThat(fooBarBuz.indicesOf(new ListWrapper.Condition<String>() {
            @Override
            public boolean evaluate(String element) {
                return element.startsWith("b");
            }
        }), is(Arrays.asList(1, 2)));
        // 全件合致する。
        assertThat(fooBarBuz.indicesOf(new ListWrapper.Condition<String>() {
            @Override
            public boolean evaluate(String element) {
                return element.length() == 3;
            }
        }), is(Arrays.asList(0, 1, 2)));

        // 合致する要素がない
        assertThat(fooBarBuz.indicesOf(new ListWrapper.Condition<String>() {
            @Override
            public boolean evaluate(String element) {
                return false;
            }
        }).isEmpty(), is(true));

    }

    /**
     * 格納された要素に合致するクラスのインスタンスが取得されること。
     */
    @Test
    public void testSelect() {
        assertThat(target.select(Integer.class), is(0));
        assertThat(target.select(Long.class), is(1L));
        assertThat(target.select(Double.class), is(2.0));
        assertThat(target.select(Short.class), nullValue());
    }

    /**
     * 指定したクラスに合致する要素の直後にインスタンスが挿入されること。
     */
    @Test
    public void testInsertAfter() {
        assertThat(wrapped.size(), is(4)); // 初期サイズは３
        target.insert((short) 5).after(Double.class);
        assertThat(wrapped.size(), is(5));
        assertThat((Double) wrapped.get(3), is((double) 2));
        assertThat((Short) wrapped.get(4), is((short) 5));   // Doubleの後にShortが挿入される。
    }

    /**
     * 指定したクラスに合致する要素がリスト内に存在しない場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertAfterFail() {
        target.insert((short) 5).after(Float.class);  // Floatはリスト内に存在しない。
    }

    /**
     * 指定したクラスに合致する要素の直前にインスタンスが挿入されること。
     */
    @Test
    public void testInsertBefore() {
        assertThat(wrapped.size(), is(4));   // 初期サイズは３
        target.insert((short) 5).before(Integer.class);
        target.insert((float) 5.0).before(Double.class);
        assertThat(wrapped.size(), is(6));
        assertThat((Short) wrapped.get(0), is((short) 5));  // Integerの前にShortが挿入される。
        assertThat((Float) wrapped.get(4), is((float) 5.0));  // Doubleの前にfloatが挿入される。
        assertThat((Integer) wrapped.get(1), is(0));
    }

    /**
     * 指定したクラスに合致する要素がリスト内に存在しない場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInsertBeforeFail() {
        target.insert((short) 5).before(Float.class);
    }


    /**
     * 引数がnullの場合、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWrapNull() {
        ListWrapper.wrap(null);
    }

    /**
     * 条件に合致する要素が除外されること。
     */
    @Test
    public void testExclude() {
        ListWrapper<String> target = ListWrapper.wrap(Arrays.asList("foo", "bar", "buz"));
        assertThat(target.exclude(new ListWrapper.Condition<String>() {
            public boolean evaluate(String element) {
                return element.startsWith("b");
            }
        }), is(Arrays.asList("foo")));
    }
}
