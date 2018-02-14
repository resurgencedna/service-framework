package sf.balance;

import sf.ns.Service;

import java.util.List;

public interface LoadBalance {

    Service select(List<Service> services);

}
