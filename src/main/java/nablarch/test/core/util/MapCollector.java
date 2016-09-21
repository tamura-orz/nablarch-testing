package nablarch.test.core.util;


import java.util.HashMap;
import java.util.Map;

/**
 * マップの置き換えを簡易的に行うクラス。<br/>
 *
 * @param <NEW> 置き換え後の型
 * @param <K>   キーの型
 * @param <V>   値の型
 * @author T.Kawasaki
 */
public abstract class MapCollector<NEW, K, V> {

    /** 変換をスキップしたか否か */
    private boolean skipped = false;

    /** 変換結果 */
    private final Map<K, NEW> result;

    /** デフォルトコンストラクタ。 */
    public MapCollector() {
        result = new HashMap<K, NEW>();
    }

    /**
     * コンストラクタ。
     *
     * @param result 結果を格納するMap
     */
    public MapCollector(Map<K, NEW> result) {
        this.result = result;
    }

    /**
     * 変換する。
     *
     * @param orig 元のMap
     * @return 変換後のMap
     */
    public final Map<K, NEW> collect(Map<K, V> orig) {
        for (java.util.Map.Entry<K, V> entry : orig.entrySet()) {
            K key = entry.getKey();
            V originalValue = entry.getValue();
            skipped = false;  // 初期化
            NEW newValue = evaluate(key, originalValue);
            if (!skipped) {
                result.put(key, newValue);
            }
        }
        return result;
    }

    /**
     * 評価する。
     *
     * @param key   元のキー
     * @param value 元の値
     * @return 評価後の値
     */
    protected abstract NEW evaluate(K key, V value);

    /**
     * 評価をスキップする。<br/>
     * スキップされた場合、そのキーは結果には含まれない。
     *
     * @return <code>null</code> （使用されない）
     */
    protected final NEW skip() {
        skipped = true;
        return null;
    }
}
