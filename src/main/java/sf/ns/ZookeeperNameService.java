package sf.ns;

import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ZookeeperNameService implements NameService {

    private ZkClient zkClient;

    private static final String CLIENT_HOST = "localhost";
    private static final int CLIENT_PORT = 2181;
    private static final int CONNECTION_TIMEOUT = 5000;

    public ZookeeperNameService() {
        zkClient = new ZkClient(CLIENT_HOST + ":" + CLIENT_PORT, CONNECTION_TIMEOUT);
    }

    @Override
    public void registerService(Service service) {
        zkClient.createPersistent("/ns/" + service.getServiceName() + "/provider/ips/" + genIpPort(service), true);
    }

    @Override
    public void unRegisterService(Service service) {
        zkClient.delete("/ns/" + service.getServiceName() + "/provider/ips/" + genIpPort(service));
    }

    @Override
    public List<Service> getServiceList(String serviceName) {
        List<String> providerIps = zkClient.getChildren("/ns/" + serviceName + "/provider/ips");
        return genProviderServices(serviceName, providerIps);
    }

    private String genIpPort(Service service) {
        return service.getIp() + ":" + service.getPort();
    }

    public List<Service> genProviderServices(String serviceName, Collection<String> ips) {
        if (ips == null || ips.isEmpty()) {
            return Collections.emptyList();
        }
        List<Service> services = new ArrayList<>();
        for (String ip : ips) {
            String[] splitedIp = ip.split(":");
            Service service = new Service(serviceName, splitedIp[0], Integer.parseInt(splitedIp[1]));
            services.add(service);
        }
        return services;
    }
}
