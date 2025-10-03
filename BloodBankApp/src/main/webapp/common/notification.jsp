<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<style>
    /* The CSS from the previous design remains the same */
    .alert-notification {
        position: fixed;
        top: -120px; /* Start off-screen */
        left: 50%;
        transform: translateX(-50%);
        width: 100%;
        max-width: 380px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 8px 25px rgba(0,0,0,0.1);
        display: flex;
        padding: 16px;
        border-left: 5px solid;
        transition: top 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        z-index: 2000;
        overflow: hidden; /* For progress bar */
    }
    .alert-notification.show { top: 20px; }
    .alert-notification .icon { font-size: 1.6rem; margin-right: 15px; line-height: 1; }
    .alert-notification .text-content { flex-grow: 1; }
    .alert-notification .text-content .title { margin: 0 0 2px 0; font-family: 'Poppins', sans-serif; font-weight: 600; font-size: 1rem; color: #333; }
    .alert-notification .text-content p { margin: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; font-weight: 400; color: #555; font-size: 0.9rem; }
    .alert-notification .close-btn { background: transparent; border: none; color: #aaa; font-size: 1.2rem; cursor: pointer; padding: 5px; margin-left: 15px; line-height: 1; transition: color 0.2s; }
    .alert-notification .close-btn:hover { color: #333; }
    .alert-notification.success { border-left-color: #28a745; }
    .alert-notification.success .icon { color: #28a745; }
    .alert-notification.error { border-left-color: #dc3545; }
    .alert-notification.error .icon { color: #dc3545; }
    .alert-notification.info { border-left-color: #007bff; }
    .alert-notification.info .icon { color: #007bff; }
    .progress-bar { position: absolute; bottom: 0; left: 0; height: 4px; width: 100%; background-color: transparent; }
    .progress-bar-inner { height: 100%; width: 100%; background-color: #28a745; animation: shrink 5s linear forwards; border-radius: 0 0 0 8px; }
    .alert-notification.error .progress-bar-inner { background-color: #dc3545; }
    .alert-notification.info .progress-bar-inner { background-color: #007bff; }
    @keyframes shrink { from { width: 100%; } to { width: 0%; } }
</style>

<%-- Logic for Success Message (from URL parameter) --%>
<c:if test="${not empty param.success}">
    <div class="alert-notification success">
        <div class="icon"><i class="fas fa-check-circle"></i></div>
        <div class="text-content">
            <p class="title">Success</p>
            <p>${param.success}</p>
        </div>
        <button class="close-btn">&times;</button>
        <div class="progress-bar"><div class="progress-bar-inner"></div></div>
    </div>
</c:if>

<%-- Logic for Error Message (from URL parameter) --%>
<c:if test="${not empty param.error}">
    <div class="alert-notification error">
        <div class="icon"><i class="fas fa-exclamation-triangle"></i></div>
        <div class="text-content">
            <p class="title">Error</p>
            <p>${param.error}</p>
        </div>
        <button class="close-btn">&times;</button>
        <div class="progress-bar"><div class="progress-bar-inner"></div></div>
    </div>
</c:if>

<%-- ✅ UPDATED: Logic for Welcome Message with User-Specific Logo --%>
<c:if test="${not empty sessionScope.welcomeMessage}">
    <div class="alert-notification info">
        <div class="icon">
            <c:choose>
                <%-- Hospital User --%>
                <c:when test="${not empty sessionScope.hospital}">
                    <i class="fas fa-hospital"></i>
                </c:when>
                <%-- Donor User --%>
                <c:when test="${sessionScope.user.role == 'DONOR'}">
                    <i class="fas fa-heart"></i>
                </c:when>
                <%-- Patient User --%>
                <c:when test="${sessionScope.user.role == 'PATIENT'}">
                    <i class="fas fa-user-injured"></i>
                </c:when>
                <%-- Fallback for Admin or other roles --%>
                <c:otherwise>
                    <i class="fas fa-user-check"></i>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="text-content">
            <p class="title">Welcome!</p>
            <p>${sessionScope.welcomeMessage}</p>
        </div>
        <button class="close-btn">&times;</button>
        <div class="progress-bar"><div class="progress-bar-inner"></div></div>
    </div>
    <c:remove var="welcomeMessage" scope="session"/>
</c:if>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const notification = document.querySelector('.alert-notification');

        if (notification) {
            const closeNotification = () => {
                notification.classList.remove('show');
                setTimeout(() => {
                    if (notification.parentNode) {
                        notification.parentNode.removeChild(notification);
                    }
                }, 500);
            };

            setTimeout(() => {
                notification.classList.add('show');
            }, 100);

            const autoCloseTimer = setTimeout(closeNotification, 5000);

            const closeButton = notification.querySelector('.close-btn');
            if (closeButton) {
                closeButton.addEventListener('click', () => {
                    clearTimeout(autoCloseTimer);
                    closeNotification();
                });
            }
        }
    });
</script>