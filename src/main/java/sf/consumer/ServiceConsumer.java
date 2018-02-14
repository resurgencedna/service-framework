package sf.consumer;

import org.springframework.beans.factory.FactoryBean;
import sf.balance.LoadBalance;
import sf.balance.impl.RandomLoadBalance;
import sf.consumer.handler.ClientInvocationHandler;
import sf.ns.NameService;
import sf.ns.ZookeeperNameService;

import java.lang.reflect.Proxy;

public class ServiceConsumer implements FactoryBean {

    private Class<?> serviceInterface;

    private Object serviceProxy;

    private static final NameService nameService = new ZookeeperNameService();
    private static final LoadBalance loadBalance = new RandomLoadBalance();

    public void init() {
        serviceProxy = createProxy(serviceInterface);
    }

    public void destroy() {

    }

    private static <T> T createProxy(Class<T> iface) {
        return (T) Proxy.newProxyInstance(iface.getClassLoader(), new Class[]{iface},
                new ClientInvocationHandler(nameService, loadBalance));
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        return serviceProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}