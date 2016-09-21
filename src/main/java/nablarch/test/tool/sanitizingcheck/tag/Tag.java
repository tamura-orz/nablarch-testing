package nablarch.test.tool.sanitizingcheck.tag;

import java.util.ArrayList;
import java.util.List;

/**
 * タグをあらわすクラス。
 *
 * @author hisaaki sioiri
 */
public abstract class Tag {

    /** タグ名 */
    private final String name;

    /** タグのポジション */
    private int position;

    /** 属性 */
    private final List<TagAttribute> attributes = new ArrayList<TagAttribute>();

    /** 行番号 */
    private final int lineNo;

    /** タグが閉じられているか否か */
    private boolean closed;

    /** 閉じタグの開始位置 */
    private int closeTagPosition;

    /** 属性情報 */
    private TagAttribute attribute = new TagAttribute();

    /** 属性情報の一時領域 */
    private StringBuilder attributeTemp =  new StringBuilder();

    /** チェックが無効化されているタグが否か */
    private boolean suppressJspCheck = false;

    /**
     * コンストラクタ。
     *
     * @param name タグ名
     * @param lineNo 行番号
     * @param position タグの位置
     */
    protected Tag(String name, int lineNo, int position) {
        this.name = name;
        this.lineNo = lineNo;
        this.position = position;
    }

    /**
     * タグのタイプを取得する。
     *
     * @return タグのタイプ
     */
    public abstract TagType getType();

    /**
     * タグの内容を解析する。
     * <p/>
     * 閉じタグまでの内容を解析し、タグの属性値を保持する。
     *
     * @param lineNo 行番号
     * @param line 解析対象の行
     * @param searchPosition 解析開始位置
     */
    public void parse(int lineNo, String line, int searchPosition) {
        int currentPosition = searchPosition;
        while (currentPosition < line.length()) {
            char c = line.charAt(currentPosition++);
            if (attribute.getName() == null && c == '>') {
                // 属性が存在していない状態で閉じタグが来た場合は、タグが閉じられていると判断する。
                setCloseTagPosition(currentPosition);
                break;
            } else if (attribute.getName() == null) {
                // 属性名の解析
                if (c == '=') {
                    // 「=」は、属性名と属性値の区切り文字なので、一時領域の属性名を確定する。
                    attribute.setName(attributeTemp.toString().trim());
                    attributeTemp.setLength(0);
                } else {
                    if (!Character.isWhitespace(c)) {
                        if (attributeTemp.length() != 0
                                && Character.isWhitespace(attributeTemp.charAt(attributeTemp.length() - 1))) {
                            // 非スペース文字が来た場合で、一つ前の文字がスペースの場合は、
                            // キャッシュしていた属性名をクリアする。
                            attributeTemp.setLength(0);
                        }
                    }
                    attributeTemp.append(c);
                }
            } else if (attribute.getName() != null) {
                // 属性値の解析
                if (c == '\'' || c == '"') {
                    // 開始文字または、終了文字が来た場合
                    attributeTemp.append(c);
                    if (attributeTemp.length() == 1) {
                        // 属性値が開始された場合
                        attribute.setLineNo(lineNo);
                        attribute.setPosition(currentPosition);
                    } else {
                        // 属性値が終了した場合
                        attribute.setValue(attributeTemp.toString());
                        addAttribute(attribute);
                        attribute = new TagAttribute();
                        attributeTemp.setLength(0);
                    }
                } else {
                    if (attribute.getPosition() != 0) {
                        attributeTemp.append(c);
                    }
                    if (c == '\\') {
                        // エスケープ文字の場合は、次の文字も読み込む
                        if (currentPosition < line.length()) {
                            attributeTemp.append(line.charAt(currentPosition++));
                        }
                    }
                }
            }
        }
    }

    /**
     * タグ名を取得する。
     *
     * @return タグ名
     */
    public String getName() {
        return name;
    }

    /**
     * タグの開始位置を取得する。
     *
     * @return タグの開始位置
     */
    public int getPosition() {
        return position;
    }

    /**
     * タグの開始位置を設定する。
     * @param position 開始位置
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 閉じタグの位置を取得する。
     *
     * @return 閉じタグの位置
     */
    public int getCloseTagPosition() {
        return closeTagPosition;
    }

    /**
     * 閉じタグの位置を設定する。
     *
     * @param closeTagPosition 閉じタグの位置
     */
    public void setCloseTagPosition(int closeTagPosition) {
        this.closeTagPosition = closeTagPosition;
        closed = true;
    }

    /**
     * タグが閉じられているか否か。
     *
     * @return trueの場合は閉じられている。
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * 属性を追加する。
     *
     * @param attribute 属性
     */
    public void addAttribute(TagAttribute attribute) {
        attributes.add(attribute);
    }

    /**
     * 属性情報を取得する。
     *
     * @return 属性情報
     */
    public List<TagAttribute> getAttributes() {
        return attributes;
    }

    /**
     * 行番号を取得する。
     *
     * @return 行番号
     */
    public int getLineNo() {
        return lineNo;
    }

    /**
     * メッセージを取得する。
     * デフォルト実装では、タグ名を返却する。
     *
     * @return メッセージ
     */
    protected String getMessage() {
        return name;
    }

    @Override
    public String toString() {
        return getMessage() + " (at line=" + lineNo
                + " column=" + position
                + ')';
    }

    /**
     * チェックが無効化されているタグか否か
     * @return チェックが無効化されている場合true
     */
    public boolean isSuppressJspCheck() {
        return suppressJspCheck;
    }

    /**
     * チェックが無効化されているタグか否かを設定する。
     * @param suppressJspCheck チェックが無効化されている場合はtrue
     */
    public void setSuppressJspCheck(boolean suppressJspCheck) {
        this.suppressJspCheck = suppressJspCheck;
    }

    /**
     * タグ属性を表すクラス。
     *
     * @author hisaaki sioiri
     * @version 1.2
     */
    public static class TagAttribute {

        /** 属性値の行番号 */
        private int lineNo;

        /** 開始位置 */
        private int position;

        /** 属性名 */
        private String name;

        /** 属性値 */
        private String value;

        /**
         * コンストラクタ。
         */
        public TagAttribute() {
        }

        /**
         * コンストラクタ。
         *
         * @param lineNo 属性値の行番号
         * @param position 属性値の開始位置
         * @param name 属性名
         * @param value 属性値
         */
        public TagAttribute(int lineNo, int position, String name, String value) {
            this.lineNo = lineNo;
            this.position = position;
            this.name = name;
            this.value = value;
        }

        /**
         * 属性値の行番号を取得する。
         * @return 行番号
         */
        public int getLineNo() {
            return lineNo;
        }

        /**
         * 属性値の行番号を設定する。
         * @param lineNo 行番号
         */
        public void setLineNo(int lineNo) {
            this.lineNo = lineNo;
        }

        /**
         * 開始位置を取得する。
         * @return 開始位置
         */
        public int getPosition() {
            return position;
        }

        /**
         * 属性値の開始位置を設定する。
         * @param position 開始位置
         */
        public void setPosition(int position) {
            this.position = position;
        }

        /**
         * 属性値を取得する。
         *
         * @return 属性値
         */
        public String getValue() {
            return value;
        }

        /**
         * 属性値を設定する。
         * @param value 属性値
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * 属性名を取得する。
         *
         * @return 属性名
         */
        public String getName() {
            return name;
        }

        /**
         * 属性名を設定する。
         * @param name 属性名
         */
        public void setName(String name) {
            this.name = name;
        }
    }
}

