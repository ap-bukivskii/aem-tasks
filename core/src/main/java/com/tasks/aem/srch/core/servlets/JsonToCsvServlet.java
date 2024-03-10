package com.tasks.aem.srch.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import com.tasks.aem.srch.core.services.JsonToCsvService;

@Component(
        service = {Servlet.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=JSON to CSV Conversion Servlet",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/customtools/convertJsonToCsv"
        }
)
public class JsonToCsvServlet extends SlingAllMethodsServlet {

    @Reference
    private JsonToCsvService jsonToCsvService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String jsonString;
        try (BufferedReader reader = request.getReader()) {
            jsonString = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Error reading JSON from request body.", e);
            return;
        }

        try {
            String csvData = jsonToCsvService.convertJsonToCsv(jsonString);
            response.setContentType("text/csv");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"searchResults.csv\"");

            try (PrintWriter writer = response.getWriter()) {
                writer.write(csvData);
            }
        } catch (JSONException e) {
            sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error converting JSON to CSV.", e);
        }
    }
    private void sendErrorResponse(SlingHttpServletResponse response, int statusCode, String message, Exception e) throws IOException {
        response.setStatus(statusCode);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(message + " " + e.getMessage());
        }
    }
}