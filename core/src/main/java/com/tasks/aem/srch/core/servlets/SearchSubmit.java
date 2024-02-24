package com.tasks.aem.srch.core.servlets;

import org.apache.jackrabbit.util.Text;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.jcr.NodeIterator;
import javax.servlet.Servlet;
import org.apache.sling.api.servlets.HttpConstants;
import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

@Component(
        service = { Servlet.class },
        property = {
                Constants.SERVICE_DESCRIPTION + "=Custom Component Search servlet",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_PATHS + "=/apps/customtools/search"
        }
)
public class SearchSubmit extends SlingAllMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String rootPath = request.getParameter("searchPath");
        String propertyName = request.getParameter("propName");
        String propertyValue = request.getParameter("propVal");
        String escapedPropertyName = Text.escapeIllegalJcrChars(propertyName);
        String escapedRootPath = Text.escapeIllegalJcrChars(rootPath);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray resultsArray = new JSONArray();

        try {
            Session session = request.getResourceResolver().adaptTo(Session.class);
            QueryManager queryManager = session.getWorkspace().getQueryManager();

//            String queryString = "SELECT parent.* FROM [nt:unstructured] AS s " +
//                    "INNER JOIN [cq:PageContent] AS pageContent ON ISCHILDNODE(s, pageContent) " +
//                    "INNER JOIN [cq:Page] AS parent ON ISCHILDNODE(pageContent, parent) " +
//                    "WHERE ISDESCENDANTNODE(parent, '" + rootPath + "') " +
//                    "AND s.[" + propertyName + "] LIKE '" + propertyValue + "'";

            String queryString = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE(s, '" + rootPath + "') AND s.[" + propertyName + "] LIKE '" + propertyValue + "'";

            Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);

            QueryResult result = query.execute();
            NodeIterator nodeIter = result.getNodes();

            while (nodeIter.hasNext()) {
                Node node = nodeIter.nextNode();
                Node parrentPageNode = node;
                Node componentNode = node;
                JSONObject resultObject = new JSONObject();
                String pagePath = "";
                String componentType = "";

                while (parrentPageNode != null && !(parrentPageNode.isNodeType("cq:Page") || parrentPageNode.isNodeType("sling:Folder"))) {
                    parrentPageNode = parrentPageNode.getParent();
                }
                if (parrentPageNode != null) {
                    pagePath = parrentPageNode.getPath();
                }
                while (componentNode != null && !componentNode.hasProperty("sling:resourceType")) {
                    componentNode = componentNode.getParent();
                }
                if (componentNode != null) {
                    componentType = String.valueOf(componentNode.getProperty("sling:resourceType").getString());
                }

                resultObject.put("pagePath", pagePath);
                resultObject.put("componentType", componentType);
                resultsArray.put(resultObject);
            }

            response.getWriter().write(resultsArray.toString());

        } catch (Exception e) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}