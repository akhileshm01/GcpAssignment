package org.example;
import com.google.cloud.datastore.*;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "UploadServlet", urlPatterns = {"/upload"})
@MultipartConfig
public class UploadServlet extends HttpServlet {

    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private KeyFactory userKeyFactory = datastore.newKeyFactory().setKind("User");

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Part filePart = req.getPart("userFile");
            InputStream fileContent = filePart.getInputStream();
            XSSFWorkbook workbook = new XSSFWorkbook(fileContent);
            XSSFSheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();

            int rows = sheet.getPhysicalNumberOfRows();
            int successfulUploads = 0;

            for (int i = 1; i < rows; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) continue;

                String name = formatter.formatCellValue(row.getCell(0));
                String dob = formatter.formatCellValue(row.getCell(1));
                String email = formatter.formatCellValue(row.getCell(2));
                String password = formatter.formatCellValue(row.getCell(3));
                String phone = formatter.formatCellValue(row.getCell(4));

                Key userKey = userKeyFactory.newKey(email);

                Entity userEntity = Entity.newBuilder(userKey)
                        .set("name", name)
                        .set("dob", dob)
                        .set("email",email)
                        .set("password", password)
                        .set("phone", phone)
                        .build();

                datastore.put(userEntity);
                successfulUploads++;
            }
            
            workbook.close();
            resp.getWriter().print("Successfully uploaded " + successfulUploads + " users to Datastore.");

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("Upload failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}