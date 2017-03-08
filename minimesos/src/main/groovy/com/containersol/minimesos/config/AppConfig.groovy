package com.containersol.minimesos.config

/**
 * Configuration for a Marathon app. Path is relative to the minimesosFile.
 */
class AppConfig {

    private String marathonJson

    void setMarathonJson(String marathonJson) {
        this.marathonJson = marathonJson
    }

    String getMarathonJson() {
        return marathonJson
    }

}
