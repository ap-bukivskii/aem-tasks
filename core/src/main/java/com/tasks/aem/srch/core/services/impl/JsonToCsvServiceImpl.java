package com.tasks.aem.srch.core.services.impl;

import com.tasks.aem.srch.core.services.JsonToCsvService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;

@Component(service = JsonToCsvService.class)
public class JsonToCsvServiceImpl implements JsonToCsvService {

    @Override
    public String convertJsonToCsv(String jsonString) throws JSONException {
        JSONArray jsonArray = new JSONArray(jsonString);
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Page Path,Component Type\n");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String pagePath = jsonObject.optString("pagePath", "");
            String componentType = jsonObject.optString("componentType", "");
            csvBuilder.append(pagePath).append(",").append(componentType).append("\n");
        }
        return csvBuilder.toString();
    }
}