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
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div style="background-color:#00008b;color:#ffffff;height:20px;padding: 5px;">
    SPMS(Simple Project Management System)
    <c:if test="${!empty sessionScope.member and
              !empty sessionScope.member.email}">
<span style="float:right;">
${sessionScope.member.name}
<a style="color:white;"
   href="<%=request.getContextPath()%>/auth/logout.do">로그아웃</a>
</span>
    </c:if>
</div>