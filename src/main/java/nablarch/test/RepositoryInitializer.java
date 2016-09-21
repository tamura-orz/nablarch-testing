package nablarch.test;

import java.util.Map;
import java.util.WeakHashMap;

import nablarch.core.repository.SystemRepository;
import nablarch.core.repository.di.ComponentDefinitionLoader;
import nablarch.core.repository.di.DiContainer;
import nablarch.core.repository.di.config.xml.XmlComponentDefinitionLoader;
import nablarch.core.util.annotation.Published;
import nablarch.test.event.TestEventListener;

/**
 * リポジトリの初期化クラス。
 *
 * @author T.Kawasaki
 */
public class RepositoryInitializer extends TestEventListener.Template {

    /** デフォルトのコンポーネント設定ファイル */
    private static final String DEFAULT_INIT_FILE_NAME = "unit-test.xml";

    /** デフォルトのコンテナ */
    private static DiContainer defaultContainer = null;

    /** DiContainerキャッシュ */
    private static Map<String, DiContainer> containerCache = new WeakHashMap<String, DiContainer>();

    /** リポジトリが再初期化されたか否かのフラグ */
    private static boolean isReInitialized;

    static {
        initializeDefaultRepository();
    }

    /** デフォルトのリポジトリの初期化を行う。 */
    @Published(tag = "architect")
    public static void initializeDefaultRepository() {
        if (defaultContainer == null) {
            defaultContainer = createContainer(DEFAULT_INIT_FILE_NAME);
        }
        SystemRepository.load(defaultContainer);
    }

    /** デフォルトのリポジトリを復元する。 */
    @Published(tag = "architect")
    public static void revertDefaultRepository() {
        if (isReInitialized) {
            SystemRepository.clear();
            SystemRepository.load(defaultContainer);
            isReInitialized = false;
        }
    }


    /**
     * リポジトリの初期化を行う。<br/>
     * 引数で指定されたコンポーネント設定ファイルでリポジトリを初期化する。
     *
     * @param initFileName コンポーネント設定ファイル名
     */
    @Published(tag = "architect")
    public static void reInitializeRepository(String initFileName) {

        try {
            DiContainer container = containerCache.get(initFileName);
            if (container == null) {
                container = createContainer(initFileName);
                containerCache.put(initFileName, container);
            }
            SystemRepository.clear();
            SystemRepository.load(container);
            isReInitialized = true;
        } catch (RuntimeException e) {
            try {
                revertDefaultRepository();
            } catch (RuntimeException ignored) {  // SUPPRESS CHECKSTYLE 元の例外をもみ消さないようにするため
            }
            throw new RuntimeException("failed reinitializing repository. file=[" + initFileName + "]", e);
        }

    }

    /**
     * リポジトリの初期化を行う。<br/>
     * 引数で指定されたコンポーネント設定ファイルでリポジトリを初期化する。
     * 引数で指定された順に初期化を行うので、同一のキーがあれば後のもので上書きされる。
     *
     * @param initFileNames コンポーネント設定ファイル名
     */
    @Published(tag = "architect")
    public static void reInitializeRepository(String... initFileNames) {
        reInitializeRepository(true, initFileNames);
    }

    /**
     * リポジトリの再作成を行う。<br/>
     * キャッシュは使用しないので、全てのインスタンスが再作成される。
     *
     * @param initFileNames コンポーネント設定ファイル名
     */
    @Published(tag = "architect")
    public static void recreateRepository(String... initFileNames) {
        reInitializeRepository(false, initFileNames);
    }

    /**
     * リポジトリの初期化を行う。<br/>
     * 引数で指定されたコンポーネント設定ファイルでリポジトリを初期化する。
     * 引数で指定された順に初期化を行うので、同一のキーがあれば後のもので上書きされる。
     *
     * @param useCache キャッシュを使用するかどうか
     * @param initFileNames コンポーネント設定ファイル名
     */
    private static void reInitializeRepository(boolean useCache, String... initFileNames) {
        String initFileName = null;
        try {
            isReInitialized = true;
            SystemRepository.clear();
            for (String e : initFileNames) {
                initFileName = e;
                DiContainer container = useCache
                        ? getCachedContainer(initFileName)
                        : createContainer(initFileName);
                SystemRepository.load(container);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("failed reinitializing repository. file=[" + initFileName + "]", e);
        }
    }

    /**
     * キャッシュからDIコンテナを取得する。<br/>
     * キャッシュにヒットしない場合、新規作成する。
     *
     * @param initFileName コンポーネント設定ファイル
     * @return DIコンテナ
     */
    private static DiContainer getCachedContainer(String initFileName) {
        DiContainer container = containerCache.get(initFileName);
        if (container == null) {
            container = createContainer(initFileName);
            containerCache.put(initFileName, container);
        }
        return container;
    }

    /**
     * DIコンテナを生成する
     *
     * @param initFileName コンポーネント設定ファイル名
     * @return DIコンテナ
     */
    private static DiContainer createContainer(String initFileName) {
        ComponentDefinitionLoader loader = new XmlComponentDefinitionLoader(initFileName);
        return new DiContainer(loader);
    }

    /** テストクラス終了後に元のリポジトリに戻す。 */
    @Override
    public void afterTestClass() {
        revertDefaultRepository();
    }
}
