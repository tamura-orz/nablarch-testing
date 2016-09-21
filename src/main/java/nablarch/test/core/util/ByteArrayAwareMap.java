package nablarch.test.core.util;


import nablarch.core.util.BinaryUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.util.map.MapWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static nablarch.core.util.Builder.concat;
import static nablarch.core.util.Builder.join;

/**
 * バイト配列を認識する{@link Map}実装クラス。
 *
 * @param <K> キーの型
 * @param <V> 値の型
 * @author T.Kawasaki
 */
public class ByteArrayAwareMap<K, V> extends MapWrapper<K, V> {

    /** 元のマップ */
    private Map<K, V> orig;

    /**
     * コンストラクタ。
     *
     * @param orig 元のMap
     */
    public ByteArrayAwareMap(Map<K, V> orig) {
        this.orig = orig;
    }

    /** {@inheritDoc} */
    @Override
    public Map<K, V> getDelegateMap() {
        return orig;
    }

    /**
     * {@inheritDoc}
     * 要素中にバイト配列が含まれていた場合、等価であれば等しい要素であるとみなす。
     */
    @Override
    public boolean equals(Object o) {

        if (orig.equals(o)) {
            return true;
        }

        if (!(o instanceof Map)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        Map<K, V> t = (Map<K, V>) o;
        if (t.size() != size()) {
            return false;
        }

        for (Entry<K, V> e : entrySet()) {
            K key = e.getKey();
            V value = e.getValue();
            if (!(t.containsKey(key) && equals(value, t.get(key)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * ２つの値が等価であるか判定する。
     *
     * @param one     値１
     * @param another 値２
     * @return 判定結果
     */
    private boolean equals(V one, V another) {
        if (one == another) {
            return true;
        }
        if (one instanceof byte[]) {
            return another instanceof byte[] && Arrays.equals((byte[]) one, (byte[]) another);
        }
        return one.equals(another);
    }

    /**
     * {@inheritDoc}
     * 要素中にバイト配列が含まれていた場合、16進数文字列に変換して出力する。
     */
    @Override
    public String toString() {
        List<String> elements = new ArrayList<String>(size());
        for (Entry<K, V> e : entrySet()) {
            K key = e.getKey();
            V value = e.getValue();
            elements.add(concat(toString(key), "=", toString(value)));
        }
        return concat("{", join(elements, ", "), "}");
    }


    /**
     * 文字列に変換する。
     *
     * @param orig 値
     * @return 文字列
     */
    private String toString(Object orig) {
        if (orig == this) {
            return "(this Map)";
        }
        if (orig instanceof byte[]) {
            return BinaryUtil.convertToHexStringWithPrefix((byte[]) orig);
        }
        return orig == null ? "null" : StringUtil.toString(orig);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return orig.hashCode();
    }
}
