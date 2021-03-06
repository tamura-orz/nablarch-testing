package nablarch.test.core.http;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import nablarch.fw.ExecutionContext;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.HttpServer;
import nablarch.test.RepositoryInitializer;

import org.junit.After;
import org.junit.Test;

/**
 * {@link BasicHttpRequestTestTemplate}のテストクラス。
 * 
 * @author Koichi Asano 
 *
 */
public class BasicHttpRequestTestTemplateTest {

    /** システムリポジトリとHttpRequestTestSupportをデフォルトに復元する。 */
    @After
    public void initializeSystemRepository() {
        RepositoryInitializer.revertDefaultRepository();
        HttpRequestTestSupport.resetHttpServer();
    }

    /** リダイレクトのステータスコードが同一視されることのテスト。予想結果302で実際302の場合 */
    @Test
    public void testRedirect() {
        RepositoryInitializer.reInitializeRepository("nablarch/test/core/http/http-action-test-configuration.xml");
        // テスト用にサブクラス化
        BasicHttpRequestTestTemplate target = new BasicHttpRequestTestTemplate(getClass()) {

            @Override
            protected String getBaseUri() {
                return "/action/RedirectAction/";
            }
            
        };

        // 正しく終わるはず
        target.execute("redirect302302");
        target.execute("redirect303302");
        target.execute("redirect302303");
        target.execute("redirect303303");

        // 失敗するはず
        try {
            target.execute("redirect303200");
            fail("例外が発生するはず");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("expected:<[200]> but was:<[303]>"));
        }

        try {
            target.execute("redirect200303");
            fail("例外が発生するはず");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("expected:<[303]> but was:<[200]>"));
        }


        try {
            target.execute("redirect303400");
            fail("例外が発生するはず");
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("expected:<[400]> but was:<[303]>"));
        }


        try {
            target.execute("redirect400303");
            fail("例外が発生するはず");
        } catch (AssertionError e) {
            // レスポンスコードが200系は
            assertThat(e.getMessage(), containsString("expected:<[303]> but was:<[400]>"));
        }
    }

    /**
     * オーバーレイのテスト。
     */
    @Test
    public void testOverlayFirst() {
        RepositoryInitializer.reInitializeRepository("nablarch/test/core/http/testOverlay.xml");
        BasicHttpRequestTestTemplate target = new BasicHttpRequestTestTemplateForTesting(getClass());
        target.execute("overlayFirst");   // 1番目のWebAppであるappのリソースを取得できること。
        target.execute("overlaySecond");  // 2番目のWebAppであるappのリソースを取得できること。
        target.execute("overlayThird");   // 3番目のWebAppであるappのリソースを取得できること。
    }


    /** テスト用の{@link BasicHttpRequestTestTemplate} */
    private static class BasicHttpRequestTestTemplateForTesting extends BasicHttpRequestTestTemplate {

        private BasicHttpRequestTestTemplateForTesting(Class<?> testClass) {
            super(testClass);
        }

        @Override
        protected String getBaseUri() {
            return "/";
        }

        @Override
        protected HttpServer createHttpServer() {
            return new HttpServerForTesting();
        }
    }

    /** テスト用HttpServer */
    private static class HttpServerForTesting extends HttpServer {

        /** {@inheritDoc} */
        @Override
        public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
            HttpResponse res = super.handle(req, ctx);
            // ステータスコードを設定する。
            HttpRequestTestSupport.getTestSupportHandler().setStatusCode(res.getStatusCode());
            return res;
        }
    }
}
