package nablarch.test.core.messaging;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.dataformat.DataRecordFormatter;
import nablarch.core.dataformat.FieldDefinition;
import nablarch.core.dataformat.InvalidDataFormatException;
import nablarch.core.dataformat.LayoutDefinition;
import nablarch.core.dataformat.RecordDefinition;
import nablarch.fw.messaging.ReceivedMessage;
import nablarch.fw.messaging.SendingMessage;
import nablarch.test.core.file.FixedLengthFile;
import nablarch.test.core.file.TestDataConverter;
import nablarch.test.support.tool.Hereis;
import nablarch.test.RepositoryInitializer;

import org.junit.Before;
import org.junit.Test;
import org.junit.AfterClass;

/**
 * {@link MessagePool}のテストクラス
 * 
 * @author TIS
 */
public class MessagePoolTest {
    /**
     * ヘッダフォーマッターが存在しない場合は、本文のみのメッセージになること。
     */
    @Test
    public void testPutterWithoutHeaderFormatter() {
        
        // テストデータを固定的に作成するファイルオブジェクト
        FixedLengthFile source = createTestFile();
        
        // 送信メッセージを作成する
        Map<String, String> fwHeader = new HashMap<String, String>();
        fwHeader.put("key1", "value1");

        MessagePool pool = new MessagePool(source, fwHeader);
        MessagePool.Putter putter = pool.prepareForPut();
        SendingMessage sendingMessage = putter.createSendingMessage(null);
        byte[] actual = sendingMessage.getBodyBytes();
        
        byte[] expected = "HOGEHOGEHOGE".getBytes(); // HogeDataConvertorにより、本文部分がHOGEに置換される
        
        assertArrayEquals(expected, actual);
        
    }

    /**
     * ヘッダフォーマッターが存在する場合は、本文の前にヘッダーが追加されたメッセージになること。
     */
    @Test
    public void testPutterWithHeaderFormatter() {
        
        // テストデータを固定的に作成するファイルオブジェクト
        FixedLengthFile source = createTestFile();
        
        // 送信メッセージを作成する
        Map<String, String> fwHeader = new HashMap<String, String>();
        fwHeader.put("key1", "value1");

        MessagePool pool = new MessagePool(source, fwHeader);
        MessagePool.Putter putter = pool.prepareForPut();
        DummyHeaderFormatter dummyHeaderFormatter = new DummyHeaderFormatter();
        SendingMessage sendingMessage = putter.createSendingMessage(dummyHeaderFormatter);
        byte[] actual = sendingMessage.getBodyBytes();
        
        byte[] expected = "key1value1HOGEHOGEHOGE".getBytes(); // HogeDataConvertorにより、本文部分がHOGEに置換される
        
        assertArrayEquals(expected, actual);
        
    }

    @Test
    public void testComparator() {

        // テストデータを固定的に作成するファイルオブジェクト
        FixedLengthFile source = createTestFile();
        
        byte[] expected = "HOGEHOGEHOGE".getBytes(); // HogeDataConvertorによりHOGEに置換される
        
        // ボディの比較を行う
        MessagePool pool = new MessagePool(source, new HashMap<String, String>());
        MessagePool.Comparator comparator = pool.prepareForCompare();
        ReceivedMessage responseMessage = new ReceivedMessage(expected);
        comparator.compareBody("msgOnFail", responseMessage);
        
    }
    
    @Before
    public void initSystemRepositoryForTest() {
        // テスト用のリポジトリ構築
        File diConfigFile = Hereis.file("tmp/MessagePoolTest.xml");
        /*****
        <?xml version="1.0" encoding="UTF-8"?>
        <component-configuration
            xmlns="http://tis.co.jp/nablarch/component-configuration"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://tis.co.jp/nablarch/component-configuration  ./../component-configuration.xsd">
        
            <!-- データを一律HOGEに置き換えるテスト用コンバータ -->
            <component name="TestDataConverter_Hoge" 
                       class="nablarch.test.core.messaging.MessagePoolTest$HogeDataConvertor"/>
                                            
        </component-configuration>
         */
        diConfigFile.deleteOnExit();
        RepositoryInitializer.reInitializeRepository(diConfigFile.toURI().toString());
    }
    
