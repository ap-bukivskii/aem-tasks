package com.tasks.aem.srch.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Common Component Properties Configuration", description = "OSGi configuration for Common Component Properties")
public @interface CommonPropertiesConfig {

    @AttributeDefinition(name = "Component Properties", description = "List of component properties in the format propertyName,description")
    String[] componentProperties() default {};
}