package com.tasks.aem.srch.core.services.impl;

import com.tasks.aem.srch.core.services.CommonPropertiesServiceCsv;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import com.day.cq.dam.api.Asset;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Deprecated
@Component(service = CommonPropertiesServiceCsv.class)
public class CommonPropertiesServiceCsvImpl implements CommonPropertiesServiceCsv {

    @Override
    public JSONArray extractProperties(ResourceResolver resourceResolver, String csvFilePath) throws IOException, JSONException {
        JSONArray jsonProperties = new JSONArray();
        Resource csvFileResource = resourceResolver.getResource(csvFilePath);
        Asset csvAsset = null;
        if (csvFileResource != null) {
            csvAsset = csvFileResource.adaptTo(Asset.class);
        }

        if (csvAsset != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvAsset.getRendition("original").getStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length > 1) {
                        JSONObject jsonProperty = new JSONObject();
                        jsonProperty.put("propertyName", values[0].trim());
                        jsonProperty.put("description", values[1].trim());
                        jsonProperties.put(jsonProperty);
                    }
                }
            }
        }
        return jsonProperties;
    }
}