<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, java.util.List, model.Post" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    User u = (User) session.getAttribute("user");
    // This page can be viewed by anyone, but only logged-in users can post.
%>
<html>
<head>
    <title>Community Forum</title>
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
            border-bottom: 2px solid var(--medium-gray);
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        .header h1 {
            color: var(--brand-red);
            margin: 0;
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
        h2 {
            color: var(--brand-red);
        }

        /* ✅ NEW: Welcome Post Styles */
        .welcome-post {
            background: #fff;
            border-left: 5px solid var(--brand-red);
            padding: 20px 25px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            margin-bottom: 30px;
        }
        .welcome-post h2 {
            margin-top: 0;
            color: var(--brand-red);
        }
        .welcome-post h4 {
            color: #333;
            border-bottom: 1px solid #eee;
            padding-bottom: 5px;
            margin-top: 25px;
        }
        .welcome-post ul {
            padding-left: 20px;
            list-style: none;
        }
        .welcome-post li {
            margin-bottom: 10px;
            line-height: 1.5;
            padding-left: 10px;
        }
        .welcome-post p {
            line-height: 1.6;
        }
        .welcome-post .admin-team {
            font-style: italic;
            color: var(--dark-gray);
        }

        /* New Post Form Styles */
        .new-post-form {
            background: #fff;
            padding: 25px 30px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            margin-bottom: 30px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            font-weight: 600;
            margin-bottom: 5px;
            color: #555;
        }
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-family: 'Poppins', sans-serif;
            font-size: 1rem;
            transition: border-color 0.2s, box-shadow 0.2s;
        }
        .form-group input:focus, .form-group textarea:focus {
            outline: none;
            border-color: var(--brand-blue);
            box-shadow: 0 0 5px rgba(0,123,255,0.25);
        }
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
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
            transition: background-color 0.2s;
        }
        button:hover {
            background-color: var(--brand-hover);
        }
        
        .login-prompt {
            text-align: center;
            padding: 20px;
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            margin-bottom: 30px;
        }
        .login-prompt a {
            font-weight: 600;
            color: var(--brand-blue);
        }

        /* Post List Styles */
        .post-list {
            list-style: none;
            padding: 0;
        }
        .post-item {
            background: #fff;
            margin-bottom: 15px;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
            transition: box-shadow 0.2s ease, transform 0.2s ease;
            position: relative; 
        }
        .post-item:hover {
            transform: translateY(-3px);
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        .post-item a.post-link {
            text-decoration: none;
            color: var(--text-color);
            display: block;
            padding: 20px 25px;
        }
        .post-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: var(--brand-red);
            margin: 0 0 10px 0;
        }
        .post-meta {
            font-size: 0.9rem;
            color: var(--dark-gray);
        }
        
        .admin-delete {
            position: absolute;
            top: 15px;
            right: 15px;
            background-color: var(--brand-red);
            color: white;
            text-decoration: none;
            padding: 5px 10px;
            font-size: 0.8rem;
            font-weight: 600;
            border-radius: 5px;
            opacity: 0.7;
            transition: all 0.2s;
        }
        .admin-delete:hover {
            opacity: 1;
            background-color: #a71d2a;
        }

    </style>
</head>
<body>
    <div class="container">
        
        <div class="header">
            <h1>Community Forum</h1>
            <c:if test="${not empty user}">
                <a href="${pageContext.request.contextPath}/logout">Logout</a>
            </c:if>
        </div>

        <%-- ✅ NEW: Default Welcome & Rules Post --%>
        <div class="welcome-post">
            <h2>Welcome to the PLASMIC Community Forum! 🤞👐</h2>
            <p>We're thrilled to launch this forum as a place for our incredible donors, patients, and hospital partners to connect, share stories, and support one another. This is your space to ask questions, share your donation experiences, and be a part of the life-saving journey.</p>
            <p>To ensure this remains a safe, positive, and helpful environment for everyone, we have a few rules. By posting here, you agree to the following:</p>
            
            <h4>🛑Forum Rules & Regulations</h4>
            <ul>
                <li><strong>Be Respectful🫂:</strong> We have a zero-tolerance policy for harassment, personal attacks, profanity, or any speech that is hateful or discriminatory. Please treat everyone with kindness.</li>
                <li><strong>Protect Privacy👥:</strong> This is a public forum. For your own safety, **DO NOT** post personal, private, or confidential information. This includes full names, phone numbers, email addresses, or specific, private medical details.</li>
                <li><strong>NO Medical Advice:</strong> This is a *support* community, not a substitute for professional medical care. You may **not** ask for or give medical advice. Please always consult a qualified healthcare professional for any medical concerns.</li>
                <li><strong>Stay On-Topic:</strong> Please keep discussions related to blood donation, health, patient support, and community events (like blood drives).</li>
                <li><strong>No Spam or Advertising📰:</strong> This forum may not be used to promote commercial products or services.</li>
                <li><strong>Moderation:🧑‍💻</strong> Admins reserve the right to remove any post or comment, or to ban users who violate these rules, to keep the community safe.</li>
            </ul>
            
            <p>We are so excited to see this community grow. Thank you for being a part of the PLASMIC family!❤️🩸</p>
            <p class="admin-team">— The PLASMIC Admin Team</p>
        </div>

        <%-- This is where the new post form and post list will appear --%>
        <c:if test="${not empty user}">
            <div class="new-post-form">
                <h2>Create a New Post</h2>
                <form action="${pageContext.request.contextPath}/add-post" method="post">
                    <div class="form-group">
                        <label for="postTitle">Title:</label>
                        <input type="text" id="postTitle" name="postTitle" required>
                    </div>
                    <div class="form-group">
                        <label for="postContent">Message:</label>
                        <textarea id="postContent" name="postContent" required></textarea>
                    </div>
                    <button type="submit">Submit Post</button>
                </form>
            </div>
        </c:if>
        <c:if test="${empty user}">
            <div class="login-prompt">
                <p><a href="login.jsp?redirectURL=${pageContext.request.contextPath}/community">Log in</a> to create a post or join the conversation.</p>
            </div>
        </c:if>
        
        <h2>All Posts</h2>
        <ul class="post-list">
            <c:if test="${empty postList}">
                <li><p>No posts yet. Be the first!</p></li>
            </c:if>
            
            <c:forEach var="post" items="${postList}">
                <li class="post-item">
                    <%-- This link is for regular users --%>
                    <a href="${pageContext.request.contextPath}/post?id=${post.postId}" class="post-link">
                        <h3 class="post-title">${post.postTitle}</h3>
                        <div class="post-meta">
                            Posted by <strong>${post.username}</strong> on ${post.postTimestamp}
                        </div>
                    </a>
                    
                    <%-- Admin-only delete button --%>
                    <c:if test="${not empty user && user.role == 'ADMIN'}">
                        <a href="${pageContext.request.contextPath}/delete-post?postId=${post.postId}"
                           class="admin-delete"
                           onclick="return confirm('Are you sure you want to delete this post and all its comments?');">
                           Delete Post
                        </a>
                    </c:if>
                </li>
            </c:forEach>
        </ul>
    </div>
</body>
</html>