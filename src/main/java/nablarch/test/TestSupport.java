package nablarch.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nablarch.core.ThreadContext;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.I18NUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.test.core.db.TableData;
import nablarch.test.core.reader.TestDataParser;
import nablarch.test.core.util.MapCollector;
import nablarch.test.event.TestEventDispatcher;
import static nablarch.core.util.StringUtil.isNullOrEmpty;


/**
 * テストサポートクラス。<br/>
 * 本テスティングフレームワークを利用する際のヘルパーメソッドを提供する。
 *
 * @author Tsuyoshi Kawasaki
 */
@Published
public class TestSupport extends TestEventDispatcher {

    /** リソース読み込み時のベースディレクトリ */
    private static final String DEFAULT_RESOURCE_ROOT = "test/java/";

    /** ベースディレクトリを取得するためのキー */
    private static final String RESOURCE_ROOT_KEY = "nablarch.test.resource-root";

    /** ThreadContextに設定するデフォルトのロケール表現を取得するためのキー */
    private static final String DEFAULT_LOCALE_EXPRESSION_KEY = "defaultLocale";

    /** ThreadContextに設定するロケール表現を取得するためのキー */
    private static final String LOCALE_EXPRESSION_KEY = ThreadContext.LANG_KEY;

    /** パスセパレータ */
    private static final String PATH_SEPARATOR = ";";

    /** テスト対象クラス */
    private final Class<?> testClass;

    /**
     * ThreadContextに値を設定する。<br/>
     *
     * @param sheetName 取得元シート名
     * @param id        取得元ID
     */
    public void setThreadContextValues(String sheetName, String id) {
        Map<String, String> contextValues = getMap(sheetName, id);
        setThreadContextValues(contextValues);
    }

    /**
     * ThreadContextに値を設定する。<br/>
     *
     * @param contextValues ThreadContextに設定する値
     */
    public static void setThreadContextValues(Map<String, String> contextValues) {

        for (Entry<String, String> entry : contextValues.entrySet()) {
            String key = entry.getKey();
            if (key.equals(LOCALE_EXPRESSION_KEY)) {
                continue;   // ロケールは後続処理で追加
            }
            ThreadContext.setObject(key, entry.getValue());
        }
        // ロケールの設定
        setLocaleToThreadContext(contextValues.get(LOCALE_EXPRESSION_KEY));
    }

    /**
     * スレッドコンテキストにロケールを設定する
     *
     * @param localeExpression ロケールの文字列表現
     */
    private static void setLocaleToThreadContext(String localeExpression) {

        String locale = isNullOrEmpty(localeExpression)
                ? SystemRepository.getString(DEFAULT_LOCALE_EXPRESSION_KEY)
                : localeExpression;
        if (!isNullOrEmpty(locale)) {
            ThreadContext.setLanguage(I18NUtil.createLocale(locale));
        }
    }

    /**
     * コンストラクタ
     *
     * @param testClass テスト対象クラス
     */
    @Published
    public TestSupport(Class<?> testClass) {
        this.testClass = testClass;
    }

    /**
     * HTTPリクエストパラメータ作成用のMapを取得する。<br/>
     *
     * @param sheetName シート名
     * @param id        ID
     * @return Map形式のデータ
     */
    @Published
    public Map<String, String[]> getParameterMap(String sheetName, String id) {
        return convert(getMap(sheetName, id));
    }

