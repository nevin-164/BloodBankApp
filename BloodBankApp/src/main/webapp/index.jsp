<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome to PLASMIC</title>
    <style>
        body {
            font-family: sans-serif;
            text-align: center;
            margin: 0;
            overflow: hidden; /* Prevent scrollbars during the preloader */
        }

        /* --- Preloader Styles --- */
        #preloader {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 9999;
            /* ✅ ADDED: Smooth transition for the fade-out effect */
            transition: opacity 1s ease-in-out;
        }
        #preloader.fade-out {
            opacity: 0;
        }

        /* ✅ MODIFIED: Styles to make the video a full-screen background */
        .loader-video {
            position: absolute;
            top: 50%;
            left: 50%;
            min-width: 100%;
            min-height: 100%;
            width: auto;
            height: auto;
            transform: translateX(-50%) translateY(-50%);
            object-fit: cover; /* This is the key to making the video cover the screen */
        }

        /* --- Main Content Styles --- */
        .main-content {
            padding-top: 50px;
            /* Initially hidden with zero opacity */
            opacity: 0;
            /* ✅ ADDED: Smooth fade-in effect */
            transition: opacity 1s ease-in-out;
        }
        .main-content.visible {
            opacity: 1;
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

            // Set a timer for 7 seconds
            setTimeout(function() {
                // ✅ MODIFIED: Add the 'fade-out' class to start the transition
                preloader.classList.add('fade-out');
                
                // Make the main content visible after the fade-out starts
                mainContent.classList.add('visible');

                // Completely remove the preloader from the page after the transition is done (1 second)
                setTimeout(function() {
                    preloader.style.display = 'none';
                }, 1000); // This should match the transition duration in the CSS

            }, 7000); // Your video is 7 seconds long
        });
    </script>

</body>
</html>