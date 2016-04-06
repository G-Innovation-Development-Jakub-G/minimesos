package com.containersol.minimesos.mesos;

import com.containersol.minimesos.MinimesosException;
import com.containersol.minimesos.cluster.MesosAgent;
import com.containersol.minimesos.cluster.ZooKeeper;
import com.containersol.minimesos.config.MesosAgentConfig;
import org.junit.Test;

public class MesosAgentTest {
    private static final ZooKeeper zooKeeper = new ZooKeeperContainer();

    /**
     * It must be possible to detect wrong image within 30 seconds
     */
    @Test(expected = MinimesosException.class, timeout = 30 * 1000)
    public void testPullingWrongContainer() {
        MesosAgentConfig config = new MesosAgentConfig();
        config.setImageTag("non-existing-one");

        MesosAgentContainer agent = new MesosAgentContainer(zooKeeper, config);
        agent.pullImage();
    }

}
