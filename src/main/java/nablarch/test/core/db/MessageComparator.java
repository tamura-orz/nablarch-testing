package nablarch.test.core.db;

import nablarch.core.message.Message;
import nablarch.core.validation.ValidationResultMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link Message}の順序比較を行うクラス。<br/>
 *
 * @author T.Kawasaki
 */
class MessageComparator implements Comparator<Message>, Serializable {

    /**
     * 順序付けのために2つの引数を比較する。<br/>
     * 以下のロジックで比較を行う。
     * <ul>
     * <li>
     * 片方がプロパティを持っておりもう片方がプロパティを持っていないグローバルなメッセージの場合、
     * プロパティを持っている方をより小さいと判断する。
     * </li>
     * <li>
     * 双方がそれぞれ異なるプロパティを持っている場合は、双方のプロパティ名を文字列として比較した結果を返却する。
     * </li>
     * <li>
     * 双方が同じプロパティ名を持っている場合、またはどちらもプロパティを持っていない場合、
     * メッセージIDを文字列として比較した結果を返却する。
     * </li>
     * </ul>
     *
     * @param one     比較対象の最初のオブジェクト
     * @param another 比較対象の 2 番目のオブジェクト
     * @return 比較結果
     */
    public int compare(Message one, Message another) {

        boolean b1 = one instanceof ValidationResultMessage;
        boolean b2 = another instanceof ValidationResultMessage;

        if (b1 && !b2) {   // ValidationResultMessageとMessage
            return -1;
        }

        if (!b1 && b2) { // MessageとValidationResultMessage
            return 1;
        }

        if (b1 && b2) {   // 両方ともValidationResultMessage
            // プロパティ名で比較
            ValidationResultMessage v1 = (ValidationResultMessage) one;
            ValidationResultMessage v2 = (ValidationResultMessage) another;
            int idDiff = v1.getPropertyName().compareTo(v2.getPropertyName());
            if (idDiff != 0) {
                return idDiff;   // プロパティ名が異なる場合
            }
        }

        // メッセージIDの差を返却する。
        String id1 = String.valueOf(one.getMessageId());
        String id2 = String.valueOf(another.getMessageId());
        return id1.compareTo(id2);
    }

    /**
     * 本クラスの比較ロジックに基づいてソートを行う（非破壊メソッド）。<br/>
     *
     * @param orig ソート対象となるメッセージ
     * @return ソート後のメッセージ
     */
    static List<Message> sort(List<Message> orig) {
        if (orig == null || orig.isEmpty()) {
            return orig;
        }
        List<Message> sorted = new ArrayList<Message>(orig);
        Collections.sort(sorted, new MessageComparator());
        return sorted;
    }
}
