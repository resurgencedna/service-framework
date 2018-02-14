package sf.balance.impl;

import sf.balance.LoadBalance;
import sf.ns.Service;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance {

    private static final Random RANDOM = new Random();

    @Override
    public Service select(List<Service> services) {
        int length = services.size();
        return services.get(RANDOM.nextInt(length));
    }
}
