package com.tasks.aem.srch.core.models;

import com.tasks.aem.srch.core.services.ComponentDataService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ComponentsModel {

    @SlingObject
    private Resource resource;

    @OSGiService
    private ComponentDataService componentDataService;

    private List<Group> groups;

    public List<Group> getGroups() throws JSONException, RepositoryException {
        if (groups == null) {
            groups = new ArrayList<>();
            JSONArray groupsJson = componentDataService.getComponentsData(resource.getResourceResolver());
            for (int i = 0; i < groupsJson.length(); i++) {
                JSONObject groupJson = groupsJson.getJSONObject(i);
                Group group = new Group(groupJson.getString("groupName"), new ArrayList<>());

                JSONArray componentsJson = groupJson.getJSONArray("components");
                for (int j = 0; j < componentsJson.length(); j++) {
                    JSONObject componentJson = componentsJson.getJSONObject(j);
                    Component component = new Component(
                            componentJson.getString("name"),
                            componentJson.getString("description"),
                            componentJson.getString("resourceType")
                    );
                    group.components.add(component);
                }
                groups.add(group);
            }
        }
        return groups;
    }
    public static class Group {
        public String name;
        public List<Component> components;

        public Group(String name, List<Component> components) {
            this.name = name;
            this.components = components;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<Component> getComponents() {
            return components;
        }
        public void setComponents(List<Component> components) {
            this.components = components;
        }
    }
    public static class Component {
        public String name;
        public String description;
        public String resourceType;

        public Component(String name, String description, String resourceType) {
            this.name = name;
            this.description = description;
            this.resourceType = resourceType;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getResourceType() {
            return resourceType;
        }
        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }
    }
}