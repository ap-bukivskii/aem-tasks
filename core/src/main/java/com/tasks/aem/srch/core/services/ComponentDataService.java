package com.tasks.aem.srch.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;

import javax.jcr.RepositoryException;

public interface ComponentDataService {
    JSONArray getComponentsData(ResourceResolver resourceResolver) throws RepositoryException, JSONException;
}