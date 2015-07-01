package org.apache.mesos.mini;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.DockerException;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.*;
import org.apache.log4j.Logger;
import org.junit.rules.ExternalResource;

import java.security.SecureRandom;
import java.util.ArrayList;

public class MesosCluster extends ExternalResource {

    Logger log = Logger.getLogger(MesosCluster.class);

    public String mesosLocalImage = "mesos-local"; // TODO pull that docker image from Dockerhub

    private ArrayList<String> containerNames = new ArrayList<String>();

    final private MesosClusterConfig config;

    public DockerClient dockerClient;

    public CreateContainerResponse createContainerResponse;

    public StartContainerCmd startContainerCmd;

    public MesosCluster(int numberOfSlaves) {
        this(MesosClusterConfig.builder().defaultDockerClient().numberOfSlaves(numberOfSlaves).build());
    }

    public MesosCluster(MesosClusterConfig config) {
        this.config = config;
        this.dockerClient = config.dockerClient;
    }


    public void start() {
        String containerName = "mini_mesos_cluster_" + new SecureRandom().nextInt();
        log.debug("*****************************         Creating container \"" + containerName + "\"         *****************************");

        createContainerResponse = dockerClient.createContainerCmd(mesosLocalImage)
                .withName(containerName)
                .withExposedPorts(ExposedPort.parse("5050"))
                .withPortBindings(PortBinding.parse("0.0.0.0:5050:5050"))
                .withPrivileged(true)
                .withEnv("NUMBER_OF_SLAVES=" + config.numberOfSlaves)
//                .withVolumes(new Volume("/var/lib/docker/aufs:/var/lib/docker/aufs"))
//                .withVolumes(new Volume("/var/lib/docker/btrfs:/var/lib/docker/btrfs"))
//                .withVolumes(new Volume("/var/lib/docker/execdriver:/var/lib/docker/execdriver"))
//                .withVolumes(new Volume("/var/lib/docker/graph:/var/lib/docker/graph"))
//                .withVolumes(new Volume("/var/lib/docker/init:/var/lib/docker/init"))
//                .withVolumes(new Volume("/var/lib/docker/repositories-aufs:/var/lib/docker/repositories-aufs"))
//                .withVolumes(new Volume("/var/lib/docker/tmp:/var/lib/docker/tmp"))
//                .withVolumes(new Volume("/var/lib/docker/trust:/var/lib/docker/trust"))
//                .withVolumes(new Volume("/var/lib/docker/vfs:/var/lib/docker/vfs"))
//                .withVolumes(new Volume("/var/lib/docker/volumes:/var/lib/docker/volumes"))

                .withBinds(Bind.parse("/var/lib/docker/aufs:/var/lib/docker/aufs"))
                .withBinds(Bind.parse("/var/lib/docker/btrfs:/var/lib/docker/btrfs"))
                .withBinds(Bind.parse("/var/lib/docker/execdriver:/var/lib/docker/execdriver"))
                .withBinds(Bind.parse("/var/lib/docker/graph:/var/lib/docker/graph"))
                .withBinds(Bind.parse("/var/lib/docker/init:/var/lib/docker/init"))
                .withBinds(Bind.parse("/var/lib/docker/repositories-aufs:/var/lib/docker/repositories-aufs"))
                .withBinds(Bind.parse("/var/lib/docker/tmp:/var/lib/docker/tmp"))
                .withBinds(Bind.parse("/var/lib/docker/trust:/var/lib/docker/trust"))
                .withBinds(Bind.parse("/var/lib/docker/vfs:/var/lib/docker/vfs"))
                .withBinds(Bind.parse("/var/lib/docker/volumes:/var/lib/docker/volumes"))
                .exec();


        containerNames.add(containerName);

        startContainerCmd = dockerClient.startContainerCmd(createContainerResponse.getId());
        startContainerCmd.exec();
    }

    public void stop() {
        for (String containerName : containerNames) {
            try {
                log.debug("*****************************         Removing container \"" + containerName + "\"         *****************************");

                dockerClient.removeContainerCmd(containerName).withForce().exec();
            } catch (DockerException ignore) {
                ignore.printStackTrace();
            }
        }
    }



    // For usage as JUnit rule...
    @Override
    protected void before() throws Throwable {
        start();
    }

    @Override
    protected void after() {
        stop();
    }


}
