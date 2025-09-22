<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Welcome to PLASMIC</title>
    
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;600;700&display=swap" rel="stylesheet">

    <style>
    body {
        font-family: 'Poppins', sans-serif;
        margin: 0;
        overflow: hidden; /* Hide scrollbars during preloader */
    }

    /* --- Preloader Styles --- */
   #preloader {
    position: fixed; top: 0; left: 0; width: 100%; height: 100%;
    background-color: #fff; /* White background */
    display: flex; justify-content: center; align-items: center; 
    z-index: 9999;
    cursor: pointer;
    transition: opacity 1s ease;
}
    #preloader.fade-out {
        opacity: 0;
    }
    .loader-video {
    position: absolute;
    top: 50%; left: 50%;
    transform: translate(-50%, -50%);
    min-width: 100%;
    min-height: 100%;
    width: auto;
    height: auto;
    object-fit: contain;
    background: #fff;
}


    /* --- Main Content Styles --- */
    .main-content {
        height: 100vh;
        background-image: url('<%= request.getContextPath() %>/images/indeximage.png');
        background-size: cover;
        background-position: center;
        display: flex;
        justify-content: center;
        align-items: center;
        text-align: center;
        opacity: 0;
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
        max-width: 700px; /* ✅ UPDATED: Made wider for 3 cards */
        width: 90%;
        text-align: center;
        opacity: 0;
        transform: translateY(30px);
        transition: opacity 1s ease, transform 1s ease;
    }
      .content-panel.visible {
        opacity: 1;
        transform: translateY(0);
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
        flex-wrap: wrap; /* ✅ ADDED: Allows cards to wrap nicely if needed */
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
    
    /* --- Media Query for Mobile Devices --- */
    @media (max-width: 600px) {
        .loader-video {
            /* Keep the same very high initial zoom */
            transform: translate(-50%, -50%) scale(5.0); 
            /* Slower transition to make the de-zoom gradual */
            transition: transform 3s ease-out; 
            min-width: 200%;
            min-height: 200%;
        }

        .loader-video.de-zoom {
            /* De-zoomed state remains the same */
            transform: translate(-50%, -50%) scale(0.6);
            max-width: 90%;
            max-height: 90%;
        }
    }
</style>
</head>
<body>

  <div id="preloader">
    <video autoplay muted playsinline class="loader-video">
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
              <%-- ✅ NEW: Link to Community Forum --%>
              <a href="${pageContext.request.contextPath}/community" class="portal-card">
                  <div class="icon">💬</div>
                  <h3>Community Forum</h3>
              </a>
          </div>
      </div>
  </div>

  <script>
      let animationEnded = false;
      const preloader = document.getElementById('preloader');
      const mainContent = document.getElementById('main-content');
      const contentPanel = document.querySelector('.content-panel');
      const video = document.querySelector('.loader-video');
      
      // Trigger the de-zoom animation on mobile immediately
      // to give it the full video length to finish
      setTimeout(() => {
          if (window.innerWidth <= 600) {
              video.classList.add('de-zoom');
          }
      }, 0); 

      function showMain() {
          if (animationEnded) return;
          animationEnded = true;

          preloader.classList.add('fade-out');
          
          setTimeout(() => {
              mainContent.classList.add('visible');
              contentPanel.classList.add('visible');
              document.body.style.overflow = 'auto'; // Restore scrolling
          }, 50);

          setTimeout(() => {
              preloader.style.display = 'none';
          }, 1000); 
      }

      video.onended = showMain;
      preloader.addEventListener('click', showMain);
  </script>

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