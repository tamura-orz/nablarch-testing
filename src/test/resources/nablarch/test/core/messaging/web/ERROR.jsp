<?xml version="1.0" encoding="UTF-8" ?>
<%@ taglib prefix="n" uri="http://tis.co.jp/nablarch" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"    pageEncoding="UTF-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <jsp:include page="/html_header.jsp">
        <jsp:param name="title" value="システムエラー" />
    </jsp:include>
</head>

<body>
    <div id="wrapper">
    <jsp:include page="/app_header.jsp">
        <jsp:param name="title" value=""/>
    </jsp:include>
    <div id="mainContents">
        <div id="mainContentsInner">
            <div class="systemError">システムエラーが発生しました。<br/>管理者へ連絡してください。</div>
            <div style="padding: 20px;">問い合わせ番号: <n:write name="trackingNumber" /></div>
        </div>
    </div>
    </div>
</body>
</html>
