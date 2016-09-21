package nablarch.test.core.reader;

import static nablarch.core.util.StringUtil.isNullOrEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;
import nablarch.test.NablarchTestUtils;
import nablarch.test.core.file.FixedLengthFile;
import nablarch.test.core.messaging.MessagePool;
import nablarch.test.core.messaging.RequestTestingMessagePool;
import nablarch.test.core.util.interpreter.TestDataInterpreter;

/**
 * メッセージを解析するクラス。
 *
 * @author T.Kawasaki
 */
public class MessageParser extends SingleDataParsingTemplate<MessagePool> {

    /** 代理のパーサ */
    private final FixedLengthFileParser delegate;

    /** FW制御ヘッダ */
    private final Map<String, String> fwHeader = new HashMap<String, String>();

    /** SystemRepositoryに設定するFW制御ヘッダの項目名を管理するためのキー */
    private static final String FW_HEADER_KEY = "reader.fwHeaderfields";
    
    /**
     * コンストラクタ。
     *
     * @param reader       リーダ
     * @param interpreters 解釈クラス
     * @param targetType   処理対象のデータ型
     */
    public MessageParser(TestDataReader reader, List<TestDataInterpreter> interpreters, DataType targetType) {
        super(reader, interpreters, targetType);
        delegate = createFixedLengthFileParser(reader, interpreters, targetType);
    }

    /**
     * パーサを生成する。
     * @param reader       リーダ
     * @param interpreters 解釈クラス
     * @param targetType   処理対象のデータ型
     * @return パーサ
     */
    protected FixedLengthFileParser createFixedLengthFileParser(
            TestDataReader reader, List<TestDataInterpreter> interpreters,
            DataType targetType) {
        
        return new FixedLengthFileParser(reader, interpreters, targetType) {
            @Override
            protected void onReadingNames(List<String> line) {
                ArrayList<String> temp = new ArrayList<String>(line);
                if (!temp.isEmpty()) {
                    temp.remove(0);
                    temp.add(0, "default");
                }
                super.onReadingNames(temp);
            }

            @Override
            protected void onReadingValues(List<String> line) {
                if (StringUtil.isNullOrEmpty(line)) {
                    return;  // 空行の場合
                }
                // データ行の場合
                currentFragment.addValue(tail(line));
            }

            @Override
            protected boolean processDirectives(List<String> line) {
                if (super.processDirectives(line)) {
                    return true;
                }

                String fieldName = line.get(0);
                String value = line.get(1);
                if (isFrameworkHeader(fieldName)) {
                    fwHeader.put(fieldName, value);
                    return true;
                }

                return false;
            }
        };
    }

    /**
     * フレームワーク制御ヘッダであるかどうか判定する。
     *
     * @param name 名前
     * @return フレームワーク制御ヘッダの場合、真
     */
    private boolean isFrameworkHeader(String name) {
        return fwHeaderFields.contains(name);
    }

    /** フレームワーク制御ヘッダフィールド一覧 */
    private Set<String> fwHeaderFields
        = isNullOrEmpty(SystemRepository.getString(FW_HEADER_KEY))
        ? NablarchTestUtils.asSet("requestId", "userId", "resendFlag", "resultCode")
        : NablarchTestUtils.asSet(NablarchTestUtils.makeArray(SystemRepository.getString(FW_HEADER_KEY)));

    /** {@inheritDoc} */
    @Override
    void onReadLine(List<String> line) {
        delegate.onReadLine(line);
    }

    /** {@inheritDoc} */
    @Override
    void onTargetTypeFound(List<String> line) {
        delegate.onTargetTypeFound(line);
    }

    /** {@inheritDoc} */
    @Override
    public MessagePool getResult() {
        List<FixedLengthFile> data = delegate.getResult();
        if (data.isEmpty()) {
            return null;
        }
        FixedLengthFile body = data.get(0);
        return new RequestTestingMessagePool(body, fwHeader);
    }
    
    /**
     * 代理のパーサを取得する。
     * @return 代理のパーサ
     */
    FixedLengthFileParser getDelegate() {
        return this.delegate;
    }
    
    /**
     * FWヘッダを取得する。
     * @return FWヘッダ
     */
    Map<String, String> getFwHeader() {
        return this.fwHeader;
    }
}
