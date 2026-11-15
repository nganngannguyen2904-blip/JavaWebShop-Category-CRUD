<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Tự động chuyển hướng đến controller /waiting để phân quyền hoặc /login
    response.sendRedirect(request.getContextPath() + "/waiting");
%>
