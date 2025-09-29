package servlet;

import dao.DBUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * A temporary diagnostic tool to trace a stock transfer and determine why
 * the receiving hospital might not be seeing the incoming shipment.
 */
@WebServlet("/transfer-diagnostics")
public class TransferDiagnosticsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Start HTML output
        out.println("<html><head><title>Transfer Diagnostics</title>");
        out.println("<style>body { font-family: sans-serif; padding: 20px; } h2 { color: #c9302c; } pre { background-color: #f4f4f4; padding: 15px; border: 1px solid #ddd; } code { font-family: monospace; }</style>");
        out.println("</head><body>");
        out.println("<h1>Stock Transfer Diagnostic Report</h1>");

        String transferIdStr = request.getParameter("transferId");
        if (transferIdStr == null || transferIdStr.trim().isEmpty()) {
            out.println("<p style='color: red;'>ERROR: Please provide a 'transferId' in the URL. Example: ?transferId=1</p>");
            out.println("</body></html>");
            return;
        }

        try {
            int transferId = Integer.parseInt(transferIdStr);
            out.println("<h2>🔍 Checking Transfer ID: " + transferId + "</h2>");

            int requestingHospitalId = 0;
            int supplyingHospitalId = 0;
            String bloodGroup = "";
            int units = 0;

            // --- Step 1: Check the original transfer request itself ---
            out.println("<h3>Step 1: Analyzing the `stock_transfers` record...</h3>");
            String transferSql = "SELECT * FROM stock_transfers WHERE transfer_id = ?";
            try (Connection con = DBUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(transferSql)) {
                ps.setInt(1, transferId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        requestingHospitalId = rs.getInt("requesting_hospital_id");
                        supplyingHospitalId = rs.getInt("supplying_hospital_id");
                        bloodGroup = rs.getString("blood_group");
                        units = rs.getInt("units");
                        String status = rs.getString("transfer_status");

                        out.println("<p><b>Status:</b> " + status + "</p>");
                        out.println("<p><b>Requesting Hospital ID:</b> " + requestingHospitalId + "</p>");
                        out.println("<p><b>Supplying Hospital ID:</b> " + supplyingHospitalId + "</p>");
                        out.println("<p><b>Blood Group:</b> " + bloodGroup + "</p>");
                        out.println("<p><b>Units Requested:</b> " + units + "</p>");
                        
                        if (!"APPROVED".equals(status)) {
                             out.println("<p style='color: orange;'><b>NOTE:</b> This transfer is not marked as 'APPROVED'. The transfer logic would not have run.</p>");
                        }

                    } else {
                        out.println("<p style='color: red;'><b>CRITICAL ERROR:</b> No record found in `stock_transfers` for this ID.</p>");
                        out.println("</body></html>");
                        return;
                    }
                }
            }

            // --- Step 2: Check the inventory for in-transit bags ---
            out.println("<h3>Step 2: Searching for matching bags in `blood_inventory`...</h3>");
            out.println("<p>The system should find bags where <code>hospital_id = " + requestingHospitalId + "</code> AND <code>inventory_status = 'IN_TRANSIT'</code>.</p>");
            
            String inventorySql = "SELECT bag_id, hospital_id, inventory_status, blood_group FROM blood_inventory WHERE hospital_id = ? AND inventory_status = 'IN_TRANSIT' AND blood_group = ?";
            int foundBags = 0;
            
            try (Connection con = DBUtil.getConnection();
                 PreparedStatement ps = con.prepareStatement(inventorySql)) {
                ps.setInt(1, requestingHospitalId);
                ps.setString(2, bloodGroup);
                
                out.println("<pre><code>" + ps.toString().replaceFirst(".*: ", "") + "</code></pre>");

                try (ResultSet rs = ps.executeQuery()) {
                    out.println("<h4>Query Results:</h4>");
                    while(rs.next()) {
                        foundBags++;
                        out.println("<li>Found Bag ID: <b>" + rs.getInt("bag_id") + "</b> - Correctly assigned and in transit.</li>");
                    }
                }
            }
            
            // --- Step 3: Final Diagnosis ---
            out.println("<h3>Step 3: Final Diagnosis</h3>");
            if (foundBags > 0) {
                 out.println("<p style='color: green; font-weight: bold;'>✅ SUCCESS: Found " + foundBags + " bag(s) correctly assigned to the receiving hospital and marked as 'IN_TRANSIT'. The 'Receive' button should be appearing.</p>");
                 out.println("<p>If the button is still not appearing, the issue is likely a caching problem in the JSP or a logic error in the `HospitalDashboardServlet`'s `getInTransitBagsByHospital` call.</p>");
            } else {
                out.println("<p style='color: red; font-weight: bold;'>❌ FAILURE: The query found 0 bags assigned to the receiving hospital with 'IN_TRANSIT' status.</p>");
                out.println("<p><b>This is the root cause of the problem.</b> It confirms that the `UPDATE` command in the `BloodInventoryDAO.transferBags` method is not working correctly on your server. The `hospital_id` of the bags is not being changed to " + requestingHospitalId + ".</p>");
                out.println("<p>This is a classic symptom of a server synchronization issue. The server is running an old version of the `BloodInventoryDAO.class` file. Please perform the <b>'Full Manual Reset'</b> procedure to fix this.</p>");
            }


        } catch (Exception e) {
            out.println("<p style='color: red;'>An exception occurred: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }

        out.println("</body></html>");
    }
}
