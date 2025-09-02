<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome to PLASMIC</title>
    
    <!-- Google Font for a modern look -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            margin: 0;
            overflow: hidden; /* Hide scrollbars during preloader */
        }

        /* --- Preloader Styles (Kept from your version) --- */
        #preloader {
            position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background-color: #000; display: flex; justify-content: center; align-items: center; z-index: 9999;
            cursor: pointer;
            transition: opacity 1s ease;
        }
        #preloader.fade-out {
            opacity: 0;
        }
        .loader-video {
            position: absolute; top: 50%; left: 50%; min-width: 100%; min-height: 100%;
            width: auto; height: auto; transform: translate(-50%, -50%); object-fit: cover;
        }

        /* --- ✅ NEW: Creative Main Content Styles --- */
        
        .main-content {
            height: 100vh;
            /* High-quality background image */
            background-image: url('<%= request.getContextPath() %>/images/indeximage.png');
            background-size: cover;
            background-position: center;
            /* Flexbox to center the content panel */
            display: flex;
            justify-content: center;
            align-items: center;
            text-align: center;
            opacity: 0; /* Initially hidden */
            transition: opacity 1s ease;
        }
     .main-content.visible {
    opacity: 1;
}


        /* The semi-transparent "glass" panel */
.content-panel {
    background: rgba(255, 255, 255, 0.3);
    backdrop-filter: blur(12px);
    -webkit-backdrop-filter: blur(12px);
    padding: 40px;
    border-radius: 20px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
    max-width: 600px;
    width: 90%;
    text-align: center;
    opacity: 0;               /* start hidden */
    transform: translateY(30px); /* slight downward offset */
    transition: opacity 1s ease, transform 1s ease;
}


.content-panel.visible {
    opacity: 1;
    transform: translateY(0); /* slides into place */
}




        h1 {
            color: #c9302c;
            font-size: 52px;
            font-weight: 700;
            margin: 0;
        }
        p.tagline {
            color: #6c757d;
            font-size: 18px;
            margin-bottom: 30px;
        }
        .portal-grid {
            display: flex;
            gap: 20px;
            justify-content: center;
        }
        .portal-card {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            padding: 30px;
            border-radius: 10px;
            text-decoration: none;
            color: #333;
            width: 200px;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        .portal-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 15px 25px rgba(0,0,0,0.1);
        }
        .portal-card .icon {
            font-size: 48px;
            margin-bottom: 10px;
        }
        .portal-card h3 {
            margin: 0;
            font-size: 20px;
            color: #333;
        }
        .skip-btn {
    position: absolute;
    bottom: 40px;
    right: 40px;
    background: rgba(255,255,255,0.6);
    border: none;
    padding: 10px 20px;
    border-radius: 8px;
    font-size: 16px;
    cursor: pointer;
    color: #333;
    transition: background 0.3s ease;
}
.skip-btn:hover {
    background: rgba(255,255,255,0.8);
}
        
    </style>
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

    <div id="main-content" class="main-content">
        <div class="content-panel">
            <h1>PLASMIC</h1>
            <p class="tagline">Your integrated blood bank management solution.</p>
            <div class="portal-grid">
                <a href="login.jsp" class="portal-card">
                    <div class="icon">👤</div>
                    <h3>User Portal</h3>
                </a>
                <a href="hospital-login.jsp" class="portal-card">
                    <div class="icon">🏥</div>
                    <h3>Hospital Portal</h3>
                </a>
            </div>
        </div>
    </div>

    <script>
    
        window.addEventListener('load', function() {
            var preloader = document.getElementById('preloader');
            var mainContent = document.getElementById('main-content');
            var animationEnded = false;

            function endAnimation() {
                if (animationEnded) return;
                animationEnded = true;

                preloader.classList.add('fade-out');
                mainContent.style.display = 'flex';

                setTimeout(() => {
                    mainContent.classList.add('visible');
                    document.querySelector('.content-panel').classList.add('visible'); // 👈 add this
                }, 50);

                setTimeout(() => {
                    preloader.style.display = 'none';
                }, 1000);
            }

            var timer = setTimeout(endAnimation, 7000);

            preloader.addEventListener('click', function() {
                clearTimeout(timer);
                endAnimation();
            });
            
            // Add a smooth transition effect to preloader
            preloader.style.transition = 'opacity 1s ease';
        });
    </script>

</body>
</html>