    /**
     * 実行後にリポジトリを復元する。
     */
    @AfterClass
    public static void tearDownClass() {
        RepositoryInitializer.revertDefaultRepository();
    }

    private FixedLengthFile createTestFile() {
        FixedLengthFile file = new FixedLengthFile("dummyPath") {
            @Override
            public List<DataRecord> toDataRecords() {
                DataRecord record = new DataRecord();
                record.put("key1", "value1");
                record.put("key2", "value2");
                record.put("key3", "value3");
                List<DataRecord> dataRecords = new ArrayList<DataRecord>();
                dataRecords.add(record);
                return dataRecords;
            }
            
            @Override
            public LayoutDefinition createLayout() {
                RecordDefinition rd = new RecordDefinition();
                rd.setTypeName("test");
                rd.addField(new FieldDefinition().setName("key1").addConvertorSetting("X", new Object[]{6}).setPosition(1));
                rd.addField(new FieldDefinition().setName("key2").addConvertorSetting("X", new Object[]{6}).setPosition(7));
                rd.addField(new FieldDefinition().setName("key3").addConvertorSetting("X", new Object[]{6}).setPosition(13));
                
                LayoutDefinition ld = new LayoutDefinition();
                ld.getDirective().put("file-type", "Fixed");
                ld.getDirective().put("text-encoding", "UTF-8");
                ld.getDirective().put("record-length", 18);
                ld.addRecord(rd);
                
                return ld;
            }
        };
        
        file.setDirective("file-type", "Hoge");
        
        return file;
    }
    
    public static class DummyHeaderFormatter implements DataRecordFormatter {
        /** 出力ストリーム */
        private OutputStream dest;

        @Override
        public DataRecord readRecord() throws IOException,
                InvalidDataFormatException {
            return new DataRecord();
        }

        @Override
        public void writeRecord(Map<String, ?> record) throws IOException,
                InvalidDataFormatException {
            writeRecord(null, record);
        }

        @Override
        public void writeRecord(String recordType, Map<String, ?> record)
                throws IOException, InvalidDataFormatException {
            for(Entry<String, ?> entrySet: record.entrySet()){
                dest.write(entrySet.getKey().getBytes());
                String value = (String)entrySet.getValue();
                dest.write(value.getBytes());
            }
        }

        @Override
        public DataRecordFormatter initialize() {
            return this;
        }

        @Override
        public DataRecordFormatter setInputStream(InputStream stream) {
            return this;
        }

        @Override
        public void close() {
            if (dest != null) {
                try {
                    dest.close();
                    dest = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public DataRecordFormatter setDefinition(LayoutDefinition definition) {
            return this;
        }

        @Override
        public DataRecordFormatter setOutputStream(OutputStream stream) {
            dest = stream;
            return this;
        }

        @Override
        public boolean hasNext() throws IOException {
            return false;
        }

        @Override
        public int getRecordNumber() {
            return 0;
        }
        
    }
    
    public static class HogeDataConvertor implements TestDataConverter {
        @Override
        public DataRecord convertData(LayoutDefinition definition,
                DataRecord currentData, Charset encoding) {
            for (String key : currentData.keySet()) {
                currentData.put(key, "HOGE");
            }
            return currentData;
        }
        
        @Override
        public LayoutDefinition createDefinition(
                LayoutDefinition defaultDefinition, DataRecord currentData,
                Charset encoding) {
            
            RecordDefinition rd = new RecordDefinition();
            rd.setTypeName("test");
            RecordDefinition orgRd = defaultDefinition.getRecordType("test");
            int pos = 1;
            for (FieldDefinition orgFd : orgRd.getFields()) {
                String data = currentData.getString(orgFd.getName());
                int len = data.length();
                rd.addField(new FieldDefinition().setName(orgFd.getName()).addConvertorSetting("X", new Object[]{len}).setPosition(pos));
                pos += len;
            }
            
            LayoutDefinition ld = new LayoutDefinition();
            ld.getDirective().putAll(defaultDefinition.getDirective());
            ld.getDirective().put("record-length", pos - 1);
            ld.addRecord(rd);
            
            return ld;
        }
    }

}
