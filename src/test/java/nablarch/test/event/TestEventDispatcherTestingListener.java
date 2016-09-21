package nablarch.test.event;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;

import nablarch.test.Assertion;

/**
 * {@link TestEventDispatcher}をテストするための{@link TestEventListener}
 * 実装クラス。
 */
public class TestEventDispatcherTestingListener implements TestEventListener {

    /** 発生したイベント */
    private static List<Event> events = resetEvent();


    /**
     * 発生したイベントをリセットする。
     * @return 新規リスト
     */
    private static List<Event> resetEvent() {
        ArrayList<Event> newList = new ArrayList<Event>();
        newList.add(Event.NOTHING);
        return newList;
    }

    /** 初回起動かどうか */
    private static boolean first = true;

    /**
     * イベントを追加する。
     * @param event 追加するイベント
     */
    private static void addEvent(Event event) {
        events.add(event);
    }

    /**
     * 前回発生したイベントを取得する。
     * @return 前回発生イベント
     */
    private static Event previous() {
        assertFalse(events.isEmpty());
        return events.get(events.size() - 1);
    }

    /** イベントの種類を表す列挙型 */
    private static enum Event {
        /** 何のイベントも発生していない (NULLオブジェクト）*/
        NOTHING {
            @Override
            boolean isCollectPreviousEvent(Event previous) {
                throw new IllegalStateException();            // cannot be happen.
            }
        },
        /** 全テスト実行前 */
        BEFORE_TEST_SUITE {
            @Override
            boolean isCollectPreviousEvent(Event previous) {
                return previous == NOTHING;
            }
        },
        /** テストクラス実行前 */
        BEFORE_TEST_CLASS {
            @Override
            boolean isCollectPreviousEvent(Event previous) {
                return  previous ==  NOTHING || previous == BEFORE_TEST_SUITE;
            }
        },
        /** テストメソッド実行前 */
        BEFORE_TEST_METHOD {
            @Override
            boolean isCollectPreviousEvent(Event previous) {
                return previous ==BEFORE_TEST_CLASS || previous == AFTER_TEST_METHOD;
            }
        },
        /** テストメソッド実行後 */
        AFTER_TEST_METHOD {
            @Override
            boolean isCollectPreviousEvent(Event previous) {
                return previous == BEFORE_TEST_METHOD;
            }
        },
        /** テストクラス実行後*/
        AFTER_TEST_CLASS {
            @Override
            boolean isCollectPreviousEvent(Event previous) {
                return previous == AFTER_TEST_METHOD;
            }
        };

        /**
         * 前回イベントが正しいかどうか
         * @param previous 前回イベント
         * @return 判定結果
         */
        abstract boolean isCollectPreviousEvent(Event previous);

    }

    /** {@inheritDoc} */
    public void beforeTestSuite() {
        assertTrue(first);   // beforeTestSuiteが最初であること
        first = false;

        assertStatus(Event.BEFORE_TEST_SUITE);

    }

    /** {@inheritDoc} */
    public void beforeTestClass() {
        assertFalse(first);   // beforeTestSuiteが最初であること
        assertStatus(Event.BEFORE_TEST_CLASS);
    }

    /** {@inheritDoc} */
    public void beforeTestMethod() {
        assertFalse(first);   // beforeTestSuiteが最初であること
        assertStatus(Event.BEFORE_TEST_METHOD);
    }

    /** {@inheritDoc} */
    public void afterTestMethod() {
        assertFalse(first);   // beforeTestSuiteが最初であること
        assertStatus(Event.AFTER_TEST_METHOD);
    }

    /** {@inheritDoc} */
    public void afterTestClass() {
        assertFalse(first);   // beforeTestSuiteが最初であること
        try {
            assertStatus(Event.AFTER_TEST_CLASS);
        } finally {
            events = resetEvent();
        }
    }

    /**
     * 状態を確認する。
     * @param current 現在のイベント
     */
    private static void assertStatus(Event current) {
        LOGGER.logDebug(current.name() + " events=" + events);
        try {
            if (!current.isCollectPreviousEvent(previous())) {
                Assertion.fail("current=[", current, "] events=", events);
            }
        } finally {
            addEvent(current);
        }
    }

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(TestEventDispatcherTestingListener.class);

    public static void resetFirst() {
        first = false;
    }
}
