package com.tasks.aem.srch.core.servlets;

import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.framework.Constants;
import javax.servlet.Servlet;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(
        service = { Servlet.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=Components DataSource Servlet",
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/apps/datasource/components",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json"
        }
)
public class ComponentsDataSourceServlet extends SlingAllMethodsServlet {
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONArray results = new JSONArray();
        Map<String, JSONArray> groupedComponents = new HashMap<>();

        try {
            Session session = request.getResourceResolver().adaptTo(Session.class);
            QueryManager queryManager = session.getWorkspace().getQueryManager();
            String queryString = "SELECT * FROM [cq:Component] AS s WHERE ISDESCENDANTNODE(s, '/apps')";
            Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);

            QueryResult result = query.execute();
            NodeIterator nodeIter = result.getNodes();
            while (nodeIter.hasNext()) {
                Node node = nodeIter.nextNode();
                String groupName = node.hasProperty("componentGroup") ? node.getProperty("componentGroup").getString() : "Others";
                String componentName = node.hasProperty("jcr:title") ? node.getProperty("jcr:title").getString() : node.getName();
                String componentDescription = node.hasProperty("jcr:description") ? node.getProperty("jcr:description").getString() : "No description available";
                String componentResourceType = node.getPath();

                JSONObject componentJson = new JSONObject();
                componentJson.put("name", componentName);
                componentJson.put("description", componentDescription);
                componentJson.put("resourceType", componentResourceType);

                groupedComponents.computeIfAbsent(groupName, k -> new JSONArray()).put(componentJson);
            }

            groupedComponents.forEach((group, components) -> {
                JSONObject groupJson = new JSONObject();
                try {
                    groupJson.put("groupName", group);
                    groupJson.put("components", components);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                results.put(groupJson);
            });

            response.getWriter().write(results.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
