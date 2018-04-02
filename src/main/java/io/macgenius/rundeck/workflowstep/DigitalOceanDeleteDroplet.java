package io.macgenius.rundeck.workflowstep;

import com.dtolabs.rundeck.core.execution.workflow.steps.StepException;
import com.dtolabs.rundeck.core.execution.workflow.steps.StepFailureReason;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.step.PluginStepContext;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import lombok.extern.log4j.Log4j;

import java.util.Map;

@Plugin(name = "digital_ocean_delete_droplet", service = ServiceNameConstants.WorkflowStep)
@PluginDescription(title = "Digital Ocean Delete Droplet", description = "Deletes a droplet on Digital Ocean")
@Log4j
public class DigitalOceanDeleteDroplet extends DigitalOceanWorkflowStep {
    @PluginProperty(name = "dropletId", title = "Droplet ID",
            description = "A unique identifier for each Droplet instance.")
    private String dropletId;

    @PluginProperty(name = "dropletTag", title = "Droplet Tag",
            description = "A tag identifying a server(s).")
    private String dropletTag;

    @Override
    public void executeStep(PluginStepContext pluginStepContext, Map<String, Object> map) throws StepException {
        DigitalOceanClient client = new DigitalOceanClient("v2", accessToken);
        try {
            if (dropletId != null && !dropletId.equals("")) {
                client.deleteDroplet(Integer.parseInt(dropletId));
            } else if (dropletTag != null && !dropletTag.equals("")) {
                client.deleteDropletByTagName(dropletTag);
            } else {
                throw new StepException("Droplet ID or Droplet Tag cannot be null!", StepFailureReason.ConfigurationFailure);
            }
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            e.printStackTrace();
            throw new StepException(e.getMessage(), e, StepFailureReason.IOFailure);
        }
    }
}
