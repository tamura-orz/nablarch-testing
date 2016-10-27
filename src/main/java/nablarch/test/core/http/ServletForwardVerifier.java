package nablarch.test.core.http;

import static nablarch.test.Assertion.fail;
import static org.junit.Assert.assertEquals;

import java.util.List;

import nablarch.core.util.StringUtil;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpRequestHandler;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.ResourceLocator;
import nablarch.fw.web.handler.HttpResponseHandler;
import nablarch.test.core.util.ListWrapper;

/**
 * フォワード先を検証するハンドラ。<br/>
 * 本クラスをハンドラキューに登録する（{@link #register(java.util.List)} ）と、
 * フォワード先URIが記録される。
 * リクエスト実行後に、フォワード先を検証する
 * （{@link #verifyForward(String, String)} ）ことができる。
 *
 * @author T.Kawasaki
 */
class ServletForwardVerifier implements HttpRequestHandler {

    /** 実際のフォワード先 */
    private ResourceLocator contentPath = null;

    /** {@inheritDoc} */
    public HttpResponse handle(HttpRequest request, ExecutionContext context) {
        clear();
        // 次のハンドラを起動
        HttpResponse httpResponse = context.handleNext(request);
        contentPath = httpResponse.getContentPath();
        return httpResponse;
    }

    /** 初期化する。 */
    void clear() {
        contentPath = null;
    }

    /**
     * フォワード結果を検証する。<br/>
     * 期待値がnullの場合は検証しない。
     *
     * @param additionalMsg 追加メッセージ（検証失敗時に使用）
     * @param expectedUri   期待するフォワード先URI
     */
    void verifyForward(String additionalMsg, String expectedUri) {
        try {
            if (expectedUri == null) {
                return;
            }
            if (StringUtil.hasValue(expectedUri)) {
                assertForwardedOrRedirected(additionalMsg);
            }
            assertForwardUriEquals(additionalMsg, expectedUri);
        } finally {
            clear();
        }
    }

    /**
     * フォワード結果を検証する。<br/>
     *
     * @param expectedUri 期待するフォワード先URI
     * @see #verifyForward(String, String)
     */
    void verifyForward(String expectedUri) {
        verifyForward("", expectedUri);
    }

    /**
     * サーブレットフォワードあるいはリダイレクトされていることを表明する。
     *
     * @param msg 追加メッセージ
     */
    private void assertForwardedOrRedirected(String msg) {
        if (contentPath != null) {
            String scheme = contentPath.getScheme();
            if ("servlet".equals(scheme)
                    || "redirect".equals(scheme)
                    || "http".equals(scheme)
                    || "https".equals(scheme)) {
                return;
            }
        }
        fail(msg, " forward expected, but not. [", contentPath, "]");
    }

    /**
     * フォワード先URIが期待通りであることを表明する。
     *
     * @param msg         追加メッセージ
     * @param expectedUri 期待するフォワード先URI
     */
    private void assertForwardUriEquals(String msg, String expectedUri) {
        final String actualUri;
        if (contentPath == null) {
            actualUri = "";
        } else {
            final String scheme = contentPath.getScheme();
            if (scheme.equals("http") || scheme.equals("https")) {
                actualUri = contentPath.toString();
            } else if (scheme.equals("file")) {
                actualUri = "";
            } else {
                actualUri = contentPath.getPath();
            }
        }
        assertEquals(msg + " unexpected forward URI.", expectedUri, actualUri);
    }

    /**
     * 自インスタンスをハンドラキューに登録する。<br/>
     * 本クラスにはハンドラキュー上の登録場所に以下の制約がある。
     * <ul>
     * <li>サーブレットフォワードされる前のフォワード先URIを取得する必要があるため、
     * {@link nablarch.fw.web.handler.HttpResponseHandler}より<strong>後</strong>であること。</li>
     * <li>内部フォワードより後に実行させるため、{@link nablarch.fw.web.handler.ForwardingHandler}より
     * <strong>前</strong>に配置すること。</li>
     * HttpErrorResponseの例外処理が行われている必要があるため、{@link nablarch.fw.web.handler.HttpErrorHandler}より
     * <strong>前</strong>に配置すること。
     * </ul>
     * まとめると、以下のような順序となっていればよい。
     * <ul>
     * <li>HttpResponseHandler</li>
     * <li>ServletForwardVerifier（本クラス）</li>
     * <li>ForwardingHandler</li>
     * <li>HttpErrorHandler</li>
     * </ul>
     * <p/>
     * このメソッドでは、{@link HttpResponseHandler}の直後に自インスタンスを登録する。
     * これにより、上記の制約事項が満たされる。
     *
     * @param handlerQueue 自インスタンスの登録対象となるハンドラキュー
     */
    void register(List<Handler> handlerQueue) {
        ListWrapper.wrap(handlerQueue)
                   .insert(this)
                   .after(HttpResponseHandler.class);
    }
}
