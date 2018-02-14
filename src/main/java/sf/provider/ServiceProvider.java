package sf.provider;

import org.springframework.beans.factory.FactoryBean;
import sf.ns.NameService;
import sf.ns.Service;
import sf.ns.ZookeeperNameService;
import sf.provider.handler.ServerHandler;
import sf.util.NetUtil;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceProvider implements FactoryBean {

    private Class<?> serviceInterface;
    private Object serviceImpl;
    private int port;

    private String localIp;
    private String serviceName;
    private Service service;

    private ServerSocket serverSocket = null;

    private volatile boolean stopped = false;

    private static final NameService nameService = new ZookeeperNameService();

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public void serve() {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(localIp, port));
            System.out.println("server has been started! ip:" + localIp + ", port:" + port);
            while (!stopped) {
                Socket socket = serverSocket.accept();
                EXECUTOR.submit(new ServerHandler(socket, serviceName, serviceImpl));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        unRegisterWhenShutdown();
    }

    public void init() {
        localIp = NetUtil.getLocalIp();
        serviceName = serviceInterface.getName();
        service = new Service(serviceName, localIp, port);
        nameService.registerService(service);
    }

    public void destroy() {
        unRegisterService();
    }

    private void unRegisterService() {
        nameService.unRegisterService(service);
    }

    private void unRegisterWhenShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                unRegisterService();
            }
        });
    }

    public Class<?> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public Object getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(Object serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    public Object getObject() throws Exception {
        return this;
    }

    public Class<?> getObjectType() {
        return this.getClass();
    }

    public boolean isSingleton() {
        return true;
    }
}
