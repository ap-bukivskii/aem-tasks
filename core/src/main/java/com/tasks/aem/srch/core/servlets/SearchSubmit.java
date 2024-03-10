package com.tasks.aem.srch.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import org.json.JSONArray;

import java.io.IOException;

import com.tasks.aem.srch.core.services.ComponentSearchService;
import org.osgi.service.component.annotations.Reference;

@Component(
        service = { Servlet.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=Custom Component Search servlet",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/bin/customtools/search"
        }
)
public class SearchSubmit extends SlingAllMethodsServlet {

    @Reference
    private ComponentSearchService searchService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String rootPath = request.getParameter("searchPath");
        String propertyName = request.getParameter("propName");
        String propertyValue = request.getParameter("propVal");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            JSONArray resultsArray = searchService.searchComponents(request.getResourceResolver(), rootPath, propertyName, propertyValue);
            response.getWriter().write(resultsArray.toString());
        } catch (RepositoryException | JSONException e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}