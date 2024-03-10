package com.tasks.aem.srch.core.services.impl;

import com.day.cq.search.QueryBuilder;
import com.day.cq.search.Query;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.result.SearchResult;
import com.day.cq.search.result.Hit;
import com.tasks.aem.srch.core.services.ComponentDataService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

@Component(service = ComponentDataService.class)
public class ComponentDataServiceImpl implements ComponentDataService {

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    public JSONArray getComponentsData(ResourceResolver resourceResolver) throws JSONException, RepositoryException {
        JSONArray results = new JSONArray();
        Map<String, JSONArray> groupedComponents = new HashMap<>();

        Map<String, String> map = new HashMap<>();
        map.put("type", "cq:Component");
        map.put("group.p.or", "true"); // Group condition to OR the paths
        map.put("group.1_path", "/apps/srch");
        map.put("group.2_path", "/apps/weretail");
        map.put("group.3_path", "/apps/wknd");
        map.put("p.limit", "-1"); // No limit on result count

        Query query = queryBuilder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
        SearchResult result = query.getResult();

        for (Hit hit : result.getHits()) {
            Resource resource = hit.getResource();
            Map<String, Object> properties = resource.getValueMap();
            String groupName = (String) properties.getOrDefault("componentGroup", "Others");
            String componentName = (String) properties.getOrDefault("jcr:title", resource.getName());
            String componentDescription = (String) properties.getOrDefault("jcr:description", "No description available");
            String componentResourceType = resource.getPath();

            JSONObject componentJson = new JSONObject();
            componentJson.put("name", componentName);
            componentJson.put("description", componentDescription);
            componentJson.put("resourceType", componentResourceType);

            groupedComponents.computeIfAbsent(groupName, k -> new JSONArray()).put(componentJson);
        }

        for (Map.Entry<String, JSONArray> entry : groupedComponents.entrySet()) {
            JSONObject groupJson = new JSONObject();
            groupJson.put("groupName", entry.getKey());
            groupJson.put("components", entry.getValue());
            results.put(groupJson);
        }

        return results;
    }
}