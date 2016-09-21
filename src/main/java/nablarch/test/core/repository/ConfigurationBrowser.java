package nablarch.test.core.repository;

import java.util.Map;

import nablarch.core.repository.di.ComponentDefinitionLoader;
import nablarch.core.repository.di.DiContainer;
import nablarch.core.repository.di.config.xml.XmlComponentDefinitionLoader;
import nablarch.core.util.Builder;
import nablarch.core.util.annotation.Published;
import nablarch.test.NablarchTestUtils;

/**
 * 初期値設定を読み取るクラス。<br/>
 * テスト側からテスト対象のコンポーネント設定ファイルを読む場合に使用する。
 * DIコンテンおよびコンポーネントの参照はキャッシュされる。
 *
 * @author T.Kawasaki
 */
public class ConfigurationBrowser {

    /** シングルトンインスタンス */
    private static final ConfigurationBrowser SOLO_INSTANCE
            = new ConfigurationBrowser();

    /**
     * コンポーネントを取得する。
     *
     * @param config   コンポーネント設定ファイル
     * @param key      キー
     * @param useCache キャッシュを使用するかどうか
     * @param <T>      コンポーネントの型
     * @return コンポーネント
     */
    public static <T> T get(String config, String key, boolean useCache) {
        return SOLO_INSTANCE.<T>getComponent(config, key, useCache);
    }

    /**
     * コンポーネントを取得する。<br/>
     *
     * @param config   コンポーネント設定ファイル
     * @param key      キー
     * @param useCache キャッシュを使用するかどうか
     * @param <T>      コンポーネントの型
     * @return コンポーネント
     * @throws IllegalArgumentException コンポーネントが見つからない場合
     */
    @Published(tag = "architect")
    public static <T> T require(String config, String key, boolean useCache) throws IllegalArgumentException {
        T result = ConfigurationBrowser.<T>get(config, key, useCache);
        if (result == null) {
            throw new IllegalArgumentException(Builder.concat(
                    "can't get [", key, "] from [", config, "]"));
        }
        return result;
    }

    /** DIコンテナのキャッシュ */
    private Map<String, DiContainer> containerCache = NablarchTestUtils.createLRUMap(5);

    /** コンポーネントのキャッシュ */
    private Map<Entry, Object> componentCache = NablarchTestUtils.createLRUMap(32);

    /**
     * コンポーネントを取得する。<br/>
     * キャッシュにヒットした場合は、キャッシュ上のコンポーネントが返却される。
     *
     * @param config   コンポーネント設定ファイル
     * @param key      キー
     * @param <T>      コンポーネントの型
     * @param useCache キャッシュを使用するかどうか
     * @return コンポーネント
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponent(String config, String key, boolean useCache) {
        if (!useCache) {
            return (T) createContainer(config).getComponentByName(key);
        }

        Entry p = new Entry(config, key);
        Object component = componentCache.get(p);
        if (component == null) {
            DiContainer container = getContainer(config, true);
            component = container.getComponentByName(key);
            componentCache.put(p, component);
        }
        return (T) component;

    }

    /**
     * DIコンテナを取得する。
     *
     * @param config   コンポーネント設定ファイル
     * @param useCache キャッシュを使用するかどうか
     * @return DIコンテナ
     */
    private DiContainer getContainer(String config, boolean useCache) {
        if (!useCache) {
            return createContainer(config);
        }

        DiContainer container = containerCache.get(config);
        if (container == null) {
            // テスト用のリポジトリ構築
            container = createContainer(config);
            containerCache.put(config, container);
        }
        return container;
    }

    /**
     * DIコンテナを生成する。
     *
     * @param config コンポーネント設定ファイル
     * @return DIコンテナ
     */
    private DiContainer createContainer(String config) {
        ComponentDefinitionLoader loader = new XmlComponentDefinitionLoader(config);
        return new DiContainer(loader);
    }

    /**
     * コンポーネント設定ファイルとコンポーネントのキーの組み合わせを表すクラス。<br/>
     * キャッシュのキーとして使用する。
     */
    private static final class Entry {

        /** コンポーネント設定ファイル */
        private String config;

        /** キー */
        private String key;

        /**
         * コンストラクタ
         *
         * @param config コンポーネント設定ファイル
         * @param key    キー
         */
        private Entry(String config, String key) {
            this.config = config;
            this.key = key;
        }

        /** {@inheritDoc} */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry another = (Entry) o;
            return config.equals(another.config) && key.equals(another.key);
        }

        /** {@inheritDoc} */
        @Override
        public int hashCode() {
            int result = config.hashCode();
            result = 31 * result + key.hashCode();
            return result;
        }
    }
}
