<%--
  Created by IntelliJ IDEA.
  User: beomju
  Date: 2020/03/02
  Time: 9:56 오후
  To change this template use File | Settings | File Templates.
--%>
<%@page import="spms.vo.Member"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<jsp:useBean id="member" scope="session" class="spms.vo.Member"/>
<%
//    Member member = (Member)session.getAttribute("members");
%>
<div style="background-color:#00008b;color:#ffffff;height:20px;padding: 5px;">
SPMS(Simple Project Management System)
<% if (member.getEmail() != null) { %>
<span style="float:right;">
<%=member.getName()%>
<a style="color:white;"
   href="<%=request.getContextPath()%>/auth/logout">로그아웃</a>
</span>
<% } %>
</div>