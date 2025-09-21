<%-- public-dashboard.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>PLASMIC - Public Dashboard</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body { 
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; 
            padding: 20px; 
            background-color: #f9f9f9;
            color: #333;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            background: #fff;
            padding: 20px 30px;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        h1 { 
            color: #c9302c; 
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
            margin-top: 0;
        }
        p.subtitle {
            font-size: 1.1rem;
            color: #555;
        }

        /* --- Bar Graph Styles --- */
        .bar-chart-container {
            width: 100%;
            margin-top: 30px;
        }
        .bar-group {
            margin-bottom: 15px;
        }
        .bar-label {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 8px;
        }
        .bar-wrapper {
            width: 100%;
            background-color: #e9ecef;
            border-radius: 5px;
            overflow: hidden;
            box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);
        }
        .bar {
            height: 30px;
            line-height: 30px;
            color: white;
            font-weight: 600;
            padding-left: 10px;
            border-radius: 5px 0 0 5px;
            white-space: nowrap;
            transition: width 0.5s ease-in-out;
        }

        /* The Status Levels from our Servlet */
        .bar.High {
            width: 95%; /* Don't make it 100% so it looks better */
            background-color: #28a745; /* Green */
        }
        .bar.Medium {
            width: 60%;
            background-color: #ffc107; /* Yellow */
            color: #333; /* Dark text on yellow */
        }
        .bar.Low {
            width: 30%;
            background-color: #fd7e14; /* Orange */
        }
        .bar.Empty {
            width: 10%; /* Show a small sliver */
            background-color: #dc3545; /* Red */
            font-size: 0.9rem;
        }
        
        .no-stock {
            font-size: 1.1rem;
            font-weight: 500;
            color: #555;
        }

    </style>
</head>
<body>
    <div class="container">
        <h1>Public Blood Stock Status</h1>
        <p class="subtitle">This dashboard shows the combined, system-wide status for all blood types.</p>

        <div class="bar-chart-container">
            
            <%-- Check if the map from the servlet is empty --%>
            <c:if test="${empty stockStatusMap}">
                <p class="no-stock">No stock data is currently available.</p>
            </c:if>
            
            <%-- Loop over the new "stockStatusMap" from the servlet --%>
            <c:forEach var="entry" items="${stockStatusMap}">
                <div class="bar-group">
                    <%-- entry.key is the Blood Group (e.g., "O+") --%>
                    <div class="bar-label">${entry.key}</div>
                    
                    <div class="bar-wrapper">
                        <%-- entry.value is the Status (e.g., "High", "Low") --%>
                        <%-- We use the status as both the CSS class and the text --%>
                        <div class="bar ${entry.value}">${entry.value}</div>
                    </div>
                </div>
            </c:forEach>
            
        </div>
    </div>
</body>
</html>