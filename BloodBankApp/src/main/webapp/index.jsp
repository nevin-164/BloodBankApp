<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome to PLASMIC</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: 'Poppins', sans-serif;
        }

        /* Preloader */
        #preloader {
            position: fixed; 
            top: 0; left: 0; 
            width: 100%; height: 100%;
            background-color: #000; 
            display: flex; 
            justify-content: center; 
            align-items: center; 
            z-index: 9999;
            opacity: 1;
            transition: opacity 1s ease;
        }

        #preloader.hidden {
            opacity: 0;
            pointer-events: none;
        }

        .loader-video {
            position: absolute; 
            top: 50%; left: 50%; 
            min-width: 100%; min-height: 100%;
            width: auto; height: auto; 
            transform: translate(-50%, -50%); 
            object-fit: cover;
        }

        /* Main content */
        .main-content {
            text-align: center; 
            padding-top: 50px;
            opacity: 0;   
            transition: opacity 1s ease;
        }

        .main-content.visible {
            opacity: 1;
        }

        h1 { color: #c9302c; }
        .nav { margin-top: 30px; }
        .nav a { 
            margin: 0 15px; 
            text-decoration: none; 
            padding: 12px 25px; 
            border-radius: 5px; 
            color: white; 
            font-size: 16px; 
            font-weight: bold; 
        }
        .user-login { background-color: #007bff; }
        .hospital-login { background-color: #28a745; }

        /* Skip button */
        .skip-btn {
            position: absolute; 
            bottom: 30px; 
            right: 30px; 
            padding: 10px 20px; 
            background: rgba(255,255,255,0.2); 
            color: #fff; 
            border: 2px solid #fff; 
            border-radius: 8px; 
            cursor: pointer; 
            font-weight: bold;
            transition: background 0.3s;
        }
        .skip-btn:hover {
            background: rgba(255,255,255,0.5);
        }
    </style>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600&display=swap" rel="stylesheet">

    <script>
        // 👇 Run before page renders
        if (sessionStorage.getItem("introPlayed")) {
            document.write('<style>#preloader{display:none}</style>');
            window.addEventListener("DOMContentLoaded", () => {
                document.getElementById("main-content").classList.add("visible");
            });
        }
    </script>
</head>
<body>

    <!-- Preloader -->
    <div id="preloader">
        <video autoplay muted playsinline class="loader-video" onended="showMain()">
            <source src="https://nevin-164.github.io/BLOODBANKAPPPREVIEW/loader.mp4" type="video/mp4">
            Your browser does not support the video tag.
        </video>
        <button class="skip-btn" onclick="showMain()">Skip</button>
    </div>

    <!-- Main Content -->
    <div id="main-content" class="main-content">
        <h1>Welcome to PLASMIC</h1>
        <p>Your integrated blood bank management solution.</p>
        <div class="nav">
            <a href="login.jsp" class="user-login">User Portal</a>
            <a href="hospital-login.jsp" class="hospital-login">Hospital Portal</a>
        </div>
    </div>

    <script>
        function showMain() {
            const preloader = document.getElementById('preloader');
            const main = document.getElementById('main-content');

            preloader.classList.add('hidden'); // Fade out
            setTimeout(() => {
                preloader.style.display = 'none';
                main.classList.add('visible'); // Fade in
            }, 1000);

            sessionStorage.setItem("introPlayed", "true");
        }

        window.addEventListener('load', function () {
            if (!sessionStorage.getItem("introPlayed")) {
                setTimeout(showMain, 7000); // if not skipped, auto after 7s
            }
        });
    </script>

</body>
</html>
