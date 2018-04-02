package io.macgenius.rundeck.workflowstep;

import com.dtolabs.rundeck.core.execution.workflow.steps.StepException;
import com.dtolabs.rundeck.core.execution.workflow.steps.StepFailureReason;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.descriptions.RenderingOption;
import com.dtolabs.rundeck.plugins.descriptions.RenderingOptions;
import com.dtolabs.rundeck.plugins.step.PluginStepContext;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.Droplet;
import com.myjeeva.digitalocean.pojo.Image;
import com.myjeeva.digitalocean.pojo.Key;
import com.myjeeva.digitalocean.pojo.Region;
import lombok.extern.log4j.Log4j;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Creates a new droplet on Digital Ocean.
 *
 * @author John Harrison
 */
@Plugin(name = "digital_ocean_create_droplet", service = ServiceNameConstants.WorkflowStep)
@PluginDescription(title = "Digital Ocean Create Droplet", description = "Creates a new droplet on Digital Ocean")
@Log4j
public class DigitalOceanCreateDroplet extends DigitalOceanWorkflowStep {
    @PluginProperty(name = "name", title = "Droplet Name",
            description = "The human-readable string you wish to use when displaying the Droplet name.",
            required = true)
    private String name;

    @PluginProperty(name = "region", title = "Droplet Region",
            description = "The unique slug identifier for the region that you wish to deploy in.",
            required = true)
    private String region;

    @PluginProperty(name = "size", title = "Droplet Size",
            description = "The unique slug identifier for the size that you wish to select for this Droplet.",
            required = true)
    private String size;

    @PluginProperty(name = "image", title = "Droplet Image",
            description = "The image ID of a public or private image, or the unique slug identifier for a public image.",
            required = true)
    private String image;

    @PluginProperty(name = "sshKeys", title = "SSH Keys",
            description = "A list of comma separated Digital Ocean ssh key IDs.")
    private String sshKeys;

    @PluginProperty(name = "enableBackups", title = "Enable Backups",
            description = "A boolean indicating whether automated backups should be enabled for the Droplet.",
            defaultValue = "false")
    private boolean enableBackups;

    @PluginProperty(name = "enableIpv6", title = "Enable IPv6",
            description = "A boolean indicating whether IPv6 is enabled on the Droplet.", defaultValue = "false")
    private boolean enableIpv6;

    @PluginProperty(name = "enablePrivateNetworking", title = "Enable Private Networking",
            description = "A boolean indicating whether private networking is enabled for the Droplet.",
            defaultValue = "false")
    private boolean enablePrivateNetworking;

    @PluginProperty(name = "enableMonitoring", title = "Enable Monitoring",
            description = "A boolean indicating whether to install the DigitalOcean agent for monitoring.",
            defaultValue = "false")
    private boolean enableMonitoring;

    @PluginProperty(name = "userData", title = "User Data", description = "A string containing 'user data' which " +
            "may be used to configure the Droplet on first boot, often a 'cloud-config' file or Bash script.")
    @RenderingOption(key = "displayType", value = "CODE")
    private String userData;

    @PluginProperty(name = "volumes", title = "Volumes", description = "A flat array including " +
            "the unique string identifier for each Block Storage volume to be attached to the " +
            "Droplet. (Comma separated)")
    @RenderingOption(key = "displayType", value = "MULTI_LINE")
    private String volumes;

    @PluginProperty(name = "tags", title = "Tags", description = "A flat array of tag names as strings to " +
            "apply to the Droplet after it is created. (Comma separated)")
    @RenderingOptions({@RenderingOption(key = "displayType", value = "MULTI_LINE"),
            @RenderingOption(key = "codeSyntaxSelectable", value = "true")})
    private String tags;

    @Override
    public void executeStep(PluginStepContext pluginStepContext, Map<String, Object> map) throws StepException {
        // Something Here
        DigitalOceanClient client = new DigitalOceanClient("v2", accessToken);

        Droplet droplet = new Droplet();
        droplet.setName(name);
        droplet.setImage(new Image(image));
        droplet.setRegion(new Region(region));
        droplet.setSize(size);
        if (sshKeys != null) {
            droplet.setKeys(Arrays.stream(sshKeys.split(","))
                    .map(key -> new Key(Integer.parseInt(key.trim()))).collect(Collectors.toList()));
        }
        droplet.setEnableBackup(enableBackups);
        droplet.setEnableIpv6(enableIpv6);
        droplet.setEnablePrivateNetworking(enablePrivateNetworking);
        droplet.setInstallMonitoring(enableMonitoring);
        if (userData != null) {
            droplet.setUserData(userData);
        }
        if (volumes != null) {
            droplet.setVolumeIds(Arrays.stream(volumes.split(",")).map(String::trim).collect(Collectors.toList()));
        }
        if (tags != null) {
            droplet.setTags(Arrays.stream((tags.split(","))).map(String::trim).collect(Collectors.toList()));
        }

        try {
            Droplet newDroplet = client.createDroplet(droplet);
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
            throw new StepException(e.getMessage(), e, StepFailureReason.IOFailure);
        }
    }
}
