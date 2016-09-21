package nablarch.test.event;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author T.Kawasaki
 */
public class TestEventDispatcherTest {

    /**
     * 静的初期化子で例外が発生した状態で、
     * {@link TestEventDispatcher#dispatchEventOfBeforeTestClassAndBeforeSuit()}を起動した場合、
     * エラーが発生し、そのエラーに静的初期化子で発生した例外が含まれること。
     *
     * @throws NoSuchFieldException   予期しない例外
     * @throws IllegalAccessException 予期しない例外
     */
    @Test
    public void testErrorInStaticInitializer() throws NoSuchFieldException, IllegalAccessException {
        Class clazz = TestEventDispatcher.class; // クラスをロード

        // 初期化失敗時の例外を設定した状態で、BeforeClassメソッドを起動する。
        Field errorInStaticInitializer = clazz.getDeclaredField("errorInStaticInitializer");
        Throwable exception = new Exception("test.");
        try {
            errorInStaticInitializer.setAccessible(true);
            errorInStaticInitializer.set(null, exception);
            TestEventDispatcher.dispatchEventOfBeforeTestClassAndBeforeSuit();
            fail();
        } catch (Error errorInInit) {
            // リポジトリ初期化に失敗した旨のメッセージが含まれること
            assertThat(errorInInit.getMessage(), is(
                    "failed initializing repository. see nested exception message for detail. " +
                            "and check configuration files."
            ));
            // 初期化失敗時の例外がラップされていること
            Throwable cause = errorInInit.getCause();
            assertThat(cause, is(exception));
        } finally {
            // もとに戻す
            errorInStaticInitializer.set(null, null);
            errorInStaticInitializer.setAccessible(false);
        }
    }

    /**
     * TestEventListenerが登録されていない場合にも、正常に処理が終了することを確認する。
     */
    @Test
    public void testNoTestEventListener() throws Exception {
        final List<TestEventListener> original = SystemRepository.get(TestEventDispatcher.TEST_EVENT_LISTENERS_KEY);
        try {
            SystemRepository.load(new ObjectLoader() {
                @Override
                public Map<String, Object> load() {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put(TestEventDispatcher.TEST_EVENT_LISTENERS_KEY, null);
                    return map;
                }
            });
            assertThat(SystemRepository.get(TestEventDispatcher.TEST_EVENT_LISTENERS_KEY), is(nullValue()));
            TestEventDispatcher.dispatchEventOfBeforeTestClassAndBeforeSuit();
        } finally {
            SystemRepository.load(new ObjectLoader() {
                @Override
                public Map<String, Object> load() {
                    HashMap<String, Object> org = new HashMap<String, Object>();
                    org.put(TestEventDispatcher.TEST_EVENT_LISTENERS_KEY, original);
                    return org;
                }
            });
        }

    }
}
