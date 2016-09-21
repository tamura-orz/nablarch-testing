package nablarch.test.core.messaging;

import static nablarch.core.util.Builder.concat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.dataformat.DataRecordFormatter;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.initialization.Initializable;
import nablarch.core.util.StringUtil;
import nablarch.fw.messaging.MessageSendSyncTimeoutException;
import nablarch.fw.messaging.MessagingContext;
import nablarch.fw.messaging.MessagingException;
import nablarch.fw.messaging.MessagingProvider;
import nablarch.fw.messaging.ReceivedMessage;
import nablarch.fw.messaging.SendingMessage;
import nablarch.fw.messaging.logging.MessagingLogUtil;
import nablarch.fw.messaging.provider.MessagingExceptionFactory;
import nablarch.test.Assertion;
import nablarch.test.core.file.DataFileFragment;
import nablarch.test.core.reader.DataType;

/**
 * リクエスト単体テストの際に、テストデータの内容にもとづき、要求電文のアサートおよび応答電文の返却を行うMessagingProvider。
 * <p>
 * 実際の要求電文のアサートおよび応答電文の返却処理は、{@link RequestTestingMessagingContext}に委譲する。
 * </p>
 * <p>
 * 本クラスを使用する場合、キューへのアクセスは行われない。
 * </p>
 * @author Masato Inoue
 */
public class RequestTestingMessagingProvider implements MessagingProvider, Initializable {
    
    /**
     * モックのMessagingContextを返却する。
     * @return MessagingContext モックのMessagingContext
     */
    public MessagingContext createContext() {
        return new RequestTestingMessagingContext();
    }

    /**
     * テストを行う際に、要求電文のアサートおよび応答電文を返却するMessagingContext。
     * <p>
     * アサート用の要求電文および応答電文は、Excelのテストケースから読み込むことを想定している。
     * </p>
     * <p>
     * 本クラスを使用する場合、キューへのアクセスは行われない。
     * </p>
     * @author Masato Inoue
     */
    public static class RequestTestingMessagingContext extends MessagingContext {

        /** メッセージングログを出力するロガー */
        private static final Logger LOGGER = LoggerManager.get("MESSAGING");
        
        /** 呼び出し元から送信された要求電文をキャッシュするstatic領域 */
        private static final List<SendingMessage> SENDING_MESSAGE_CACHE =
            new ArrayList<SendingMessage>();

        /** Excelシート名を取得する */
        private static String sheetName = null;
        /** テストケースNoを取得する */
        private static Integer caseNo = null;
        /** テストクラス名 */
        private static Class<?> testClass = null;
        /** 応答電文のID */
        private static String responseMessageId = null;
        /** 要求電文（期待値）のID */
        private static String expectedRequestMessageId = null;
        /** 本機能を有効にするかどうか */
        private static boolean isMockEnable = false;
        
        /**
         * リクエスト単体テスト時の初期化処理を行う。
         * <p>
         * 本機能のリクエスト単体テストを動作させるために必要なプロパティをstatic領域に設定する。
         * また、要求電文のキャッシュをクリアする。
         * </p>
         * @param clazz テストケースのクラス
         * @param sheetName シート名
         * @param no テストケース番号
         * @param responseMessageId 応答電文のID
         * @param expectedMessageId 要求電文のID
         */
        public static void initializeForRequestUnitTesting(Class<?> clazz,
                String sheetName, String no, String responseMessageId, String expectedMessageId) {
            if (StringUtil.isNullOrEmpty(expectedMessageId)
                    && StringUtil.isNullOrEmpty(responseMessageId)) {
                return;
            }
            RequestTestingMessagingContext.isMockEnable = true;
            RequestTestingMessagingContext.testClass = clazz;
            RequestTestingMessagingContext.sheetName = sheetName;
            RequestTestingMessagingContext.caseNo = Integer.parseInt(no);
            RequestTestingMessagingContext.responseMessageId = responseMessageId;
            RequestTestingMessagingContext.expectedRequestMessageId = expectedMessageId;
        }
        
        /**
         * リクエスト単体テスト時に使用する要求電文のキャッシュをクリアする。
         * <p>
         * 本機能のリクエスト単体テストで使用する要求電文のキャッシュをクリアする。
         * </p>
         */
        public static void clearSendingMessageCache() {
            RequestTestingMessagingContext.SENDING_MESSAGE_CACHE.clear();
            isMockEnable = false;
        }
        
