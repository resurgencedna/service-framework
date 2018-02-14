package sf.iface.impl;

import sf.iface.ServiceInterface;

public class ServiceImpl implements ServiceInterface {

    @Override
    public String sayHello(String userName) {
        return "Hello, " + userName;
    }
}
