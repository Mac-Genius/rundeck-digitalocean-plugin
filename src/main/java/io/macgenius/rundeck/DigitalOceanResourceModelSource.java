package io.macgenius.rundeck;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.dtolabs.rundeck.core.common.NodeSetImpl;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import com.myjeeva.digitalocean.exception.DigitalOceanException;
import com.myjeeva.digitalocean.exception.RequestUnsuccessfulException;
import com.myjeeva.digitalocean.impl.DigitalOceanClient;
import com.myjeeva.digitalocean.pojo.Droplet;
import com.myjeeva.digitalocean.pojo.Droplets;
import lombok.extern.log4j.Log4j;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Log4j
public class DigitalOceanResourceModelSource implements ResourceModelSource {
    private final String credentials;
    private final int PAGE_SIZE = 20;

    public DigitalOceanResourceModelSource(Properties properties) {
        credentials = properties.getProperty(DigitalOceanResourceModelFactory.CREDENTIALS);
    }


    @Override
    public INodeSet getNodes() throws ResourceModelSourceException {
        DigitalOceanClient client = new DigitalOceanClient("v2", credentials);
        HashMap<String, INodeEntry> entryHashMap = new HashMap<>();
        int page = 1;
        try {
            Droplets droplets = client.getAvailableDroplets(page, PAGE_SIZE);
            List<Droplet> dropletList = droplets.getDroplets();
            while (dropletList.size() > 0) {
                dropletList.forEach(droplet -> {
                    NodeEntryImpl impl = new NodeEntryImpl(droplet.getNetworks().getVersion4Networks().get(0).getIpAddress(), droplet.getName());
                    String[] versionData = droplet.getImage().getName().split(" ");
                    impl.setOsName(droplet.getImage().getDistribution());
                    impl.setOsArch(versionData[1]);
                    impl.setOsVersion(versionData[0]);
                    impl.setUsername("rundeck");
                    impl.setAttribute("ssh-key-storage-path", "keys/ssh/rundeck");
                    entryHashMap.put(droplet.getName(), impl);
                });
                page++;
                droplets = client.getAvailableDroplets(page, PAGE_SIZE);
                dropletList = droplets.getDroplets();
            }
        } catch (DigitalOceanException | RequestUnsuccessfulException e) {
            log.error(e.getMessage(), e);
        }
        return new NodeSetImpl(entryHashMap);
    }

    public void afterPropertiesSet() throws ConfigurationException {
        if (credentials == null) {
            throw new ConfigurationException("Credentials cannot be empty");
        }
    }
}