        /** sendSyncメソッドが呼ばれるたびに発行するメッセージIDのカウント */
        private static int sendSyncCount = 0;

        /**
         * {@inheritDoc}<br/>
         * この実装では、sendSyncメソッドをオーバーライドし、要求電文のアサートおよび、応答電文の返却を行う。
         * <p>
         * 本メソッドは、要求電文ヘッダに「requestId」という名前のフィールドがある前提で動作する。
         * </p>
         */
        @Override
        public ReceivedMessage sendSync(SendingMessage message, long timeout) {

            if (!isMockEnable) {
                throw new RuntimeException(
                        "expectedMessage and responseMessage was not specified in test data. expectedMessage and responseMessage must be specified.");
            }

            String messageId = "messageId" + ++sendSyncCount;
            message.setMessageId(messageId);

            try {

                String requestId = (String) message.getRecords().get(0)
                        .get("requestId");

                // 要求電文をログに出力する
                emitLog(message);

                // 送信されたメッセージをキャッシュする。ここでキャッシュした内容は、後でアサートに使用される
                SENDING_MESSAGE_CACHE.add(message);

                // 応答メッセージを生成し、返却する
                ReceivedMessage reply = createReceivedMessage(requestId);

                if (reply == null) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.logInfo("response timeout: could not receive a reply to the message below within "
                                + timeout
                                + "msec. "
                                + MessagingLogUtil.getSentMessageLog(message));
                    }
                    throw new MessageSendSyncTimeoutException(String.format(
                        "caused by timeout, failed to send message. "
                      + "requestId = [%s], retryCount = [0]", requestId
                    ), 0);
                }
                reply.setMessageId(messageId);

                // 応答電文をログに出力する
                emitLog(reply);

