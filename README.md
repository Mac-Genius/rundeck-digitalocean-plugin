# Rundeck Digital Ocean Plugin

## Description

This is a plugin allows you to create, delete, and view your Digital Ocean droplets from Rundeck.

## Configuration

### Installation

Clone this repo and run `make`. Then copy `target/rundeck-digitalocean-plugin-x.x.x.jar` into the `libext` folder
in your Rundeck folder.

### Resource Model Configuration

Ensure you have a valid Digital Ocean APIv2 token. Then go to `Project > Edit Nodes... > Configure Nodes... >
Add Source` on the Rundeck project you want to configure and select `Digital Ocean Resources` and enter your token. Hit
save and you are good to go!

### Workflow Step Configuration

There are workflow steps that can either be configured for each project or for Rundeck as a whole. To configure,
copy the following and place in your project/framework configuration file:

```
project.plugin.WorkflowStep.digital_ocean_create_droplet.accessToken=yourTokenHere
project.plugin.WorkflowStep.digital_ocean_delete_droplet.accessToken=yourTokenHere
```

This will provide the authentication to Digital Ocean for creating and deleting droplets.

*Note: Once you save the configuration, the token values will be hidden.* 
