package io.macgenius.rundeck;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.*;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceFactory;
import com.dtolabs.rundeck.plugins.ServiceNameConstants;
import com.dtolabs.rundeck.plugins.util.DescriptionBuilder;
import lombok.extern.log4j.Log4j;

import java.util.Collections;
import java.util.Properties;

@Plugin(name = "digitalocean2", service = ServiceNameConstants.ResourceModelSource)
@Log4j
public class DigitalOceanResourceModelFactory implements ResourceModelSourceFactory, Describable {

    public static final String PROVIDER_NAME = "digitalocean2";
    public static final String CREDENTIALS = "accessCredentials";

    @Override
    public ResourceModelSource createResourceModelSource(Properties properties) throws ConfigurationException {
        DigitalOceanResourceModelSource source = new DigitalOceanResourceModelSource(properties);
        source.afterPropertiesSet();
        return source;
    }

    @Override
    public Description getDescription() {
        return DescriptionBuilder.builder()
                .name(PROVIDER_NAME)
                .title("Digital Ocean Resources")
                .description("Displays your Digital Ocean nodes in the node tab.")
                .property(PropertyUtil.string(CREDENTIALS, "Digital Ocean Token",
                        "Digital Ocean token value",
                        true,
                        null,
                        null,
                        PropertyScope.Project,
                        Collections.singletonMap("displayType", StringRenderingConstants.DisplayType.PASSWORD)))
                .build();
    }
}
