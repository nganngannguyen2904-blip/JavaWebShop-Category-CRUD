<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><sitemesh:write property="title"/></title>

    <!-- Google Font Be Vietnam Pro -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@300;400;500;600;700&display=swap" rel="stylesheet">

    <!-- Bootstrap 5.3.3 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome 6.5.1 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    <style>
        body {
            font-family: 'Be Vietnam Pro', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background-color: #f1f5f9;
            color: #334155;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
        }
        .admin-wrapper {
            flex: 1 0 auto;
        }
    </style>

    <sitemesh:write property="head"/>
</head>
<body>

    <!-- Header quản trị -->
    <%@ include file="/common/admin/header.jsp" %>

    <div class="container-fluid admin-wrapper p-0">
        <div class="row g-0">
            <!-- Sidebar Trái -->
            <div class="col-md-3 col-lg-2">
                <%@ include file="/common/admin/left.jsp" %>
            </div>
            <!-- Nội dung chính -->
            <div class="col-md-9 col-lg-10 p-4">
                <sitemesh:write property="body"/>
            </div>
        </div>
    </div>

    <!-- Footer quản trị -->
    <c:import url="/common/admin/footer.jsp" />

    <!-- Bootstrap 5.3.3 Bundle JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
