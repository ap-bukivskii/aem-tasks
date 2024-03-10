package com.tasks.aem.srch.core.services;

import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;

import javax.jcr.RepositoryException;

@Deprecated
public interface ComponentSearchServiceSQL {
    JSONArray searchComponents(ResourceResolver resourceResolver, String rootPath, String propertyName, String propertyValue) throws RepositoryException, JSONException;
}