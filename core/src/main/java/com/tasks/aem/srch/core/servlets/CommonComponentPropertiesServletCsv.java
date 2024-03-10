package com.tasks.aem.srch.core.servlets;

import com.tasks.aem.srch.core.services.CommonPropertiesServiceCsv;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Deprecated
@Component(
        service = {Servlet.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "=Common Component Properties Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/customtools/commonproperties",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
        }
)
public class CommonComponentPropertiesServletCsv extends SlingAllMethodsServlet {

    @Reference
    private CommonPropertiesServiceCsv commonPropertiesServiceCsv;

    private static final String CSV_FILE_PATH = "/content/dam/srch/commonProperties.csv";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ResourceResolver resolver = request.getResourceResolver();

        try {
            JSONArray jsonProperties = commonPropertiesServiceCsv.extractProperties(resolver, CSV_FILE_PATH);
            response.getWriter().write(jsonProperties.toString());
        } catch (JSONException | IOException e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}