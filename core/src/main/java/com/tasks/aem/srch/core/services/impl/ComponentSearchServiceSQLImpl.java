package com.tasks.aem.srch.core.services.impl;

import com.tasks.aem.srch.core.services.ComponentSearchServiceSQL;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import java.util.HashSet;
import java.util.Set;

@Deprecated
@Component(service = ComponentSearchServiceSQL.class, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class ComponentSearchServiceSQLImpl implements ComponentSearchServiceSQL {

    @Override
    public JSONArray searchComponents(ResourceResolver resourceResolver, String rootPath, String propertyName, String propertyValue) throws RepositoryException, JSONException {

    JSONArray resultsArray = new JSONArray();
        Set<String> resultsSet = new HashSet<>();
        Session session = resourceResolver.adaptTo(Session.class);
        QueryManager queryManager = session.getWorkspace().getQueryManager();

        String queryString = "SELECT * FROM [nt:unstructured] AS s WHERE ISDESCENDANTNODE(s, '" + rootPath + "') AND s.[" + propertyName + "] LIKE '" + propertyValue + "'";
        Query query = queryManager.createQuery(queryString, Query.JCR_SQL2);

        QueryResult result = query.execute();
        NodeIterator nodeIter = result.getNodes();

        while (nodeIter.hasNext()) {
            Node node = nodeIter.nextNode();
            processNode(resultsSet, resultsArray, node);
        }

        return resultsArray;
    }

    private void processNode(Set<String> resultsSet, JSONArray resultsArray, Node node) throws RepositoryException, JSONException {
        Node parentPageNode = node;
        Node componentNode = node;
        JSONObject resultObject = new JSONObject();
        String pagePath = "";
        String componentType = "";

        while (parentPageNode != null && !(parentPageNode.isNodeType("cq:Page") || parentPageNode.isNodeType("sling:Folder"))) {
            parentPageNode = parentPageNode.getParent();
        }
        if (parentPageNode != null) {
            pagePath = parentPageNode.getPath();
        }
        while (componentNode != null && !componentNode.hasProperty("sling:resourceType")) {
            componentNode = componentNode.getParent();
        }
        if (componentNode != null) {
            componentType = String.valueOf(componentNode.getProperty("sling:resourceType").getString());
        }

        String unique = pagePath + '|' + componentType;
        if (!resultsSet.contains(unique)) {
            resultsSet.add(unique);
            resultObject.put("pagePath", pagePath);
            resultObject.put("componentType", componentType);
            resultsArray.put(resultObject);
        }
    }
}