    /**
     * Map形式でデータを取得する。<br/>
     *
     * @param sheetName シート名
     * @param id        ID
     * @return Map形式のデータ
     */
    @Published
    public Map<String, String> getMap(String sheetName, String id) {
        List<Map<String, String>> list = getListMap(sheetName, id);
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("no data row. sheetName=[" + sheetName + "], id=[" + id + "]");
        }
        return list.get(0);
    }


    /**
     * Mapに格納されたvalueの型変換を行う。(String -> String[])<br/>
     * 変換元のStringがカンマ区切りになっている場合、カンマを区切り文字として配列に変換する。<br/>
     *
     * @param commaSeparated 変換対象オブジェクト
     * @return 変換後オブジェクト
     */
    public static Map<String, String[]> convert(Map<String, String> commaSeparated) {

        return new MapCollector<String[], String, String>() {
            @Override
            protected String[] evaluate(String key, String value) {
                if (value == null) {
                    return skip();
                }
                List<String> unescaped = unescapeAndSplit(value);
                return unescaped.toArray(new String[unescaped.size()]);
            }
        }
        .collect(commaSeparated);
    }

    /**
     * エスケープを解除する。
     *
     * @param orig エスケープされた文字列
     * @return エスケープ解除された文字列
     */
    static List<String> unescapeAndSplit(String orig) {
        String unescaped = NablarchTestUtils.unescapeYen(orig);
        List<String> values = splitWithComma(unescaped);
        return values;
    }

    /**
     * 文字列をカンマで分割する。
     *
     * @param orig 分割対象となる文字列
     * @return 分割後の文字列
     */
    static List<String> splitWithComma(String orig) {
        List<String> result = new ArrayList<String>() {
            /**
             * {@inheritDoc}
             * 追加する際に、エスケープを解除する。
             */
            @Override
            public boolean add(String o) {
                o = o.replace("\\,", ",");
                return super.add(o);
            }
        };

        // 処理対象文字列が空文字の場合
        if (orig.length() == 0) {
            result.add("");
            return result;
        }

        // カンマ毎に要素をリストに格納する。
        String rest = orig;
        int idx = 0;
        do {
            idx = indexOfComma(rest);
            if (idx == -1) {
                break;  // カンマが無い
            }
            result.add(rest.substring(0, idx));
            rest = rest.substring(idx + 1);
        } while (true);
        result.add(rest);
        return result;
    }

    /**
     * エスケープされていないカンマの位置を返却する。<br/>
     *
     * @param str 調査対象となる文字列
     * @return カンマの位置
     */
    private static int indexOfComma(String str) {
        char previous = ' ';
        for (int i = 0; i < str.length(); i++) {
            char current = str.charAt(i);
            if (current == ',' && previous != '\\') {
                return i;
            }
            previous = current;
        }
        return -1;
    }

    /**
     * List-Map形式でデータを取得する。<br/>
     *
     * @param sheetName シート名
     * @param id        ID
     * @return List-Map形式のデータ
     */
    @Published
    public List<Map<String, String>> getListMap(String sheetName, String id) {
        String resourceName = getResourceName(sheetName);
        String path = getPathOf(resourceName);
        return getTestDataParser().getListMap(path, resourceName, id);
    }


    /**
     * List-Map形式でデータを取得する。<br/>
     * {@link nablarch.fw.web.HttpRequest}のリクエストパラメータと同じ形式で取得できる。
     * エンティティのコンストラクタにそのまま渡したい場合に使用する。
     *
     * @param sheetName シート名
     * @param id        ID
     * @return List-Map形式のデータ
     * @see nablarch.fw.web.HttpRequest#getParamMap()
     */
    @Published
    public List<Map<String, String[]>> getListParamMap(String sheetName, String id) {
        List<Map<String, String>> rawValues = getListMap(sheetName, id);
        List<Map<String, String[]>> result = new ArrayList<Map<String, String[]>>(rawValues.size());
        for (Map<String, String> e : rawValues) {
            result.add(convert(e));
        }
        return result;
    }

    /**
     * 準備用のTableDataを取得する。<br/>
     *
     * @param sheetName 取得元のシート名
     * @param groupId   グループID
     * @return 準備用のTableData
     */
    public List<TableData> getSetupTableData(String sheetName, String... groupId) {
        String resourceName = getResourceName(sheetName);
        String path = getPathOf(resourceName);
        return getTestDataParser().getSetupTableData(path, resourceName, groupId);
    }


    /**
     * 期待するTableDataを取得する。
     *
     * @param sheetName 取得元のシート名
     * @param groupId   グループID
     * @return 期待するTableData
     */
    public List<TableData> getExpectedTableData(String sheetName, String... groupId) {
        String resourceName = getResourceName(sheetName);
        String path = getPathOf(resourceName);
        return getTestDataParser().getExpectedTableData(path, resourceName, groupId);
    }

    /**
     * テストデータのパスを取得する。<br/>
     * 最初にリソースが見つかったテストデータのパスを返却する。
     *
     * @param resourceName リソース名
     * @return テストデータのパス
     */
    public String getPathOf(String resourceName) {
        List<String> baseDirs = getTestDataPaths();
        String path = getPathResourceExisting(baseDirs, resourceName);
        if (path == null) {
            throw new IllegalArgumentException(
                    "couldn't find resource [" + resourceName + "] in " + baseDirs);
        }
        return path;
    }

    /**
     * リソースが存在するパスを取得する。
     *
     * @param candidatePath 候補となるパス群
     * @param resourceName リソース名
     * @return リソースが存在するパス（存在しない場合、null）
     */
    String getPathResourceExisting(List<String> candidatePath, String resourceName) {
        for (String basePath : candidatePath) {
            if (getTestDataParser().isResourceExisting(basePath, resourceName)) {
                return basePath;
            }
        }
        return null;
    }

    /**
     * テストデータのパスのリストを取得する。
     * リソースルートディレクトリに、クラスのパッケージ階層を付与したものをパスとして返却する。
     * これらのパスがリソースを探索する際の候補となる。
     * <p>
     * 例えば、リソースルートの設定が["test/online;test/batch"]で、テストクラスがfoo.bar.Buzのとき、
     * ["test/online/foo/bar", "test/batch/foo/bar"]が返却される。
     * </p>
     * @return テストデータのパスのリスト
     */
    List<String> getTestDataPaths() {
        String resourceRootSetting = getResourceRootSetting();
        String[] baseDirs = resourceRootSetting.split(PATH_SEPARATOR);
        return getTestDataPaths(baseDirs);
    }

    /**
     * テストデータのパスを取得する。<br/>
     *
     * @param baseDirs 基点となるディレクトリ
     * @return テストデータのパス
     */
    List<String> getTestDataPaths(String[] baseDirs) {
        List<String> testDataPaths = new ArrayList<String>(baseDirs.length);
        String relativePath = packageToPath(testClass);
        for (String baseDir : baseDirs) {
            testDataPaths.add(baseDir + '/' + relativePath);
        }
        return testDataPaths;
    }

    /**
     * リソースルートの設定をリポジトリより取得する。<br/>
     * ルートディレクトリが複数設定されている場合、
     * {@link #PATH_SEPARATOR}で区切られている。
     * 明示的に設定がされていない場合はデフォルト設定（{@link #DEFAULT_RESOURCE_ROOT}）を返却する。
     *
     * @return リソースルート設定
     */
    static String getResourceRootSetting() {
        String resourceRootSetting = SystemRepository.get(RESOURCE_ROOT_KEY);
        return (resourceRootSetting == null)
                ? DEFAULT_RESOURCE_ROOT
                : resourceRootSetting;
    }

    /**
     * 与えられたクラスのパッケージからパスに変換する。
     *
     * @param clazz クラス
     * @return パッケージから変換されたパス
     */
    static String packageToPath(Class<?> clazz) {
        String pkg = clazz.getPackage().getName();
        return pkg.replace('.', '/');
    }

    /**
     * ブック名を取得する。
     *
     * @return ブック名
     */
    public String getBookName() {
        return testClass.getSimpleName();
    }


    /**
     * リソース名を取得する。<br/>
     *
     * @param sheetName シート名
     * @return リソース名
     */
    public String getResourceName(String sheetName) {
        if (StringUtil.isNullOrEmpty(sheetName)) {
            throw new IllegalArgumentException("sheetName must not be null or empty.");
        }
        return getBookName() + "/" + sheetName;
    }


    /**
     * テストデータパーサを取得する。
     *
     * @return テストデータパーサ
     */
    public final TestDataParser getTestDataParser() {
        TestDataParser parser = SystemRepository.get("testDataParser");
        if (parser == null) {
            throw new IllegalStateException("can't get TestDataParser. check configuration.");
        }
        return parser;
    }

}
