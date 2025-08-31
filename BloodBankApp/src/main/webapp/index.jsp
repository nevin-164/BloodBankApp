<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome to PLASMIC</title>
    <style>
        #preloader {
            position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background-color: #000; display: flex; justify-content: center; align-items: center; z-index: 9999;
            cursor: pointer; /* Add a pointer to indicate it's clickable */
            transition: opacity 1s ease;
        }
        #preloader.fade-out {
            opacity: 0;
        }
        .loader-video {
            position: absolute; top: 50%; left: 50%; min-width: 100%; min-height: 100%;
            width: auto; height: auto; transform: translateX(-50%) translateY(-50%); object-fit: cover;
        }
        .main-content {
            font-family: 'Poppins', sans-serif; text-align: center; padding-top: 50px;
            opacity: 0; /* Initially hidden */
            transition: opacity 1s ease;
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
     <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">
</head>
<body>

    <div id="preloader">
        <video class="loader-video" autoplay muted loop playsinline>
            <source src="<%= request.getContextPath() %>/images/loader.mp4" type="video/mp4">
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
            var animationEnded = false;

            // Function to end the animation and show the main content
            function endAnimation() {
                if (animationEnded) return; // Prevent running more than once
                animationEnded = true;

                preloader.classList.add('fade-out');
                mainContent.classList.add('visible');

                // Completely remove the preloader after the transition is done
                setTimeout(function() {
                    preloader.style.display = 'none';
                }, 1000); // This should match the CSS transition duration
            }

            // Set the automatic timer for 7 seconds
            var timer = setTimeout(endAnimation, 7000);

            // ✅ MODIFICATION: Add a click event listener to skip the animation
            preloader.addEventListener('click', function() {
                clearTimeout(timer); // Cancel the automatic timer
                endAnimation();      // End the animation immediately
            });
        });
    </script>

</body>
</html>