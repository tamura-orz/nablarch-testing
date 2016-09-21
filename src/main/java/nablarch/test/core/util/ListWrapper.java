package nablarch.test.core.util;


import java.util.ArrayList;
import java.util.List;

import static nablarch.core.util.Builder.concat;

/**
 * {@link java.util.List}をラップするクラス。<br/>
 * {@link java.util.List}に対する汎用的なオペレーションを提供する。
 *
 * @param <T> ラップされるリストの総称型
 * @author T.Kawasaki
 */
public class ListWrapper<T> {

    /**
     * インスタンス化メソッド。<br/>
     * 総称型を明示的に書かなくて良いための構文糖衣である。
     * 引数のリストの総称型から本クラスのインスタンスの総称型が決定される。
     *
     * @param list ラップされるリスト
     * @param <T>  ラップされるリストの総称型
     * @return 本クラスのインスタンス
     */
    public static <T> ListWrapper<T> wrap(List<T> list) {
        return new ListWrapper<T>(list);
    }

    /** 検索対象が見つからなかった場合のインデックス */
    public static final int NOT_FOUND = -1;

    /** ラップされるリスト */
    private final List<T> list;

    /**
     * コンストラクタ。
     *
     * @param list ラップされるリスト
     */
    ListWrapper(List<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("argument must not be null.");
        }
        this.list = list;
    }

    /**
     * 指定したクラスに合致する先頭要素を取り出す。
     *
     * @param <E>    検索対象の型
     * @param target 取り出す対象となるクラス
     * @return 指定されたクラスに合致した要素（見つからない場合はnull）
     */
    @SuppressWarnings("unchecked")
    public <E extends T> E select(Class<E> target) {
        int idx = indexOf(target);
        return (idx == NOT_FOUND)
                ? null
                : (E) list.get(idx);
    }

    /**
     * 指定したクラスに合致する先頭要素のインデックスを返却する。
     *
     * @param target 指定クラス
     * @return 指定したクラスに合致する先頭要素のインデックス（見つからない場合は{@link ListWrapper#NOT_FOUND}）
     */
    public int indexOf(Class<? extends T> target) {
        for (int i = 0; i < list.size(); i++) {
            T e = list.get(i);
            if (e != null && target.isAssignableFrom(e.getClass())) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    /**
     * 指定したクラスに合致する先頭要素のインデックスを返却する。<br/>
     * 必須指定がされており、かつ指定クラスに合致する要素がない場合は例外が発生する。
     *
     * @param target   指定クラス
     * @param required 必須か否か
     * @return 指定したクラスに合致する先頭要素のインデックス
     */
    public int indexOf(Class<? extends T> target, boolean required) {
        int idx = indexOf(target);
        if (required && idx == NOT_FOUND) {
            throw new IllegalArgumentException(concat(
                    "can't find ", target, " in [", list, "]"));
        }
        return idx;
    }

    /**
     * 条件に合致する要素のみ抽出したリストを返却する（非破壊的メソッド）。
     *
     * @param condition 抽出条件
     * @return 条件に合致する要素
     */
    public List<T> select(Condition<T> condition) {
        List<T> result = new ArrayList<T>(list.size());
        for (T e : list) {
            if (condition.evaluate(e)) {
                result.add(e);
            }
        }
        return result;
    }

    /**
     * 指定した条件に合致するインデックス一覧を返却する。<br/>
     *
     * @param condition  条件
     * @return 指定したクラスに合致するインデックス一覧
     */
    public List<Integer> indicesOf(Condition<T> condition) {
        List<Integer> indices = new ArrayList<Integer>();
        for (int index = 0; index < list.size(); index++) {
            T e = list.get(index);
            if (condition.evaluate(e)) {
                indices.add(index);
            }
        }
        return indices;
    }

    /**
     * 条件に合致する要素を除外したリストを返却する（非破壊的メソッド）。
     *
     * @param condition 除外条件
     * @return 条件に合致する要素を除外したリスト
     */
    public List<T> exclude(final Condition<T> condition) {
        return select(condition.reverse());  // 条件に合致しない要素を抽出する
    }

    /**
     * リストに対する挿入操作を用意する。
     *
     * @param insertedObject 挿入対象オブジェクト
     * @return {@link InsertOperation}
     */
    public InsertOperation insert(T insertedObject) {
        return new InsertOperation(insertedObject);
    }

    /**
     * リストに対する挿入操作を行うクラス。
     *
     * @author T.Kawasaki
     */
    public final class InsertOperation {

        /** 挿入されるオブジェクト */
        private final T inserted;

        /**
         * コンストラクタ。
         *
         * @param inserted 挿入されるオブジェクト
         */
        private InsertOperation(T inserted) {
            this.inserted = inserted;
        }

        /**
         * 指定クラスの直後に要素を挿入する。
         *
         * @param target 指定クラス
         */
        public void after(Class<? extends T> target) {
            list.add(indexOf(target, true) + 1, inserted);
        }

        /**
         * 指定クラスの直前に要素を挿入する。<br/>
         * 指定クラスが先頭の場合は、先頭に挿入される。
         *
         * @param target 指定クラス
         */
        public void before(Class<? extends T> target) {
            int targetIndex = indexOf(target, true);
            int insertAt = (targetIndex == 0)
                    ? 0
                    : targetIndex;
            list.add(insertAt, inserted);
        }
    }

    /**
     * 各種操作の条件を表すクラス。
     *
     * @author T.Kawasaki
     */
    public abstract static class Condition<T> {

        /**
         * 評価する。
         *
         * @param element リスト内の１要素
         * @return 評価結果
         */
        public abstract boolean evaluate(T element);

        /**
         * 評価を反転させる。
         *
         * @return 元の評価結果を反転した真偽値
         */
        public Condition<T> reverse() {
            final Condition<T> original = this;
            return new Condition<T>() {
                /** {@inheritDoc} */
                public boolean evaluate(T element) {
                    return !original.evaluate(element);
                }
            };
        }
    }
}

