package nablarch.fw.web;

import static nablarch.test.StringMatcher.startsWith;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.web.download.StreamResponse;
import nablarch.common.web.handler.HttpAccessLogHandler;
import nablarch.core.ThreadContext;
import nablarch.core.util.Builder;
import nablarch.fw.ExecutionContext;
import nablarch.fw.web.handler.ResourceMapping;
import nablarch.test.core.log.LogVerifier;
import nablarch.test.support.tool.Hereis;

import org.junit.Before;
import org.junit.Test;

public class HttpServerTest {

    @Before
    public void setUp() throws Exception {
        ThreadContext.clear();
    }

    @Test
    public void testAccessorsToPortNumber() {
        HttpServer server = new HttpServer();
        assertEquals(7777, server.getPort());
        server.setPort(8080);
        assertEquals(8080, server.getPort());
    }

    @Test
    public void testAccessorsToServletContext() {
        HttpServer server = new HttpServer();
        assertEquals("/", server.getServletContextPath());
        server.setServletContextPath("/app");
        assertEquals("/app", server.getServletContextPath());
    }

    @Test
    public void testHandlingOfServletContextPath()
    throws Exception {
        HttpServer server = new HttpServer()
        .setServletContextPath("/nabla_app")
        .setWarBasePath("classpath://nablarch/fw/web/sample/app/")
        .addHandler(new InternalMonitor())
        .addHandler("/path/to/somewhere/Greeting", new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                ctx.setRequestScopedVar("greeting", "Hello World!");
                return new HttpResponse(201).setContentPath(
                        "servlet:///jsp/index.jsp"
                );
            }
        })
        .addHandler("/path/to/somewhere/ja/Greeting", new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                ctx.setRequestScopedVar("greeting", "こんにちは");
                return new HttpResponse(201).setContentPath(
                        "servlet:///ja/jsp/index.jsp"
                );
            }
        })
        .startLocal();
        
        HttpResponse res = server.handle(new MockHttpRequest(
                "GET /nabla_app/path/to/somewhere/Greeting HTTP/1.1"), null);

        
        assertEquals(201, res.getStatusCode());
        assertEquals(201, InternalMonitor.response.getStatusCode());
        assertThat(res.getBodyString(), equalToIgnoringWhiteSpace(Builder.lines(
        "<html>",
        "  <head>",
        "    <title>Greeting Service</title>",
        "  </head>",
        "  <body>",
        "    Hello World!",
        "  </body>",
        "</html>"
        )));

        res = server.handle(new MockHttpRequest(
                "GET /nabla_app/path/to/somewhere/ja/Greeting HTTP/1.1"), null);

        assertEquals(201, res.getStatusCode());
        assertEquals(201, InternalMonitor.response.getStatusCode());
        assertThat(res.getBodyString(), equalToIgnoringWhiteSpace(Builder.lines(
        "",
        "<html>",
        "  <head>",
        "    <title>元気にあいさつ！</title>",
        "  </head>",
        "  <body>",
        "    こんにちは",
        "  </body>",
        "</html>"
        )));
    }

    /**
     * リダイレクションのテスト
     */
    @Test
    public void testRedirection() {
        HttpServer server = new HttpServer()
        .setServletContextPath("/nabla_app")
        .setWarBasePath("classpath://nablarch/fw/web/sample/app/")
        .addHandler("/path/that/shouldNotBeRead//", new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                return new HttpResponse("redirect:///redirectTo/caution.html");
            }
        })
        .startLocal();
        
        HttpResponse res = server.handle(new MockHttpRequest(Hereis.string()), null);
        /*************************************************************
        GET /nabla_app/path/that/shouldNotBeRead/page.html HTTP/1.1
        **************************************************************/

        // このレスポンスは内蔵サーバが返却したもの。
        assertEquals(302, res.getStatusCode());
        assertEquals("http://127.0.0.1/nabla_app/redirectTo/caution.html", res.getLocation());
    }

    @Test
    public void testThatWarBasePathCanBeAssigned() {
        HttpServer server = new HttpServer()
            .addHandler("//*.jsp", new ResourceMapping("/", "servlet:///"))
            .startLocal();
        
        ExecutionContext ctx = new ExecutionContext();
        HttpResponse res;
        
        assertEquals(
            "classpath://nablarch/fw/web/servlet/docroot/",
            server.getWarBasePath().toString()
        );
        res = server.handle(new MockHttpRequest(Hereis.string()), ctx);
        /*****************************
        GET /jsp/index.jsp HTTP/1.1
        ******************************/
        assertEquals(404, res.getStatusCode());
        
        
        server.setWarBasePath("classpath://nablarch/fw/web/sample/app/")
              .startLocal();
        
        assertEquals(
            "classpath://nablarch/fw/web/sample/app/",
            server.getWarBasePath().toString()
        );
        res = server.handle(new MockHttpRequest(Hereis.string()), ctx);
        /*****************************
        GET /jsp/index.jsp HTTP/1.1
        ******************************/
        assertEquals(200, res.getStatusCode());
    }

    @Test
    public void testAccessorsToWarBasePathWithInvalidArgument() {
        HttpServer server = new HttpServer();
        try {
            server.setWarBasePath("invalid");
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        
        try {
            server.setWarBasePath("servlet://jsp/");
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        
        try {
            server.setWarBasePath("forward://jsp/");
            fail();
        } catch (IllegalArgumentException e){
            assertTrue(true);
        }
        
        try {
            server.setWarBasePath("classpath://java/net/");
            fail();
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
            assertEquals(
                "there was no war archive or context-root path at: classpath://java/net/"
              , e.getMessage()
            );
        }
        
        try {
            server.setWarBasePath("classpath://java/lang/Object.class");
            fail();
        } catch (Exception e){
            assertEquals(IllegalArgumentException.class, e.getClass());
            assertEquals(
                "WAR base path can not be a JAR interior path. "
              + "Assign the path of the WAR archive itself "
              + "or a context-root directory path of a extracted WAR archive."
              , e.getMessage()
            );
        }
    }

    @Test
    public void testHandle() {
        HttpServer server = new HttpServer()
        .addHandler("/test/", new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                return new HttpResponse(200).write("hello world");
            }
        })
        .startLocal();
        
        HttpResponse res = server.handle(
            new MockHttpRequest(Hereis.string()),
            /****************************
            GET /test/ HTTP/1.1
            *****************************/
            new ExecutionContext()
        );
        assertEquals(200, res.getStatusCode());
        assertEquals("hello world", res.getBodyString().trim());
        
        server.addHandler("/test2/", new Object() {
            public HttpResponse getIndex(HttpRequest req, ExecutionContext ctx) {
                return new HttpResponse(200).write("hello world");
            }
        });
        
        res = server.handle(
            new MockHttpRequest("GET /test2/index HTTP/1.1"),
            new ExecutionContext()
        );
        assertEquals(200, res.getStatusCode());
        assertEquals("hello world", res.getBodyString().trim());
        
        
        res = server.handle(
            new MockHttpRequest("GET /test2/index?hoge=fuga HTTP/1.1"),
            new ExecutionContext()
        );
        assertEquals(200, res.getStatusCode());
        assertEquals("hello world", res.getBodyString().trim());
        
        res = server.handle(
            new MockHttpRequest("GET /test2/index;hoge=fuga HTTP/1.1"),
            new ExecutionContext()
        );
        assertEquals(200, res.getStatusCode());
        assertEquals("hello world", res.getBodyString().trim());
    }

    @Test
    public void testDefaultNotFoundPage() {
        HttpServer server = new HttpServer().startLocal();
        server.getHandlerQueue().add(0, new InternalMonitor());        
        
        ExecutionContext ctx = new ExecutionContext();
        HttpResponse res = server.handle(new MockHttpRequest(Hereis.string()), ctx);
        /*****************************
        GET /unknown/path HTTP/1.1
        ******************************/

        assertEquals(404, res.getStatusCode());
        assertEquals(404, InternalMonitor.response.getStatusCode());
        
        assertThat(res.getBodyString(), is(containsString("404")));
        //デフォルトでは組み込みHTTP Serverが応答を返していることを確認する。
        assertThat(res.getBodyString(), is(containsString("Jetty")));
    }

    @Test
    public void testDefaultSystemErrorPage() {
        HttpServer server = new HttpServer()
        .addHandler("/app/", new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                throw new RuntimeException();
            }
        })
        .startLocal();

        server.getHandlerQueue().add(0, new InternalMonitor());
        
        ExecutionContext ctx = new ExecutionContext();
        HttpResponse res = server.handle(new MockHttpRequest(Hereis.string()), ctx);
        /*****************************
        GET /app/ HTTP/1.1
        ******************************/
        
        assertEquals(500, res.getStatusCode());
        assertEquals(500, InternalMonitor.response.getStatusCode());

        assertThat(res.getBodyString(), is(containsString("500")));
        //デフォルトでは組み込みHTTP Serverが応答を返していることを確認する。
        assertThat(res.getBodyString(), is(containsString("Jetty")));
    }

    @Test
    public void testUnauthorizedErrorPage() {
        HttpServer server = new HttpServer()
        .addHandler("/secure//", new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                throw new HttpErrorResponse(401);
            }
        })
        .startLocal();
        
        server.getHandlerQueue().add(0, new InternalMonitor());
        
        ExecutionContext ctx = new ExecutionContext();
        HttpResponse res = server.handle(new MockHttpRequest(Hereis.string()), ctx);
        /*****************************
        GET /secure/page.jsp HTTP/1.1
        ******************************/
        
        assertEquals(401, res.getStatusCode());
        assertEquals(401, InternalMonitor.response.getStatusCode());

        assertThat(res.getBodyString(), is(containsString("401")));
        //デフォルトでは組み込みHTTP Serverが応答を返していることを確認する。
        assertThat(res.getBodyString(), is(containsString("Jetty")));
    }

    @Test
    public void testRedirecting() {
        HttpServer server = new HttpServer()
        .addHandler("/app/Dispatcher",new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                return HttpResponse.Status.SEE_OTHER
                                   .handle(req, ctx)
                                   .setLocation(req.getParam("nextPage")[0]);
            }
        })
        .startLocal();
        
        ExecutionContext ctx = new ExecutionContext();
        HttpResponse res = server.handle(new MockHttpRequest(Hereis.string()), ctx);
        /**************************************************
        GET /app/Dispatcher?nextPage=success HTTP/1.1
        **************************************************/
        
        assertEquals(303, res.getStatusCode());

        assertThat(res.getBodyString(), is(""));

    }

    public void testDumpHttpMessageGeneratedByJspEngine() throws Exception {
        HttpServer server = new HttpServer()
        .addHandler("/app/hasLink.do", new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                return new HttpResponse().setContentPath(
                    "servlet://jsp/hasLink.jsp"
                );
            }
        })
        .setWarBasePath("classpath://nablarch/fw/web/sample/")
        .setHttpDumpFilePath("/tmp/test.html")
        .startLocal();
        
        HttpRequest httpRequest = new MockHttpRequest(Hereis.string());
        /*************************************
        GET /app/hasLink.do?hoge=fuga HTTP/1.1
        *************************************/
        
        HttpResponse res = server.handle(httpRequest , null);
        
        assertEquals(200, res.getStatusCode());
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(
            new FileReader(new File("/tmp/test.html"))
        );
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            buffer.append(line).append(Builder.LS);
        }
        reader.close();
        String docRootPath = ResourceLocator
                             .valueOf("classpath://nablarch/fw/web/sample/")
                             .getRealPath();
        String expectingHtml = Hereis.string(docRootPath);
        /**********************
        <html>
        <head>
          <LINK REL="stylesheet" TYPE="text/css" HREF="./css/common.css" />
          <link rel="stylesheet" type="text/css" href="./app/./style.css" />
          <script language="javascript" src="./js/common.js" />
          <LINK REL="stylesheet" TYPE="text/css" HREF="./app/relative.css" />
          <LINK REL="stylesheet" TYPE="text/css" HREF="./app/hoge/relative.css" />
          <LINK REL="stylesheet" TYPE="text/css" HREF="./app/../hoge/relative.css" />
          <LINK REL="stylesheet" TYPE="text/css" HREF="./app/../../hoge/relative.css" />
        </head>
        <body>
          <p>Hello world</p>
          <p>
            <a href="./page/example.jsp">example</a>
          </p>
        </body>
        </html>
        **********************/
        assertEquals(expectingHtml.trim(), buffer.toString().trim());
    }

    @Test
    public void testThatAnErrorOccursIfServerStartWithInvalidConfiguration() {
        HttpServer server  = new HttpServer().setPort(8089);
        HttpServer server2 = new HttpServer().setPort(8089);
        try {
            server.start();
            server2.start();
            fail();
        } catch (RuntimeException e) {
            assertNotNull(e.getCause());
            assertEquals(BindException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testServerStart() throws InterruptedException {
        final HttpServer server = new HttpServer();
        Thread serverThread = new Thread() {
            public void run() {
                server.start();
                assertTrue(true);
                server.join();
            }
        };
        serverThread.start();
        Thread.currentThread().sleep(2000);
    }

    @Test
    public void testThatStripOutsJSessionIdFromStaticLink() {
        MockHttpRequest request = new MockHttpRequest();
        HttpServer httpServer = new HttpServer();
        String editedTag = httpServer.rewriteUriPath(Hereis.string(), request).toString();
        /********************************************************************
        <link rel="stylesheet" type="text/css" href="/css/reset.css;jsessionid=h60qsqhpuf091759hkbwi2833" charset="UTF-8" />
        **********************************************************************/
        
        assertEquals(Hereis.string(), editedTag);
        /********************************************************************
        <link rel="stylesheet" type="text/css" href="./css/reset.css" charset="UTF-8" />
        **********************************************************************/
    }

    @Test
    public void testHttpMessageDumpFacilities() throws Exception {
        File dumpRoot = new File("tmp/http_dump/");
        dumpRoot.mkdirs();
        for (File file : dumpRoot.listFiles()) {
            file.delete();
        }
        
        File docRoot = new File("tmp/doc_root/");
        docRoot.mkdirs();
        for (File file : docRoot.listFiles()) {
            file.delete();
        }

        HttpServer server = new HttpServer()
        .setHttpDumpRoot(dumpRoot.getPath())
        .setHttpDumpEnabled(true)
        .setWarBasePath("file://tmp/doc_root/")
        .addHandler("/app/test.html", new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                String crlf = "\r\n";
                ctx.invalidateSession();
                String body = Hereis.string();
                body = body.replaceAll("crlf", crlf);
                return new HttpResponse()
                       .setContentType("text/html;charset=utf8")
                       .write(body);
                /****************************
                <html>
                <head>
                  <LINK REL="stylesheet" HREF="/css/common.css" />
                  <link rel="stylesheet" href="./style.css" />
                  <script language="javascript" src="/js/common.js" />
                  <LINK REL="stylesheet" HREF="relative.css" />
                  <LINK REL="stylesheet" HREF="hoge/relative.css" />
                  <LINK REL="stylesheet" HREF="../hoge/relative.css" />
                  <LINK REL="stylesheet" HREF="../../hoge/relative.css" />                  
                </head>
                <body>
                  <p>Hello world</p>
                  <p>
                    <a href="/page/example.jsp">example</a>
                  </p>
                </body>
                </html>         
                *****************************/
            }
        })
        .startLocal();
        
        assertTrue(server.isHttpDumpEnabled());
        assertEquals(dumpRoot, server.getHttpDumpRoot());
        
        ExecutionContext ctx = new ExecutionContext();
        HttpRequest req = new MockHttpRequest(Hereis.string());
        /*************************************************************
        GET /app/test.html HTTP/1.1
        *************************************************************/
        
        HttpResponse res = server.handle(req, ctx);

        assertEquals(200, res.getStatusCode());
        assertEquals("text/html;charset=utf8", res.getContentType());
        assertEquals("/app/test.html", req.getRequestPath());
        
        File[] dumpFiles = dumpRoot.listFiles();
        assertEquals(1, dumpFiles.length);
        File dumpFile = dumpFiles[0];
        
        assertTrue(dumpFile.exists());
        assertTrue(dumpFile.getPath().endsWith(".html"));
        
        
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(dumpFile));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            buffer.append(line).append(Builder.LS);
        }
        reader.close();
        String docRootPath = docRoot.getAbsolutePath();
        String expectingHtml = Hereis.string(docRootPath);
        /**********************
        <html>
        <head>
          <LINK REL="stylesheet" HREF="./css/common.css" />
          <link rel="stylesheet" href="./app/./style.css" />
          <script language="javascript" src="./js/common.js" />
          <LINK REL="stylesheet" HREF="./app/relative.css" />
          <LINK REL="stylesheet" HREF="./app/hoge/relative.css" />
          <LINK REL="stylesheet" HREF="./app/../hoge/relative.css" />
          <LINK REL="stylesheet" HREF="./app/../../hoge/relative.css" />                  
        </head>
        <body>
          <p>Hello world</p>
          <p>
            <a href="./page/example.jsp">example</a>
          </p>
        </body>
        </html>
        ***********/
        assertEquals(expectingHtml.trim(), buffer.toString().trim());
    }

    /**
     * ダウンロードファイルがダンプされること。
     */
    @Test
    public void testHttpMessageDumpFacilitiesForDownload() throws Exception {
        File dumpRoot = new File("tmp/http_dump/");
        dumpRoot.mkdirs();
        for (File file : dumpRoot.listFiles()) {
            file.delete();
        }
        
        File docRoot = new File("tmp/doc_root/");
        docRoot.mkdirs();
        for (File file : docRoot.listFiles()) {
            file.delete();
        }
        
        HttpServer server = new HttpServer()
        .setHttpDumpRoot(dumpRoot.getPath())
        .setHttpDumpEnabled(true)
        .setWarBasePath("file://tmp/doc_root/")
        .addHandler(new HttpAccessLogHandler())
        .addHandler("/app/test.html", new HttpRequestHandler() {
            public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                File file = Hereis.fileWithEncoding("./work/テスト一時ファイル.txt", "UTF-8");
                /*
                あいうえお
                かきくけこさしすせそ
                たちつてとなにぬねのはひふへほ
                 */
                boolean deleteOnCleanup = true;
                StreamResponse response = new StreamResponse(file, deleteOnCleanup);
                response.setContentType("text/plain; charset=UTF-8");
                response.setContentDisposition(file.getName());
                return response;
            }
        })
        .startLocal();
        
        assertTrue(server.isHttpDumpEnabled());
        assertEquals(dumpRoot, server.getHttpDumpRoot());
        
        ExecutionContext ctx = new ExecutionContext();
        HttpRequest req = new MockHttpRequest(Hereis.string());
        /*************************************************************
        GET /app/test.html HTTP/1.1
        *************************************************************/

        String dumpFilePath = Builder.concat(
                dumpRoot.getAbsolutePath(), File.separatorChar,
                "01", ".", "html");
        
        server.setHttpDumpFilePath(dumpFilePath);

        HttpResponse res = server.handle(req, ctx);

        
        assertEquals(200, res.getStatusCode());
        assertEquals("text/plain; charset=UTF-8", res.getContentType());
        assertEquals("/app/test.html", req.getRequestPath());
        
        File[] dumpFiles = dumpRoot.listFiles();
        assertEquals(Arrays.asList(dumpFiles).toString(), 1, dumpFiles.length);
        File dumpFile = dumpFiles[0];
        
        assertTrue(dumpFile.exists());
        assertTrue(dumpFile.getPath().endsWith("テスト一時ファイル.txt"));
        
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(dumpFile));
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            buffer.append(line).append(Builder.LS);
        }
        String docRootPath = docRoot.getAbsolutePath();
        String expectingHtml = Hereis.string(docRootPath);
        /*
        あいうえお
        かきくけこさしすせそ
        たちつてとなにぬねのはひふへほ
         */
        assertEquals(expectingHtml.trim(), buffer.toString().trim());
    }
    
    /**
     * ダンプファイル作成失敗した場合に、エラーログからファイル名が特定できること。
     */
    @Test
    public void testHttpMessageDumpFacilitiesError() throws Exception {
        // このテストはWindowsでしか動作しない。
        // ダンプファイル名に改行コードを含めることで（test\1234.html）不正なファイル名としているが、
        // Unix上では不正なファイル名とみなされない。
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        assumeThat(isWindows, is(true));

        LogVerifier.setExpectedLogMessages(new ArrayList<Map<String, String>>() {
            {
                add(new HashMap<String, String>() {
                    {
                        put("logLevel", "WARN");
                        put("message1", "an error occurred while the http dump was being written. make sure dump file path is valid (especially file name). path = [\\tmp\\test\n1234.html]");
                    }
                });
            }
        });
        
        try {
            HttpServer server = new HttpServer()
            .addHandler("/app/hasLink.do", new HttpRequestHandler() {
                public HttpResponse handle(HttpRequest req, ExecutionContext ctx) {
                    return new HttpResponse().setContentPath(
                        "servlet://jsp/hasLink.jsp"
                    );
                }
            })
            .setWarBasePath("classpath://nablarch/fw/web/sample/")
            .setHttpDumpFilePath("/tmp/test\n1234.html")
            .startLocal();
            
            HttpRequest httpRequest = new MockHttpRequest(Hereis.string());
            /*************************************
            GET /app/hasLink.do?hoge=fuga HTTP/1.1
            *************************************/
            
            HttpResponse res = server.handle(httpRequest , null);
            fail();
        } catch (RuntimeException e) {
            assertEquals(IOException.class, e.getCause().getClass());
            assertTrue(e.getMessage().endsWith("path = [\\tmp\\test\n1234.html]"));
            
            // ----- assert log -----
            LogVerifier.verify("assert log");
        }

    }

    /** オーバレイディレクトリのテスト。*/
    @Test
    public void testOverLay() {
        List<ResourceLocator> paths = Arrays.asList(
                ResourceLocator.valueOf("classpath://nablarch/fw/web/sample/overlay/first/"),
                ResourceLocator.valueOf("classpath://nablarch/fw/web/sample/overlay/second/"),
                ResourceLocator.valueOf("classpath://nablarch/fw/web/sample/overlay/third/")
        );

        HttpServer server = new HttpServer()
                .setServletContextPath("/")
                .setWarBasePaths(paths)
                .addHandler("/*.html", new ResourceMapping().setBaseUri("/").setBasePath("servlet:///"));
        server.startLocal();

        // firstのみにあるリソースが取得できること
        HttpResponse first = server.handle(new MockHttpRequest("GET /first.html HTTP/1.1"), null);
        assertThat(first.getBodyString(), is("this is first.html"));
        assertThat(first.getStatusCode(), is(200));

        // secondのみにあるリソースが取得できること
        HttpResponse second = server.handle(new MockHttpRequest("GET /second.html HTTP/1.1"), null);
        assertThat(second.getBodyString(), is("this is second.html"));
        assertThat(second.getStatusCode(), is(200));

        // thirdのみにあるリソースが取得できること
        HttpResponse third = server.handle(new MockHttpRequest("GET /third.html HTTP/1.1"), null);
        assertThat(third.getBodyString(), is("this is third.html"));
        assertThat(third.getStatusCode(), is(200));

        // firstとsecondとthirdで重複したリソースを要求した場合、firstのリソースが優先されること
        HttpResponse duplicateHtml = server.handle(new MockHttpRequest("GET /duplicate.html HTTP/1.1"), null);
        assertThat(duplicateHtml.getBodyString(), is("this is resource of first module."));
        assertThat(duplicateHtml.getStatusCode(), is(200));

        // secondとthirdで重複したリソースを要求した場合、secondのリソースが優先されること
        HttpResponse duplicateHtml2 = server.handle(new MockHttpRequest("GET /duplicate2.html HTTP/1.1"), null);
        assertThat(duplicateHtml2.getBodyString(), is("this is resource of second module."));
        assertThat(duplicateHtml2.getStatusCode(), is(200));

    }

    /** 不正なWarベースパスが指定された場合、例外が発生すること。*/
    @Test
    public void testInvalidWarBasePath() {
        List<ResourceLocator> paths = Arrays.asList(
                ResourceLocator.valueOf("classpath://invalid/path/to/webApp/")
        );

        HttpServer server = new HttpServer().setWarBasePaths(paths);
        try {
            server.startLocal();
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("invalid warBasePath"));
        }
    }



    /**
     * サーバ起動前にhandleメソッドを起動した場合、例外が発生すること。
     */
    @Test
    public void testNotInitialized() {
        HttpServer server = new HttpServer();
        try {
            server.handle(new MockHttpRequest(), new ExecutionContext());
            fail();
        } catch (RuntimeException e) {
            assertThat(e.getMessage(), startsWith("this server is not running on a local connector."));
        }
    }

    /**
     * 一時ディレクトリの設定ができること。
     * （JettyAPI呼び出しをしているだけなので動作自体の確認はしない）
     */
    @Test
    public void testSetTempDir() {
        HttpServer server = new HttpServer();
        server.setTempDirectory(null);  // nothing happens.
        server.setTempDirectory(System.getProperty("java.io.tmpdir"));
    }
}
