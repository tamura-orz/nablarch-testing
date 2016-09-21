<%-- コメント --%>
<%!
  /** HOGE */
  private static final String HOGE = "HOGE";
  /** FUGA */
  private static final String FUGA = "FUGA";
%>

<%
  int total = 0;
  for (int i = 0; i < 100; i++) {
    total += i;
  }
  String s1 = "-->";
  String s2 = "あいうえお";
%>
<p><%= total %></p>
<ul>
  <%-- suppress jsp check:この次の行はチェック対象外 --%>
  <li><%= HOGE %>
  </li>
  <%-- suppress jsp check:閉じタグが次の行にあるので、この行も次の行もチェック対象
  --%>
  <li><%--
  suppress jsp check:開始タグと同じ行にないのでチェック対象となる
      --%>
  </li>
  <li>
    <%-- hoge suppress jsp check:suppressの前に余計な文字が入っているのでチェック対象となる --%>
  </li>
</ul>
