package com.tasks.aem.srch.core.servlets;

import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONArray;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;
import javax.servlet.Servlet;
import java.io.IOException;

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

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String[] commonProperties = new String[]{
                "jcr:title", "sling:resourceType", "cq:template", "jcr:description",
                "cq:lastModified", "cq:lastModifiedBy", "jcr:created", "jcr:createdBy",
                "cq:dialogPath", "sling:vanityPath", "jcr:primaryType", "sling:alias",
                "cq:allowedTemplates", "cq:designPath", "cq:cssClass", "altText",
                "fileReference", "navigationTitle", "pageTitle", "linkURL"
        };

        JSONArray jsonProperties = new JSONArray();
        for (String property : commonProperties) {
            jsonProperties.put(property);
        }

        response.getWriter().write(jsonProperties.toString());
    }
}