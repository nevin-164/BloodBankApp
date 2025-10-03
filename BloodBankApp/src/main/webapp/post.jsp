<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    User u = (User) session.getAttribute("user");
%>
<html>
<head>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <title>${post.postTitle}</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
    <style>
        :root {
            --brand-red: #c9302c;
            --brand-blue: #007bff;
            --brand-hover: #0056b3;
            --light-gray: #f8f9fa;
            --medium-gray: #e9ecef;
            --dark-gray: #6c757d;
            --text-color: #333;
        }
        * { box-sizing: border-box; }
        body {
            font-family: 'Poppins', sans-serif;
            background-color: var(--light-gray);
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        .header a {
            text-decoration: none;
            color: var(--brand-red);
            font-weight: 600;
            padding: 8px 12px;
            border-radius: 5px;
            transition: background-color 0.2s;
        }
        .header a:hover {
            background-color: var(--medium-gray);
        }

        /* Main Post Styles */
        .post-container {
            background: #fff;
            padding: 25px 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
        }
        .post-title {
            font-size: 2.2rem;
            font-weight: 600;
            color: var(--brand-red);
            margin: 0 0 10px 0;
            line-height: 1.2;
        }
        .post-meta {
            font-size: 0.9rem;
            color: var(--dark-gray);
            border-bottom: 1px solid var(--medium-gray);
            padding-bottom: 15px;
            margin-bottom: 20px;
        }
        .post-meta strong {
            color: var(--text-color);
        }
        .post-content {
            font-size: 1.1rem;
            line-height: 1.7;
            color: var(--text-color);
        }
        
        h2 {
            color: var(--brand-red);
            border-bottom: 2px solid var(--medium-gray);
            padding-bottom: 10px;
            margin-top: 40px;
        }
        
        /* New Comment Form */
        .comment-form {
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-family: inherit;
            font-size: 1rem;
            resize: vertical;
            min-height: 80px;
            transition: border-color 0.2s, box-shadow 0.2s;
        }
        .form-group textarea:focus {
            outline: none;
            border-color: var(--brand-blue);
            box-shadow: 0 0 5px rgba(0,123,255,0.25);
        }
        button {
            background-color: var(--brand-blue);
            color: white;
            padding: 12px 15px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 600;
        }
        button:hover {
            background-color: var(--brand-hover);
        }
        
        /* Comment List Styles */
        .comment-list {
            margin-top: 30px;
            list-style: none;
            padding: 0;
        }
        .comment-item {
            background: #fff;
            padding: 15px 20px;
            border-radius: 8px;
            box-shadow: 0 2px 3px rgba(0,0,0,0.05);
            margin-bottom: 15px;
            position: relative; /* ✅ ADDED: To position delete button */
        }
        .comment-meta {
            font-size: 0.9rem;
            color: var(--dark-gray);
            margin-bottom: 10px;
        }
        .comment-meta strong {
            color: var(--brand-blue);
        }
        .comment-content {
            color: var(--text-color);
            line-height: 1.6;
        }
        
        /* ✅ NEW: Admin Delete Button for comments */
        .admin-delete-comment {
            position: absolute;
            top: 10px;
            right: 10px;
            background-color: #dc3545; /* Red */
            color: white;
            text-decoration: none;
            padding: 3px 8px;
            font-size: 0.75rem;
            font-weight: 600;
            border-radius: 5px;
            opacity: 0.7;
            transition: all 0.2s;
        }
        .admin-delete-comment:hover {
            opacity: 1;
        }
    </style>
</head>
<body>
<jsp:include page="common/notification.jsp" />
    <div class="container">
        
        <div class="header">
             <a href="${pageContext.request.contextPath}/community">&larr; Back to all posts</a>
            <c:if test="${not empty user}">
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </c:if>
        </div>

        <div class="post-container">
            <h1 class="post-title">${post.postTitle}</h1>
            <div class="post-meta">
                Posted by <strong>${post.username}</strong> on ${post.postTimestamp}
            </div>
            <div class="post-content">
                <p>${fn:replace(post.postContent, "
", "<br>")}</p>
            </div>
        </div>
        
        <h2>Comments</h2>
        
        <%-- Comment Form --%>
        <c:if test="${not empty user}">
            <div class="comment-form">
                <form action="${pageContext.request.contextPath}/add-comment" method="post">
                    <div class="form-group">
                        <textarea id="commentContent" name="commentContent" placeholder="Write a comment..." required></textarea>
                    </div>
                    <input type="hidden" name="postId" value="${post.postId}">
                    <button type="submit">Submit Comment</button>
                </form>
            </div>
        </c:if>
        <c:if test="${empty user}">
            <p><a href="login.jsp">Log in</a> to leave a comment.</p>
        </c:if>
        
        <%-- Comment List --%>
        <ul class="comment-list">
            <c:if test="${empty comments}">
                <li><p>No comments yet. Be the first!</p></li>
            </c:if>
            
            <c:forEach var="comment" items="${comments}">
                <li class="comment-item">
                    <div class="comment-meta">
                        <strong>${comment.username}</strong> said:
                    </div>
                    <div class="comment-content">
                        <p>${fn:replace(comment.commentContent, "
", "<br>")}</p>
                    </div>
                    
                    <%-- ✅ NEW: Admin-only delete button for comments --%>
                    <c:if test="${not empty user && user.role == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/delete-comment?commentId=${comment.commentId}&postId=${post.postId}"
                           class="admin-delete-comment"
                           onclick="return confirm('Are you sure you want to delete this comment?');">
                           X
                        </a>
                    </c:if>
                </li>
            </c:forEach>
        </ul>

    </div>
</body>
</html>