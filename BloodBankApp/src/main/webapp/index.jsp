<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome to PLASMIC</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary-red: #E63946;
            --primary-red-hover: #D62839;
            --dark-text: #2b2d42;
            --tagline-text: #495057;
            --background-color: #f8f9fa;
            --portal-bg-dark: #f0f3f5;
        }

        body {
            font-family: 'Poppins', sans-serif;
            margin: 0;
            background-color: var(--background-color);
            overflow: hidden;
            color: var(--dark-text);
        }

        /* --- Preloader Styles (Unchanged) --- */
        #preloader {
            position: fixed; top: 0; left: 0; width: 100%; height: 100%;
            background-color: #fff;
            display: flex; justify-content: center; align-items: center; 
            z-index: 9999;
            cursor: pointer;
            transition: opacity 1s ease;
        }
        #preloader.fade-out { opacity: 0; }
        .loader-video {
            position: absolute; top: 50%; left: 50%;
            transform: translate(-50%, -50%);
            min-width: 100%; min-height: 100%;
            width: auto; height: auto; object-fit: cover;
            background: #fff;
        }
        .skip-btn {
            position: absolute; bottom: 40px; right: 40px;
            background-color: var(--primary-red);
            color: #fff;
            border: 2px solid var(--primary-red);
            padding: 12px 25px;
            border-radius: 5px;
            font-size: 1rem;
            font-weight: 500;
            cursor: pointer;
            transition: background-color 0.3s ease, border-color 0.3s ease, box-shadow 0.3s ease;
            z-index: 10000;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        .skip-btn:hover {
            background-color: var(--primary-red-hover);
            border-color: var(--primary-red-hover);
            box-shadow: 0 6px 15px rgba(0,0,0,0.15);
        }

        /* --- Main Content Styles (Unchanged) --- */
        .main-content {
            display: flex;
            height: 100vh;
            width: 100%;
            opacity: 0;
            transition: opacity 0.5s ease-in;
        }
        .main-content.visible { opacity: 1; }

        @keyframes slideInFromLeft {
            from { transform: translateX(-100%); }
            to { transform: translateX(0); }
        }
        @keyframes slideInFromRight {
            from { transform: translateX(100%); }
            to { transform: translateX(0); }
        }

        .image-panel {
            flex-basis: 70%;
            background-image: url('<%= request.getContextPath() %>/images/indeximage.png');
            background-size: cover;
            background-position: center;
            animation: slideInFromLeft 1.2s cubic-bezier(0.25, 1, 0.5, 1) forwards;
        }

        .content-panel {
            flex-basis: 30%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 4vw 2.5vw;
            background-color: var(--portal-bg-dark);
            animation: slideInFromRight 1.2s cubic-bezier(0.25, 1, 0.5, 1) forwards;
            position: relative;
            overflow: hidden;
        }
        
        /* Creative background (Unchanged) */
        .content-panel::before {
            content: '';
            position: absolute;
            top: -20%; left: -20%;
            width: 140%; height: 140%;
            background: radial-gradient(circle at 40% 60%, rgba(230, 57, 70, 0.08) 0%, rgba(230, 57, 70, 0.02) 30%, rgba(255, 255, 255, 0) 60%),
                        radial-gradient(circle at 70% 30%, rgba(141, 153, 174, 0.08) 0%, rgba(141, 153, 174, 0.02) 30%, rgba(255, 255, 255, 0) 60%);
            background-blend-mode: overlay;
            filter: blur(80px);
            opacity: 0.8;
            transform: rotate(15deg);
            animation: backgroundFlow 20s infinite alternate ease-in-out;
            z-index: 0;
        }
        @keyframes backgroundFlow {
            from { transform: scale(1) rotate(15deg); }
            to { transform: scale(1.1) rotate(18deg) translate(10px, 15px); }
        }

        .content-wrapper {
            max-width: 400px;
            margin: 0 auto;
            text-align: left;
            position: relative;
            z-index: 1;
        }
        
        /* --- RESTORED TITLE AND TAGLINE --- */
        h1 {
            color: var(--primary-red);
            font-size: clamp(2.2rem, 3.5vw, 3rem);
            font-weight: 700;
            margin: 0;
            line-height: 1.1;
        }
        p.tagline {
            color: var(--tagline-text);
            font-size: 1rem;
            margin: 15px 0 40px 0;
            font-weight: 400;
            text-shadow: 0 1px 1px rgba(255, 255, 255, 0.3);
        }

        /* --- UPDATED COMPACT PORTAL BUTTONS --- */
        .portal-grid {
            display: grid;
            grid-template-columns: 1fr;
            gap: 20px;
            perspective: 1200px;
        }

        .portal-card {
            display: flex;
            flex-direction: row; /* Icon and text side-by-side */
            align-items: center;
            background: linear-gradient(135deg, #ffffff 0%, #fdfdfd 100%);
            border: 2px solid transparent;
            padding: 18px; /* Reduced padding */
            border-radius: 12px; /* Sharper radius */
            text-decoration: none;
            position: relative;
            overflow: hidden;
            
            transform: rotateY(-15deg) rotateX(7deg);
            transform-style: preserve-3d;
            box-shadow: 0 10px 25px rgba(43, 45, 66, 0.08);

            transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
        }

        .portal-card:hover {
            transform: rotateY(0deg) rotateX(0deg) scale(1.08);
            box-shadow: 0 20px 40px rgba(43, 45, 66, 0.18);
            border-color: var(--primary-red);
            z-index: 10;
        }
        
        /* Light effect (Unchanged) */
        .portal-card::before {
            content: "";
            position: absolute; top: 0; left: 0;
            width: 100%; height: 100%;
            background: radial-gradient(circle at calc(var(--mouse-x, 0.5) * 100%) calc(var(--mouse-y, 0.5) * 100%), rgba(255, 255, 255, 0.8) 0%, rgba(255, 255, 255, 0) 40%);
            opacity: 0;
            transition: opacity 0.4s ease;
            z-index: 0;
        }
        .portal-card:hover::before { opacity: 1; }

        .portal-card .icon, .portal-card .card-text {
            position: relative;
            z-index: 1;
        }

        /* Smaller icon container with emoji */
        .portal-card .icon {
            font-size: 1.8rem; /* Emoji size */
            color: var(--primary-red);
            background-color: #FEEBEE;
            height: 50px; /* Reduced container size */
            width: 50px;
            margin-right: 15px; /* Space between icon and text */
            display: inline-flex;
            justify-content: center;
            align-items: center;
            border-radius: 50%;
            flex-shrink: 0;
        }
        
        .portal-card h3 {
            font-size: 1rem; /* Reduced title size */
            font-weight: 600;
            color: var(--dark-text);
            margin: 0;
        }

        /* --- Media Queries --- */
        @media (max-width: 900px) {
            .main-content { flex-direction: column; }
            .image-panel { flex-basis: 45vh; animation: none; }
            .content-panel { flex-basis: 55vh; justify-content: center; animation: none; }
            .content-wrapper { text-align: center; }
            .portal-card { transform: none; }
            .portal-card:hover { transform: translateY(-5px); }
        }
        @media (max-width: 600px) {
            .skip-btn { bottom: 20px; right: 20px; padding: 10px 20px; font-size: 0.9rem; }
            .portal-card { padding: 15px; }
            .portal-card .icon { height: 45px; width: 45px; font-size: 1.5rem; }
            .portal-card h3 { font-size: 0.9rem; }
        }
    </style>
</head>
<body>

<jsp:include page="common/notification.jsp" />

<div id="preloader">
  <video autoplay muted playsinline class="loader-video" id="loaderVideo">
    <source src="https://nevin-164.github.io/BLOODBANKAPPPREVIEW/loader.mp4" type="video/mp4">
    Your browser does not support the video tag.
  </video>
  <button class="skip-btn" onclick="showMain()">Skip</button>
</div>

<div id="main-content" class="main-content">
    <div class="image-panel"></div>
    <div class="content-panel">
        <div class="content-wrapper">
            <h1>PLASMIC</h1>
            <p class="tagline">Your integrated blood bank management solution.</p>

            <div class="portal-grid">
                <a href="login.jsp" class="portal-card">
                    <div class="icon">👤</div>
                    <div class="card-text">
                        <h3>User Portal</h3>
                    </div>
                </a>
                <a href="hospital-login.jsp" class="portal-card">
                    <div class="icon">🏥</div>
                    <div class="card-text">
                        <h3>Hospital Portal</h3>
                    </div>
                </a>
                <a href="${pageContext.request.contextPath}/community" class="portal-card">
                    <div class="icon">💬</div>
                    <div class="card-text">
                        <h3>Community Forum</h3>
                    </div>
                </a>
                
            </div>
        </div>
    </div>
</div>


<script>
    // PART 1: Preloader Logic (Unchanged)
    let animationEnded = false; 
    const preloader = document.getElementById('preloader');
    const mainContent = document.getElementById('main-content');
    const video = document.getElementById('loaderVideo');
    
    function showMain() {
        if (animationEnded) return;
        animationEnded = true;
        preloader.classList.add('fade-out');
        setTimeout(() => {
            mainContent.classList.add('visible');
            document.body.style.overflow = 'auto';
        }, 50);
        setTimeout(() => {
            preloader.style.display = 'none';
        }, 1000); 
    }

    video.onended = showMain;
    preloader.addEventListener('click', showMain);

    // PART 2: Interactive 3D Card Logic (Unchanged)
    const cards = document.querySelectorAll('.portal-card');
    cards.forEach(card => {
        card.addEventListener('mousemove', e => {
            const rect = card.getBoundingClientRect();
            const x = (e.clientX - rect.left) / rect.width;
            const y = (e.clientY - rect.top) / rect.height;
            card.style.setProperty('--mouse-x', x);
            card.style.setProperty('--mouse-y', y);
        });
    });
</script>

<%-- Tawk.to Chat Widget Script (Unchanged) --%>
<script type="text/javascript">
var Tawk_API=Tawk_API||{}, Tawk_LoadStart=new Date();
(function(){
var s1=document.createElement("script"),s0=document.getElementsByTagName("script")[0];
s1.async=true;
s1.src='https://embed.tawk.to/68d03e1d5510221925d154c5/1j5mn58ug';
s1.charset='UTF-8';
s1.setAttribute('crossorigin','*');
s0.parentNode.insertBefore(s1,s0);
})();
</script>

</body>
</html>