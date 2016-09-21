package nablarch.test.core.reader;

import nablarch.test.core.util.ListWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static nablarch.test.NablarchTestUtils.*;

/** ヘッダ行を表すクラス。 */
class HeaderLine {

    /** キー一覧 */
    private final List<String> keys;

    /** マーカーカラムの位置一覧 */
    private final Set<Integer> markerIndices = new HashSet<Integer>();

    /** 有効なカラム名一覧(マーカーカラムを除いたカラム名一覧) */
    private final List<String> effectiveColumnNames;

    /**
     * コンストラクタ。
     *
     * @param headerLine ヘッダ行
     */
    HeaderLine(List<String> headerLine) {
        List<String> keys = trimTailCopy(headerLine);   // キャッシュを破壊しないようにコピーして編集
        if (keys == null) {
            this.keys = Collections.emptyList();
        } else {
            this.keys = keys;
        }
        ListWrapper<String> wrapper = ListWrapper.wrap(this.keys);
        markerIndices.addAll(wrapper.indicesOf(MARKER_COLUMN_CONDITION));
        effectiveColumnNames = wrapper.exclude(MARKER_COLUMN_CONDITION);
    }

    /**
     * 有効なカラム名一覧を取得する。
     *
     * @return 有効なカラム名一覧
     */
    String[] getEffectiveColumnNames() {
        return effectiveColumnNames.toArray(new String[effectiveColumnNames.size()]);
    }

    /**
     * マーカーカラムを除外したマップを取得する。
     *
     * @param line 行データ
     * @return Map(キーはヘッダ行から取得される)
     */
    Map<String, String> getMapExcludingMarkerColumns(List<String> line) {
        // 目視による比較がしやすいのでTreeMapを使用（キーでソートされる）
        Map<String, String> result = new TreeMap<String, String>();
        List<String> values = excludeMarkerColumns(line);
        for (int i = 0; i < values.size(); i++) {
            result.put(effectiveColumnNames.get(i), values.get(i));
        }
        return result;
    }

    /**
     * マーカーカラムを除外したリストを取得する。
     *
     * @param line 行データ
     * @return マーカーカラムに該当する要素が除外されたリスト
     */
    List<String> excludeMarkerColumns(List<String> line) {
        List<String> notMarked = new ArrayList<String>();
        for (int i = 0; i < keys.size(); i++) {
            if (markerIndices.contains(i)) {
                continue;  // マーカーカラムならスキップ
            }
            String val = (i >= line.size()) ? "" : line.get(i);
            notMarked.add(val);
        }
        return notMarked;
    }

    /** マーカーカラムの条件 */
    private static final ListWrapper.Condition<String> MARKER_COLUMN_CONDITION
            = new ListWrapper.Condition<String>() {
        /** {@inheritDoc} */
        public boolean evaluate(String element) {
            return element != null
                    && element.startsWith("[")
                    && element.endsWith("]");
        }
    };
}
