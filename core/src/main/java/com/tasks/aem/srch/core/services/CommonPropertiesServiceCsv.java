package com.tasks.aem.srch.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

@Deprecated
public interface CommonPropertiesServiceCsv {
    JSONArray extractProperties(ResourceResolver resourceResolver, String csvFilePath) throws IOException, JSONException;
}