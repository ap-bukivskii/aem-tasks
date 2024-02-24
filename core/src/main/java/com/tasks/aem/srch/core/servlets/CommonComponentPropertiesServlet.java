package com.tasks.aem.srch.core.servlets;

import com.day.cq.dam.api.Asset;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component(
        service = {Servlet.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=Common Component Properties Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/apps/customtools/commonproperties",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
        }
)
public class CommonComponentPropertiesServlet extends SlingAllMethodsServlet {

    private static final String CSV_FILE_PATH = "/content/dam/srch/commonProperties.csv";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResourceResolver resolver = request.getResourceResolver();
        Resource csvFileResource = resolver.getResource(CSV_FILE_PATH);
        Asset csvAsset = csvFileResource.adaptTo(Asset.class);

        JSONArray jsonProperties = new JSONArray();

        if (csvAsset != null) {
            try (BufferedReader reader =  new BufferedReader(new InputStreamReader(csvAsset.getRendition("original").getStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    JSONObject jsonProperty = new JSONObject();
                    if (values.length > 1) {
                        jsonProperty.put("propertyName", values[0].trim());
                        jsonProperty.put("description", values[1].trim());
                        jsonProperties.put(jsonProperty);
                    }
                }
            } catch (IOException | JSONException e) {
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\":\"Error reading CSV file\"}");
                return;
            }
        } else {
            response.setStatus(SlingHttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"CSV file not found\"}");
            return;
        }

        response.getWriter().write(jsonProperties.toString());
    }
}