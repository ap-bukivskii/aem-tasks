package com.tasks.aem.srch.core.services.impl;

import com.tasks.aem.srch.core.config.CommonPropertiesConfig;
import com.tasks.aem.srch.core.services.CommonPropertiesService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = CommonPropertiesService.class, immediate = true)
@Designate(ocd = CommonPropertiesConfig.class)
public class CommonPropertiesServiceImpl implements CommonPropertiesService {

    private String[] componentProperties;

    @Override
    public JSONArray extractProperties() throws JSONException {
        JSONArray jsonProperties = new JSONArray();

        for (String property : componentProperties) {
            String[] parts = property.split("=");
            if (parts.length > 1) {
                JSONObject jsonProperty = new JSONObject();
                jsonProperty.put("propertyName", parts[0].trim());
                jsonProperty.put("description", parts[1].trim());
                jsonProperties.put(jsonProperty);
            }
        }
        return jsonProperties;
    }

    @Activate
    @Modified
    protected void activate(CommonPropertiesConfig config) {
        this.componentProperties = config.componentProperties();
    }
}