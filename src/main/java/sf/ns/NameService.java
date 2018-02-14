package sf.ns;

import java.util.List;

public interface NameService {

    void registerService(Service service);

    void unRegisterService(Service service);

    List<Service> getServiceList(String serviceName);

}
