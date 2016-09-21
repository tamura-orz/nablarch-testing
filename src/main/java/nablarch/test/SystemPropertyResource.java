package nablarch.test;

import java.util.Properties;
import org.junit.rules.ExternalResource;

import nablarch.core.util.annotation.Published;

/**
 * システムプロパティの設定値を維持するクラス。<br />
 * 
 * <p>
 * 下記のようにテストクラスに記述することで、テストメソッドの実行後にシステムプロパティをテスト実行前の状態に復帰できる。
 *
 * <pre>
 *  &#064;Rule
 *  public final SystemPropertyResource systemPropertyResource = new SystemPropertyResource();
 * </pre>
 * </p>
 * 
 * @author Koichi Asano 
 *
 */
@Published(tag = "architect")
public class SystemPropertyResource extends ExternalResource {

    /**
     * 古いシステムプロパティの保持。
     */
    private Properties systemProps;
    
    @Override
    protected void before() {
        systemProps = (Properties) System.getProperties().clone();
    }

    @Override
    protected void after() {
        System.setProperties(systemProps);
    }
}
