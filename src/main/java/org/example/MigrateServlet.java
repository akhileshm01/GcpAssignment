package org.example;

import com.google.cloud.bigquery.*;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;

import com.google.cloud.WriteChannel;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.WriteChannelConfiguration;
import com.google.cloud.bigquery.Job;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "MigrateServlet", urlPatterns = {"/migrate"})
public class MigrateServlet extends HttpServlet {

    private final String DATASET_NAME = "user_data";
    private final String TABLE_NAME = "User";

    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Schema schema = Schema.of(Field.of("email", LegacySQLTypeName.STRING),
                    Field.of("name", LegacySQLTypeName.STRING),
                    Field.of("dob", LegacySQLTypeName.STRING),
                    Field.of("phone", LegacySQLTypeName.STRING)
            );

            TableId tableId = createTable(DATASET_NAME, TABLE_NAME, schema);

            Query<Entity> datastoreQuery = Query.newEntityQueryBuilder().setKind("User").build();
            QueryResults<Entity> users = datastore.run(datastoreQuery);

            WriteChannelConfiguration loadConfig = WriteChannelConfiguration.newBuilder(tableId)
                    .setFormatOptions(FormatOptions.csv()).setAutodetect(false)
                    .build();

            WriteChannel writer = bigquery.writer(loadConfig);
            Job job;
            try (OutputStream outputStream = Channels.newOutputStream(writer);
                 PrintWriter csvWriter = new PrintWriter(outputStream)) {

                while (users.hasNext()) {
                    Entity user = users.next();

                    String email = user.getKey().getName();
                    String name = user.getString("name");
                    String dob = user.getString("dob");
                    String phone = user.getString("phone");

                    email = (email == null) ? "" : email;
                    name = (name == null) ? "" : name;
                    dob = (dob == null) ? "" : dob;
                    phone = (phone == null) ? "" : phone;

                    csvWriter.printf("\"%s\",\"%s\",\"%s\",\"%s\"\n", email.replace("\"", "\"\""),
                            name.replace("\"", "\"\""), dob.replace("\"", "\"\""),
                            phone.replace("\"", "\"\""));
                }
            }

            job = ((TableDataWriteChannel) writer).getJob();
            job = job.waitFor();

            if (job.getStatus().getError() != null) {
                throw new RuntimeException("BigQuery load job failed: " + job.getStatus().getError().toString());
            }
            String bqQuery = "SELECT * FROM `" + tableId.getDataset() + "." + tableId.getTable() + "` LIMIT 100";
            TableResult result = bigquery.query(QueryJobConfiguration.newBuilder(bqQuery).build());

            String jsonResponse = formatResultsAsJson(result);
            resp.setContentType("application/json");
            resp.getWriter().print(jsonResponse);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().print("{\"error\": \"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    private TableId createTable(String datasetName, String tableName, Schema schema) {
        try {
            bigquery.create(DatasetInfo.newBuilder(datasetName).build());
        } catch (BigQueryException e) {
            if (!e.getMessage().contains("Already Exists")) throw e;
        }

        TableId tableId = TableId.of(datasetName, tableName);
        try {
            TableDefinition tableDefinition = StandardTableDefinition.of(schema);
            bigquery.create(TableInfo.of(tableId, tableDefinition));
        } catch (BigQueryException e) {
            if (!e.getMessage().contains("Already Exists")) throw e;
        }
        return tableId;
    }

    private String formatResultsAsJson(TableResult result) {
        StringBuilder json = new StringBuilder();
        json.append("{\"message\": \"Successfully migrated data.\", \"data\": [");

        boolean firstRow = true;
        for (FieldValueList row : result.iterateAll()) {
            if (!firstRow) json.append(",");
            json.append("{");

            boolean firstField = true;
            for (Field field : result.getSchema().getFields()) {
                if (!firstField) json.append(",");
                String fieldName = field.getName();

                String fieldValue = "null";
                if (row.get(fieldName) != null && !row.get(fieldName).isNull()) {
                    fieldValue = row.get(fieldName).getStringValue();
                }

                String safeValue = fieldValue.replace("\"", "\\\"");
                json.append("\"").append(fieldName).append("\":\"").append(safeValue).append("\"");

                firstField = false;
            }
            json.append("}");
            firstRow = false;
        }
        json.append("]}");
        return json.toString();
    }
}