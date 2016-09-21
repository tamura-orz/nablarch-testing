package nablarch.test.tool.findbugs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Assert;

import edu.umd.cs.findbugs.FindBugs;
import edu.umd.cs.findbugs.FindBugs2;
import edu.umd.cs.findbugs.TextUICommandLine;
import junit.framework.TestCase;

/**
 * {@link UsageOfUnpublishedMethodDetector}のテストクラス
 * 
 * 使用しているfindbugsが1.3.9のため、java8には対応しておらずエラーが出ます。
 * java6、もしくはjava7で実行してください。
 * また、テストを実行する際はgradleのtestタスクで実行してください。
 *
 * @author 香川朋和
 */
public class UsageOfUnpublishedMethodDetectorTest extends TestCase {

    private static final String CONFIG_FILE_PATH = "nablarch-findbugs-config";

    /**
     * 以下の動作を確認する。
     * ・コンストラクタ、メソッド単位で公開非公開を設定した場合
     *   ・公開指定されたコンストラクタ、メソッド以外を出力
     *   ・シグネチャが違えば別の要素として判断
     * ・パッケージ単位で設定された場合
     *   ・公開設定されたパッケージのクラス、サブパッケージも公開される
     * ・クラス単位で設定された場合
     *   ・公開されたクラスのメソッドはすべて公開される
     *   ・内部クラス、抽象クラス、インターフェースに対しても普通のクラスと同じ動作をする
     *   ・無名クラス内部は検知されない。
     * 
     * @throws Exception
     */
    public void testSettings() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/settings/settings");
        PublishedApisInfo.readConfigFiles();
        
