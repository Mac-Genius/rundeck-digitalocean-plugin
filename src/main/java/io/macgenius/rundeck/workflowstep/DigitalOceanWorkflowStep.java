package io.macgenius.rundeck.workflowstep;

import com.dtolabs.rundeck.core.plugins.configuration.PropertyScope;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.step.StepPlugin;

public abstract class DigitalOceanWorkflowStep implements StepPlugin {
    @PluginProperty(name = "accessToken", title = "Digital Ocean API 2 token",
            description = "Digital Ocean API 2 token",
            required = true, scope = PropertyScope.Project)
    protected String accessToken;
}
