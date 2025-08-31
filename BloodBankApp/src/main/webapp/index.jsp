<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome to PLASMIC</title>
    <style>
        /* This style is for the preloader */
        #preloader {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: #000000; /* Black background */
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        }
        .loader-video {
            width: 100%;
            max-width: 400px;
            height: auto;
        }

        /* This is the style for your main page content */
        .main-content {
            font-family: sans-serif;
            text-align: center;
            padding-top: 50px;
            display: none; /* Initially hidden */
        }
        h1 { color: #c9302c; }
        .nav { margin-top: 30px; }
        .nav a { margin: 0 15px; text-decoration: none; padding: 12px 25px; border-radius: 5px; color: white; font-size: 16px; font-weight: bold; }
        .user-login { background-color: #007bff; }
        .hospital-login { background-color: #28a745; }
    </style>
</head>
<body>

    <div id="preloader">
        <video class="loader-video" autoplay muted loop playsinline>
            <%-- ✅ FIXED: Using the most reliable path for the video source --%>
            <source src="${pageContext.request.contextPath}/images/loader.mp4" type="video/mp4">
        </video>
    </div>

    <div id="main-content" class="main-content">
        <h1>Welcome to PLASMIC</h1>
        <p>Your integrated blood bank management solution.</p>
        <div class="nav">
            <a href="login.jsp" class="user-login">User Portal</a>
            <a href="hospital-login.jsp" class="hospital-login">Hospital Portal</a>
        </div>
    </div>

    <script>
        window.addEventListener('load', function() {
            var preloader = document.getElementById('preloader');
            var mainContent = document.getElementById('main-content');

            setTimeout(function() {
                preloader.style.display = 'none';
                mainContent.style.display = 'block';
            }, 7000); // 7 seconds
        });
    </script>

</body>
</html>