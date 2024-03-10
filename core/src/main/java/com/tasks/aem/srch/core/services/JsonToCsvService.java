package com.tasks.aem.srch.core.services;

import org.json.JSONException;

public interface JsonToCsvService {
    String convertJsonToCsv(String jsonString) throws JSONException;
}