package nablarch.test.core.file;

import nablarch.core.dataformat.DataRecordFormatterSupport.Directive;
import nablarch.core.dataformat.FixedLengthDataRecordFormatter.FixedLengthDirective;
import nablarch.core.dataformat.DataRecord;
import nablarch.core.dataformat.LayoutDefinition;
import nablarch.core.repository.SystemRepository;

/**
 * 固定長ファイルを表すクラス。<br/>
 *
 * @author T.Kawasaki
 */
public class FixedLengthFile extends DataFile {

    /** ディレクティブのデフォルト値取得用キー */
    private static final String DEFAULT_DIRECTIVES = "fixedLengthDirectives";

    /**
     * コンストラクタ。<br/>
     *
     * @param path ファイルパス
     */
    public FixedLengthFile(String path) {
        super(path);
        prepareDefaultDirectives(DEFAULT_DIRECTIVES);
    }

    /**
     * {@inheritDoc}
     * 固定長ファイルを表すファイルタイプが返却される。
     */
    @Override
    protected String getFileType() {
        return "Fixed";
    }

    /**
     * {@inheritDoc}
     * 固定長ファイルの断片が返却される。
     *
     * @see FixedLengthFileFragment
     */
    @Override
    protected DataFileFragment createNewFragment() {
        return new FixedLengthFileFragment(this);
    }

    /**
     * {@inheritDoc}
     * 固定長ファイルのディレクティブ定義から対応するディレクティブが返却される。
     *
     * @see FixedLengthDirective
     */
    @Override
    protected Directive valueOf(String directiveName) {
        return FixedLengthDirective.valueOf(directiveName);
    }

    /**
     * フォーマット定義を作成する。<br/>
     * 本インスタンスおよび所有する{@link DataFileFragment}のレイアウト情報を元に
     * フォーマット定義を生成する。
     *
     * @return フォーマット定義
     */
    public LayoutDefinition createLayout() {
        LayoutDefinition layout = super.createLayout();

        // レコード長
        int recordLength = getRecordLength();
        layout.getDirective().put("record-length", recordLength);

        return layout;
    }

    /**
     * 単一のレコード定義を持つフォーマット定義を生成する。<br/>
     * データ読み出し時は、レコードレイアウト毎に本メソッドを使用して単一のフォーマット定義を作成し、
     * 読み込み時にレイアウトを変更して読み出しを行う。
     * （レコードレイアウトを明示的に指定してファイルを読み込む機能がないため）
     *
     * @param fragment レコード定義を取り出すための断片
     * @return フォーマット定義
     */
    @Override
    public LayoutDefinition createLayout(DataFileFragment... fragment) {
        LayoutDefinition layout = super.createLayout(fragment);
        // レコード長
        layout.getDirective().put("record-length", getRecordLength());
        return layout;
    }

    /**
     * この固定長ファイルのレコード長を取得する。<br/>
     *
     * @return レコード長
     * @throws IllegalStateException 断片によってレコード長が異なる場合
     */
    private int getRecordLength() throws IllegalStateException {
        boolean first = true;
        int length = 0;
        for (DataFileFragment e : all) {
            int current = e.getRecordLength();
            if (first) {
                length = current;
                first = false;
            } else {
                if (length != current) {
                    // 長さがちがう
                    throw new IllegalStateException(
                            "record-length differs." + all);
                }
            }
        }
        return length;
    }
    
    /**
     * 現在処理中のテストデータに対応したレイアウト定義データを生成します。
     * 
     * @param defaultDefinition エクセルファイルに記述されたデータから生成されたデフォルトのレイアウト定義データ
     * @param currentData 現在処理中の{@link #convertData(DataRecord)} 呼出し後のテストデータ。
     * @return 現在処理中のテストデータに対応したレイアウト定義データ
     */
    public LayoutDefinition createDefinition(LayoutDefinition defaultDefinition, DataRecord currentData) {
        TestDataConverter converter = getTestDataConverter();
        if (converter == null) {
            return defaultDefinition;
        } else {
            return converter.createDefinition(defaultDefinition, currentData, getEncodingFromDirectives());
        }
    }
    
    /**
     * テストデータを変換します。
     * 
     * @param definition エクセルファイルに記述されたデータから生成されたデフォルトのレイアウト定義データ
     * @param currentData 現在処理中のエクセルファイルに記述されたテストデータ。
     * @return 任意の変換処理を行ったテストデータ
     */
    public DataRecord convertData(LayoutDefinition definition, DataRecord currentData) {
        TestDataConverter converter = getTestDataConverter();
        if (converter == null) {
            return currentData;
        } else {
            return converter.convertData(definition, currentData, getEncodingFromDirectives());
        }
    }
    
    /**
     * テストデータコンバータを取得します
     * @return テストデータコンバータ
     */
    private TestDataConverter getTestDataConverter() {
        String fileType = (String) directives.get("file-type");
        return SystemRepository.get("TestDataConverter_" + fileType);
    }
}
