<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ page import="bbs.Message" %>
<%!

/**
 * HTMLで意味をもつ文字をエスケープ
 *
 * @param src
 * @return
 */
private String escapeHtml(String src) {
    return src.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"",  "&quot;")
            .replace("'",  "&#39;");
}
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>テスト掲示板</title>
</head>
<body>

<h1>テスト掲示板</h1>
<form action="/testbbs_jsp/PostBbs" method="POST">
タイトル：<input type="text" name="title" size="60"/><br/>
ハンドル名：<input type="text" name="handle"/><br/>
<textarea name="message" rows="4" cols="60"></textarea><br/>
<input type="submit"/>
</form>
<hr/>
<%
for (Message message : Message.messageList) {
%>
<p>『<%= escapeHtml(message.title) %>』&nbsp;&nbsp;
<%= escapeHtml(message.handle) %> さん&nbsp;&nbsp;
<%= escapeHtml(message.date.toString()) %></p>
<p>
<%= escapeHtml(message.message).replace("\r\n", "<br/>") %>
</p><hr/>
<%
}
%>

</body>
</html>
