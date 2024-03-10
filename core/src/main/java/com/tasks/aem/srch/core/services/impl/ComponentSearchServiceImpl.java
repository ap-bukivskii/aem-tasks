package com.tasks.aem.srch.core.services.impl;

import com.day.cq.search.QueryBuilder;
import com.day.cq.search.Query;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.result.SearchResult;
import com.day.cq.search.result.Hit;
import com.tasks.aem.srch.core.services.ComponentSearchService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

@Component(service = ComponentSearchService.class)
public class ComponentSearchServiceImpl implements ComponentSearchService {

    @Override
    public JSONArray searchComponents(ResourceResolver resourceResolver, String rootPath, String propertyName, String propertyValue) throws RepositoryException, JSONException {
        JSONArray resultsArray = new JSONArray();
        Set<String> resultsSet = new HashSet<>();
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("path", rootPath);
        queryMap.put("type", "nt:unstructured");
        queryMap.put("property", propertyName);
        queryMap.put("property.value", propertyValue);
        queryMap.put("p.limit", "-1");

        QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        Query query;

        if (queryBuilder != null) {
            query = queryBuilder.createQuery(PredicateGroup.create(queryMap), resourceResolver.adaptTo(Session.class));
            SearchResult result = query.getResult();

            for (Hit hit : result.getHits()) {
                Resource resource = hit.getResource();
                processResource(resultsSet, resultsArray, resource);
            }
        }

        return resultsArray;
    }

    private void processResource(Set<String> resultsSet, JSONArray resultsArray, Resource resource) throws JSONException {
        Resource pageResource = findParentPageResource(resource);
        String pagePath = pageResource != null ? pageResource.getPath() : "";
        String componentType = findParentResourceType(resource);

        String unique = pagePath + '|' + componentType;
        if (!resultsSet.contains(unique)) {
            resultsSet.add(unique);
            JSONObject resultObject = new JSONObject();
            resultObject.put("pagePath", pagePath);
            resultObject.put("componentType", componentType);
            resultsArray.put(resultObject);
        }
    }

    private Resource findParentPageResource(Resource resource) {
        Resource current = resource;
        while (current != null && !current.isResourceType("cq:Page") && !current.isResourceType("sling:Folder")) {
            current = current.getParent();
        }
        return current;
    }
    private String findParentResourceType(Resource resource) {
        Resource current = resource;
        String resourceType = resource.getValueMap().get("sling:resourceType", String.class);
        while (current != null && resourceType == null) {
            current = current.getParent();
            resourceType = current == null ? "" : current.getValueMap().get("sling:resourceType", String.class);
        }
        return resourceType;
    }
}