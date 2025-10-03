<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>
    .notification {
        position: fixed;
        top: 20px;
        right: -400px; /* Start off-screen */
        width: 100%;
        max-width: 350px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 5px 15px rgba(0,0,0,0.15);
        display: flex;
        align-items: center;
        padding: 15px 20px;
        border-left: 5px solid;
        transition: right 0.5s cubic-bezier(0.68, -0.55, 0.27, 1.55);
        z-index: 1000;
        overflow: hidden; /* Important for the progress bar */
    }

    /* Animation States */
    .notification.show {
        right: 20px; /* Slide in */
    }
    .notification.hide {
        right: -400px; /* Slide out */
    }

    /* Icon Styles */
    .notification .icon {
        font-size: 1.5rem;
        margin-right: 15px;
    }

    /* Text Styles */
    .notification .text-content {
        flex-grow: 1;
    }
    .notification .text-content p {
        margin: 0;
        font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        font-weight: 500;
        color: #333;
    }

    /* Color Variants */
    .notification-success {
        border-left-color: #28a745;
    }
    .notification-success .icon {
        color: #28a745;
    }

    .notification-error {
        border-left-color: #dc3545;
    }
    .notification-error .icon {
        color: #dc3545;
    }

    /* Progress Bar */
    .progress-bar {
        position: absolute;
        bottom: 0;
        left: 0;
        height: 4px;
        width: 100%;
        background-color: rgba(0,0,0,0.1);
    }
    .progress-bar::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        width: 0; /* Starts at 0 width */
        background-color: #28a745; /* Default to success color */
        border-radius: 0 0 0 8px;
        animation: progress 5s linear forwards;
    }
    .notification-error .progress-bar::after {
        background-color: #dc3545; /* Error color */
    }

    @keyframes progress {
        to {
            width: 100%;
        }
    }
</style>

<c:if test="${not empty param.success}">
    <div class="notification notification-success">
        <div class="icon"><i class="fas fa-check-circle"></i></div>
        <div class="text-content">
            <p>${param.success}</p>
        </div>
        <div class="progress-bar"></div>
    </div>
</c:if>

<c:if test="${not empty param.error}">
    <div class="notification notification-error">
        <div class="icon"><i class="fas fa-times-circle"></i></div>
        <div class="text-content">
            <p>${param.error}</p>
        </div>
        <div class="progress-bar"></div>
    </div>
</c:if>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const notification = document.querySelector('.notification');

        if (notification) {
            // 1. Slide the notification in
            setTimeout(() => {
                notification.classList.add('show');
            }, 100); // Small delay to allow the element to render first

            // 2. Start timer to slide it out
            setTimeout(() => {
                notification.classList.remove('show');
                notification.classList.add('hide');
                
                // 3. Remove from DOM after animation
                setTimeout(() => {
                    if (notification.parentNode) {
                        notification.parentNode.removeChild(notification);
                    }
                }, 500); // Matches the CSS transition duration
            }, 5000); // Notification is visible for 5 seconds
        }
    });
</script>