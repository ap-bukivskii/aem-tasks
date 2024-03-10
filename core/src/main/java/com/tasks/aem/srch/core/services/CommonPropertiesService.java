package com.tasks.aem.srch.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public interface CommonPropertiesService {
    JSONArray extractProperties() throws JSONException;
}