package com.tasks.aem.srch.core.servlets;

import com.tasks.aem.srch.core.services.ComponentDataService;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.framework.Constants;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(
        service = { Servlet.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=Components DataSource Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/datasource/components",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
        }
)
public class ComponentsDataSourceServlet extends SlingAllMethodsServlet {

    @Reference
    private ComponentDataService componentsDataService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            JSONArray results = componentsDataService.getComponentsData(request.getResourceResolver());
            response.getWriter().write(results.toString());
        } catch (RepositoryException | JSONException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}