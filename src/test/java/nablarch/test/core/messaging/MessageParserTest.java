package nablarch.test.core.messaging;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.repository.SystemRepository;
import nablarch.core.repository.di.DiContainer;
import nablarch.core.repository.di.config.xml.XmlComponentDefinitionLoader;
import nablarch.test.RepositoryInitializer;
import nablarch.test.core.reader.DataType;
import nablarch.test.core.reader.MessageParser;
import nablarch.test.core.reader.PoiXlsReader;
import nablarch.test.core.util.interpreter.TestDataInterpreter;

import org.junit.AfterClass;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author T.Kawasaki
 */
public class MessageParserTest {

    /** テストデータからメッセージが取得できること。 */
    @Test
    public void testParseRequestMessage() {
    	
    	// 条件設定
        SystemRepository.clear();
        MessageParser target = new MessageParser(new PoiXlsReader(),
                Collections.<TestDataInterpreter>emptyList(),
                DataType.MESSAGE);
    	
        target.parse("src/test/java/nablarch/test/core/messaging", "MessageParserTest/testParse", "requestMessages");
        MessagePool pool = target.getResult();
        List<DataRecord> result = pool.toDataRecords();
        assertThat(result.size(), is(2));
        // １件目
        DataRecord first = result.get(0);
        assertThat(first.getString("ユーザ名"), is("電文太郎"));
        assertThat(first.getString("備考"), is("特筆なし"));
        assertThat(first.getString("FILLER"), is(""));
        // ２件目
        DataRecord second = result.get(1);
        assertThat(second.getString("ユーザ名"), is(""));
        assertThat(second.getString("備考"), is("ユーザ名が空欄なのでエラーが発生します。"));
        assertThat(second.getString("FILLER"), is(""));

        // FW制御ヘッダ
        Map<String, String> expected = new HashMap<String, String>() {
            {
                put("requestId", "hoge");
                put("userId", "moge");
            }
        };
        Map<String, String> actual = pool.getFwHeader();
        assertThat(actual, is(expected));
    }
    
    /** 
     *  テストデータからメッセージが取得できること。
     *  (FW制御ヘッダに項目追加した場合のテスト）
     *  */
    @Test
    public void testParseRequestMessageAdd() {
    	
    	// 条件設定
        SystemRepository.clear();
        XmlComponentDefinitionLoader loader = new XmlComponentDefinitionLoader("nablarch/test/core/messaging/reader.xml");
        SystemRepository.load(new DiContainer(loader));
    	
        MessageParser target = new MessageParser(new PoiXlsReader(),
                Collections.<TestDataInterpreter>emptyList(),
                DataType.MESSAGE);
    	
        target.parse("src/test/java/nablarch/test/core/messaging", "MessageParserTest/testParseAddFields", "requestMessages");
        MessagePool pool = target.getResult();
        List<DataRecord> result = pool.toDataRecords();
        assertThat(result.size(), is(2));
        // １件目
        DataRecord first = result.get(0);
        assertThat(first.getString("ユーザ名"), is("電文太郎"));
        assertThat(first.getString("備考"), is("特筆なし"));
        assertThat(first.getString("FILLER"), is(""));
        // ２件目
        DataRecord second = result.get(1);
        assertThat(second.getString("ユーザ名"), is(""));
        assertThat(second.getString("備考"), is("ユーザ名が空欄なのでエラーが発生します。"));
        assertThat(second.getString("FILLER"), is(""));

        // FW制御ヘッダ
        Map<String, String> expected = new HashMap<String, String>() {
            {
            	put("addFields", "hogehoge");
            	put("requestId", "hoge");
                put("userId", "moge");
            }
        };
        Map<String, String> actual = pool.getFwHeader();
        assertThat(actual, is(expected));
    }
    

    @AfterClass
    public static void afterClass(){
        SystemRepository.clear();
        RepositoryInitializer.initializeDefaultRepository();
    }


}