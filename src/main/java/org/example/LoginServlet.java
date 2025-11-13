package org.example;
import com.google.cloud.datastore.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            Key userKey = userKeyFactory.newKey(email);
            Entity user = datastore.get(userKey);

            if (user == null) {
                resp.getWriter().print("Login Failed: User not found.");
            } else {
                String storedPassword = user.getString("password");
                if (storedPassword.equals(password)) {
                    resp.getWriter().print("Login Successful! Welcome, " + user.getString("name"));
                } else {
                    resp.getWriter().print("Login Failed: Incorrect password.");
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("Login error: " + e.getMessage());
        }
    }
}