                return reply;
            } catch (MessagingException e) {
                // MessagingExceptionの場合は、アサートを行う必要があるので、isMockEnable属性はtrueのままにしておく
                throw e;
            } catch (RuntimeException e) {
                // MessagingException以外の例外の場合は、この時点でテストに失敗しているので、isMockEnable属性をfalseにし、アサートが走らないようにする
                isMockEnable = false;
                throw e;
            }
        }
        
        /**
         * 任意の応答電文を返却する。
         * <p>
         * 返却する応答電文は、Excelから読み込む。
         * </p>
         * @param requestId リクエストID
         * @return 応答電文
         */
        private ReceivedMessage createReceivedMessage(String requestId) {
            
            RequestTestingSendSyncSupport support = new RequestTestingSendSyncSupport(testClass);

            // 応答電文を取得する
            RequestTestingMessagePool expectedRequestHeaderMessage = support
                    .getResponseMessage(sheetName, requestId,
                            caseNo, responseMessageId, DataType.RESPONSE_HEADER_MESSAGES, true);
            RequestTestingMessagePool expectedRequestBodyMessage = support
                    .getResponseMessage(sheetName, requestId,
                            caseNo, responseMessageId, DataType.RESPONSE_BODY_MESSAGES, true);

            // ヘッダと本文のバイナリを取得する
            byte[] headerBytes = expectedRequestHeaderMessage.createRequestTestingReceivedMessageBinary(sheetName,
                    caseNo, responseMessageId, DataType.RESPONSE_BODY_MESSAGES, requestId);
            byte[] bodyBytes = expectedRequestBodyMessage.createRequestTestingReceivedMessageBinary(sheetName,
                    caseNo, responseMessageId, DataType.RESPONSE_BODY_MESSAGES, requestId);
            
            // リトライの場合はnullを返却する
            if (bodyBytes == null) {
                return null;
            }
            
            // ヘッダと本文のバイナリからReceivedMessageを生成する
            int bufferSize = (headerBytes != null ? headerBytes.length : 0) + bodyBytes.length;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            if (headerBytes != null) {
                buffer.put(headerBytes);
            }
            buffer.put(bodyBytes);
            return new ReceivedMessage(buffer.array());
        }

        /**
         * 要求メッセージのアサートを行う。
         * @param testClass テストクラス
         * @param sheetName シート名
         * @param caseNo テストケース番号
         * @param expectedRequestMessageId 要求電文（期待値）のID
         */
        public static void assertSendingMessage(Class<?> testClass, String sheetName, String caseNo, String expectedRequestMessageId) {
            
            if (!isMockEnable) {
                return;
            }
            
            // 要求メッセージ（期待値）のIDが設定されていない場合は、テストは行わない
            if (expectedRequestMessageId == null) {
                return;
            }
            
            RequestTestingSendSyncSupport support = new RequestTestingSendSyncSupport(testClass);
            
            Integer caseNoInt = Integer.parseInt(caseNo);
            
            // メッセージIDに紐付くアサート用の要求電文（ヘッダ）を取得する
            List<RequestTestingMessagePool> expectedHeaderMessageList = 
                support.getExpectedRequestMessage(sheetName, caseNoInt, expectedRequestMessageId, DataType.EXPECTED_REQUEST_HEADER_MESSAGES, true);
            // メッセージIDに紐付くアサート用の要求電文（本文）を取得する
            List<RequestTestingMessagePool> expectedBodyMessageList = 
                support.getExpectedRequestMessage(sheetName, caseNoInt, expectedRequestMessageId, DataType.EXPECTED_REQUEST_BODY_MESSAGES, true);

            // アサート用の要求電文をリクエストID単位にまとめて保持する
            Map<String, Map<String, RequestTestingMessagePool>> expectedHeaderAndBodyMap = buildExpectedHeaderAndBodyMap(
                    expectedHeaderMessageList, expectedBodyMessageList);

            // 要求電文のアサート
            assertSendingMessage(testClass, caseNoInt, expectedHeaderAndBodyMap, SENDING_MESSAGE_CACHE);
        }

        /** Mapのヘッダが格納されるキー */
        private static final String KEY_HEADER = "header";
        /** Mapの本文が格納されるキー */
        private static final String KEY_BODY = "body";
        
        /**
         * 要求メッセージのアサートを行う。
         * @param testClass テストクラス
         * @param caseNoInt テストケース番号
         * @param expectedHeaderAndBodyMap 要求電文の期待値を保持するMap
         * @param sendingMessageCache 要求メッセージのキャッシュ
         */
        private static void assertSendingMessage(
                Class<?> testClass, Integer caseNoInt,
                Map<String, Map<String, RequestTestingMessagePool>> expectedHeaderAndBodyMap, List<SendingMessage> sendingMessageCache) {
            
            // リクエストIDの単位でアサートを行う
            for (Map.Entry<String, Map<String, RequestTestingMessagePool>> entry : expectedHeaderAndBodyMap
                    .entrySet()) {

                String requestId = entry.getKey();
                
                // エラーメッセージを生成する
                String msgOnFail = concat("case no=[", caseNoInt, "], message id=[" , expectedRequestMessageId , "]"
                        , ", request id=[", requestId , "] test class=[", testClass.getName(), "].");
                
                Map<String, RequestTestingMessagePool> expectedPoolMap = entry.getValue();

                // テストケースに対応するヘッダを取得する
                if (!expectedPoolMap.containsKey(KEY_HEADER)) {
                    Assertion.fail("not expected header message was send. ", msgOnFail);
                }
                RequestTestingMessagePool expectedHeaderPool = expectedPoolMap.get(KEY_HEADER);
                DataRecordFormatter headerFormatter = expectedHeaderPool.getFormatter();

                if (!expectedPoolMap.containsKey(KEY_BODY)) {
                    Assertion.fail("not expected body message was send. ", msgOnFail);
                }
                RequestTestingMessagePool expectedBodyPool = expectedPoolMap.get(KEY_BODY);
                DataRecordFormatter bodyFormatter = expectedBodyPool.getFormatter();
                
                // 期待する送信メッセージのヘッダのリストを取得する
                List<DataRecord> expectedHeaderRecords = expectedHeaderPool.getExpectedMessageList();
                
                // 期待する送信メッセージの本文のリストを取得する
                List<DataRecord> expectedBodyRecords = expectedBodyPool.getExpectedMessageList();

                // 期待する送信メッセージのヘッダと、期待する送信メッセージの本文の定義の数が一致しない場合、例外をスローする
                if (expectedHeaderRecords.size() != expectedBodyRecords.size()) {
                    throw new IllegalStateException(String
                            .format("number of lines of header and body does not match. "
                                    + "number of lines of header=["
                                    + expectedHeaderRecords.size()
                                    + "], but number of lines of body=[" + expectedBodyRecords.size()
                                    + "]. " + msgOnFail));
                }
                
                // リクエストIDに紐付く送信メッセージを取得する
                List<SendingMessage> sendingMessages = getSendingMessage(requestId, sendingMessageCache);

                
                // 送信されるメッセージの回数が、期待しているメッセージのサイズが小さい場合、アサーション例外をスローする
                if (sendingMessages.size() > expectedHeaderRecords.size()) {
                    Assertion.fail(String
                            .format("number of send message was invalid. "
                                    + "expected number=["
                                    + expectedHeaderRecords.size()
                                    + "], but actual number=[" + sendingMessages.size()
                                    + "]. " + msgOnFail));
                }
                // 送信されたメッセージのサイズより、期待しているメッセージのサイズが大きい場合、アサーション例外をスローする
                if (sendingMessages.size() < expectedHeaderRecords.size()) {
                    Assertion.fail(String
                            .format("number of send message was invalid. "
                                    + "expected number=["
                                    + expectedHeaderRecords.size()
                                    + "], but actual number=[" + sendingMessages.size()
                                    + "]. " + msgOnFail));
                }
                
                
                ArrayBlockingQueue<SendingMessage> queue = new ArrayBlockingQueue<SendingMessage>(sendingMessages.size(), true, sendingMessages);

                // リクエストID単位でアサートを行う
                for (int i = 0; i < expectedHeaderRecords.size(); i++) {
                    DataRecord expectedHeaderRecord = expectedHeaderRecords.get(i);
                    DataRecord expectedBodyRecord = expectedBodyRecords.get(i);

                    String headerNo = (String) expectedHeaderRecord.remove(
                            DataFileFragment.FIRST_FIELD_NO); // 識別子（連番）情報をリストから削除
                    String bodyNo = (String) expectedBodyRecord.remove(
                            DataFileFragment.FIRST_FIELD_NO); // 識別子（連番）情報をリストから削除

                    SendingMessage sendingMessage = queue.poll();
                    if (sendingMessage == null) {
                        throw new RuntimeException(msgOnFail + " test no=[" + headerNo + "]." + " message was not found in sheet.");
                    }

                    // 要求電文を読み込むために使用するReceivedMessageを生成する
                    ReceivedMessage requestMessage = new ReceivedMessage(
                            sendingMessage.getBodyBytes());
                    // ヘッダを読み込む
                    DataRecord actualHeaderRecord =
                            readProtected(requestMessage, headerFormatter, msgOnFail);
                    Assertion.assertEquals("header message assertion failed. "
                            + msgOnFail + " test no=[" + headerNo + "].",
                            expectedHeaderRecord, actualHeaderRecord);
                    // 本文を読み込む
                    DataRecord actualBodyRecord =
                            readProtected(requestMessage, bodyFormatter, msgOnFail);
                    Assertion.assertEquals("body message assertion failed. "
                            + msgOnFail + " test no=[" + bodyNo + "].",
                                           expectedBodyRecord, actualBodyRecord);
                }
            }
        }

        /**
         * 受信メッセージからデータレコードの読み込みを行う。<br/>
         * データ読み込みに失敗する場合があるため、
         * 読み込み失敗時には、引数で与えられた文言を付与した例外をリスローする。
         *
         * @param receivedMessage 受信メッセージ
         * @param formatter       読み込み時に使用するフォーマット
         * @param msgOnFail       読み込み失敗時の文言
         * @return 読み込んだデータレコード1件
         */
        private static DataRecord readProtected(ReceivedMessage receivedMessage,
                                                DataRecordFormatter formatter,
                                                String msgOnFail) {
            try {
                return receivedMessage.setFormatter(formatter).readRecord();
            } catch (RuntimeException e) {
                throw new RuntimeException(msgOnFail, e);
            }
        }

        /** Mapの本文が格納されるキー */
        private static final String KEY_REQUEST_ID = "requestId";
        
        /**
         * リクエストIDに紐付く、送信メッセージのリストを取得する。
         * @param expectedRequestId リクエストID 
         * @param sendingMessageCache 要求メッセージのキャッシュ
         * @return 送信するメッセージのリスト
         */
        private static List<SendingMessage> getSendingMessage(String expectedRequestId,
                List<SendingMessage> sendingMessageCache) {
            
            ArrayList<SendingMessage> list = new ArrayList<SendingMessage>();
            
            for (SendingMessage message : sendingMessageCache) {
                String requestId = (String) message.getRecords().get(0)
                        .get(KEY_REQUEST_ID);
                if (expectedRequestId.equals(requestId)) {
                    list.add(message);
                }
            }
            
            return list;
        }

        /**
         * 要求電文の期待値を保持するMapを生成する。
         * @param expectedHeaderMessageList ヘッダの期待値のリスト
         * @param expectedBodyMessageList 本文の期待値のリスト
         * @return 要求電文の期待値を保持するMap
         */
        private static Map<String, Map<String, RequestTestingMessagePool>> buildExpectedHeaderAndBodyMap(
                List<RequestTestingMessagePool> expectedHeaderMessageList,
                List<RequestTestingMessagePool> expectedBodyMessageList) {

            Map<String, Map<String, RequestTestingMessagePool>> result = new HashMap<String, Map<String, RequestTestingMessagePool>>();

            for (RequestTestingMessagePool pool : expectedHeaderMessageList) {
                String requestId = pool.getRequestId();
                Map<String, RequestTestingMessagePool> map = new HashMap<String, RequestTestingMessagePool>();
                map.put(KEY_HEADER, pool);
                result.put(requestId, map);
            }
            for (RequestTestingMessagePool pool : expectedBodyMessageList) {
                String requestId = pool.getRequestId();
                if (result.containsKey(requestId)) {
                    result.get(requestId).put(KEY_BODY, pool);
                } else {
                    Map<String, RequestTestingMessagePool> map = new HashMap<String, RequestTestingMessagePool>();
                    map.put(KEY_BODY, pool);
                    result.put(requestId, map);
                }
            }
            
            return result;
        }
        
        /** {@inheritDoc}<br/>
         * この実装では何も行わない。
         */
        @Override
        public void close() {
            // nop
        }

        /** {@inheritDoc}<br/> */
        @Override
        public String send(
                SendingMessage message) throws UnsupportedOperationException {

            // 要求電文をログに出力する
            emitLog(message);

            // 送信されたメッセージをスレッドローカルにキャッシュする。ここでキャッシュした内容は、後でアサートに使用される
            SENDING_MESSAGE_CACHE.add(message);

            return "messageId";
        }

        /** {@inheritDoc}<br/> */
        @Override
        public String sendMessage(
                SendingMessage message) throws UnsupportedOperationException {
            return send(message);
        }

        /** {@inheritDoc}<br/>
         * この実装ではこのメソッドはサポートしない。
         * @throws UnsupportedOperationException このメソッドが呼ばれた場合にスローされる例外
         */
        @Override
        public ReceivedMessage receiveMessage(String receiveQueue,
                String messageId, long timeout) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("this method was unsupported.");
        }

    }

    /**{@inheritDoc}
     * この実装では何もしない。
     */
    public MessagingProvider setDefaultResponseTimeout(long timeout) {
        // nop
        return this;
    }

    /**{@inheritDoc}
     * この実装では何もしない。
     */
    public MessagingProvider setDefaultTimeToLive(long timeToLive) {
        // nop
        return this;
    }

    /**{@inheritDoc}
     * この実装では何もしない。
     */
    public MessagingProvider setMessagingExceptionFactory(MessagingExceptionFactory messagingExceptionFactory) {
        // nop
        return this;
    }

    /**
     * {@inheritDoc}<br/>
     * 他の実装クラスとインタフェースを合わせるために{@link Initializable}を実装する。
     * {@link Initializable}を実装することで、リクエスト単体テスト時に
     * {@link nablarch.core.repository.initialization.ApplicationInitializer}の
     * リポジトリ設定の上書きを不要にしている。
     * 本メソッドは何も処理しない。
     */
    public void initialize() {
    }
}
