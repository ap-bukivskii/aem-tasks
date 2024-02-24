package com.tasks.aem.srch.core.servlets;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

@Component(
        service = { Servlet.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=JSON to CSV Conversion Servlet",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/apps/customtools/convertJsonToCsv"
        }
)
public class JsonToCsvServlet extends SlingAllMethodsServlet {

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
        StringBuilder jsonString = new StringBuilder();
        String line;

        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error reading JSON from request body: " + e.getMessage());
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonString.toString());
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment;filename=\"searchResults.csv\"");
            PrintWriter writer = response.getWriter();
            writer.println("Page Path,Component Type");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String pagePath = jsonObject.optString("pagePath", "");
                String componentType = jsonObject.optString("componentType", "");
                writer.println(pagePath + "," + componentType);
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error converting JSON to CSV: " + e.getMessage());
        }
    }
}