        String outputFile = "src/test/java/nablarch/test/tool/findbugs/settingTest.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/settings/Caller.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/settingsTest.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * 各種構文中で使用されるAPIに対する動作を確認する。
     * 確認している構文は以下の通り
     * ・メソッドの使用
     * ・メソッドチェイン
     * ・if文
     * ・for文
     * ・while文
     * ・do-while文
     * ・switch文
     * ・三項演算子
     * ・return文
     * ・catch文、finally文
     * ・継承
     * ・インターフェース
     * @throws Exception
     */
    public void testMethodCall() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/methodcall/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/methodCallTest.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/methodcall/Caller.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/methodCallTest.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * 様々な位置でのメソッド、コンストラクタ呼び出し時の動作を確認する。
     * 以下の内部での動作を確認する。
     * ・静的初期化子
     * ・インスタンス初期化子
     * ・コンストラクタ
     * ・無名クラス
     * ・ローカルクラス
     * ・継承有ローカルクラス
     * ・内部クラス
     * @throws Exception
     */
    public void testMethodCallInNonMethod() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/methodcall/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/methodCallInNonMethodTest.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/methodcall/ClassForVariousLocation.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/methodCallInNonMethodTest.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * 無名クラスを読み込ませた際の動作を確認する。
     */
    public void testMethodCallInAnonymousClass() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/methodcall/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/methodCallInAnnonymousClass.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/methodcall/ClassForVariousLocation$1.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/methodCallInAnnonymousClass.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * ローカルクラスを読み込ませた際の動作を確認する。
     * @throws Exception
     */
    public void testMethodCallInLocalClass() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/methodcall/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/methodCallInLocalClass.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/methodcall/ClassForVariousLocation$1Local.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/methodCallInLocalClass.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * 内部クラスを読み込ませた際の動作を確認する。
     * @throws Exception
     */
    public void testMethodCallInInnerClass() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/methodcall/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/methodCallInInnerClass.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/methodcall/ClassForVariousLocation$Inner.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/methodCallInInnerClass.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * サブクラスを読み込ませた際の動作を確認する。
     * 
     * @throws Exception
     */
    public void testMethodCallInSubClass() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/methodcall/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/methodCallInSubClass.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/methodcall/inherit/method/ClassC.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/methodCallInSubClass.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * 例外クラスに対する動作の確認を行う。
     * それぞれの位置に書かれた例外クラスに対して、正しく検査できることを確認する。
     * ・静的初期化子中
     * ・インスタンス初期化子中
     * ・トップレベルクラスthrows指摘
     * ・トップレベルクラス中catch指摘
     * ・catch句内のネストしたtry-catch
     * 
     * @throws Exception
     */
    public void testExceptionsTopLevelClass() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/exception/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/exceptionTopLevelClass.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/exception/Caller.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/exceptionTopLevelClass.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * 内部クラスで使用されている例外クラスに対する動作の確認を行う。
     * 
     * @throws Exception
     */
    public void testExceptionsInnerClass() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/exception/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/exceptionInnerClass.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/exception/Caller$InnerClass.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/exceptionInnerClass.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * ローカルクラスで使用されている例外クラスに対する動作の確認を行う。
     * 
     * @throws Exception
     */
    public void testExceptionsLocalClass() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/exception/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/exceptionTest.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/exception/Caller$1LocalClass.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/exceptionLocalClass.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * 無名クラスで使用されている例外クラスに対する動作の確認を行う。
     * 
     * @throws Exception
     */
    public void testExceptionsAnonymousClass() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/exception/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/exceptionAnnonymousClass.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/exception/Caller$1.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/exceptionAnnonymousClass.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * Java1.4以前の、オペコードにjsrが使用されている
     * classファイルに対する動作の確認を行う。
     * 
     * @throws Exception
     */
    public void testExceptionsJsrMode() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/exception/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/exceptionJsrMode.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/jsrbin/nablarch/test/tool/findbugs/data/exception/Caller.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/exceptionJsrMode.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * Java1.6でコンパイルされた
     * classファイルに対する動作の確認を行う。
     * 
     * @throws Exception
     */
    public void testExceptionsJava6() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/exception/settings");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/exceptionJaba6.txt";
        doFindBugs(outputFile,
                "src/test/java/nablarch/test/tool/findbugs/data/compilejava1.6/nablarch/test/tool/findbugs/data/exception/Caller.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/exceptionJava6.txt", outputFile);
        deleteFile(outputFile);
    }

    /**
     * 例外クラス(内部クラス)がthrows句、catch句に定義されているときの動作を確認する.
     * <ul>
     * <li>
     * 使用が許可された例外クラス(内部クラス)がthrows句、catch句に定義されているとき、
     * Findbugsが使用が禁止されたクラスとして検知しないことを確認する.
     * </li>
     * <li>
     * 使用が許可されない例外クラス（内部クラス）がthrows句、catch句に定義されているとき、
     * Findbugsが使用が禁止されたクラスとして検知することを確認する.
     * </li>
     * </ul>
     */
    public void testInnerExceptionClass() throws Exception {
        System.setProperty(CONFIG_FILE_PATH, "src/test/java/nablarch/test/tool/findbugs/data/exception/settings2");
        PublishedApisInfo.readConfigFiles();

        String outputFile = "src/test/java/nablarch/test/tool/findbugs/exceptionInnerExceptionClass.txt";
        doFindBugs(outputFile, "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/nablarch/test/tool/findbugs/data/exception/CallerForExceptionInnerClass.class");
        assertFiles("src/test/java/nablarch/test/tool/findbugs/expected/exceptionInnerExceptionClass.txt", outputFile);
        deleteFile(outputFile);
    }

    private void deleteFile(String outputFile) {
        File file = new File(outputFile);
        if (!file.delete()) {
            fail();
        }
    }

    /**
     * FindBugsの実行を行う。
     * 
     * @param args コマンドライン引数
     * @throws IOException 処理実行中の例外
     * @throws InterruptedException 処理実行中の例外
     */
    private void doFindBugs(String outputFile, String classForCheck) throws IOException, InterruptedException {

        String[] args = new String[7];
        args[0] = "-include";
        args[1] = "src/test/java/nablarch-findbugs-include.xml";
        args[2] = "-output";
        args[3] = outputFile;
        args[4] = "-auxclasspath";
        args[5] = "src/test/java/nablarch/test/tool/findbugs/data/jsrbin/"
                + File.pathSeparator
                + "src/test/java/nablarch/test/tool/findbugs/data/notjsrmode/" + File.pathSeparator;
        args[6] = classForCheck;
        FindBugs2 findBugs = new FindBugs2();
        TextUICommandLine commandLine = new TextUICommandLine();
        FindBugs.processCommandLine(commandLine, args, findBugs);
        findBugs.execute();
    }

    /**
     * ファイルの内容を比較する。
     * 
     * @param expectedFilePath 期待する内容が記述されたファイルパス
     * @param actualFilePath 実際のファイルパス
     * @throws IOException ファイル入出力の際に発生した例外
     */
    private void assertFiles(String expectedFilePath, String actualFilePath) throws IOException {
        String expectedString = getStringFromFile(expectedFilePath);
        String actualString = getStringFromFile(actualFilePath);
        Assert.assertEquals(expectedString, actualString);
    }

    /**
     * filePathにて指定されるファイルの内容を文字列として返却する。
     * 
     * @param filePath 取得するファイルのパス
     * @return filePathにて指定されるファイルの内容を文字列としたもの
     * @throws IOException ファイル入出力の際のエラー
     */
    private String getStringFromFile(String filePath) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(filePath)));

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }

            return sb.toString();

        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
