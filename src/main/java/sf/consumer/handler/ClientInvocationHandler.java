package sf.consumer.handler;

import sf.balance.LoadBalance;
import sf.ns.NameService;
import sf.ns.Service;
import sf.request.Request;
import sf.response.Response;
import sf.serialize.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ClientInvocationHandler implements InvocationHandler {

    private NameService nameService;
    private LoadBalance loadBalance;

    public ClientInvocationHandler(NameService nameService, LoadBalance loadBalance) {
        this.nameService = nameService;
        this.loadBalance = loadBalance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            Service provider = selectProvider(method.getDeclaringClass().getName());
            Socket socket = genSocket(provider);

            Request request = genRequest(method, args);
            byte[] requestData = Serializer.serialize(request);
            writeAndFlushRequest(socket.getOutputStream(), requestData);

            byte[] responseData = receiveResponse(socket.getInputStream());
            Response response = (Response) Serializer.deserialize(responseData);
            return getResult(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Service selectProvider(String serviceName) {
        List<Service> providers = nameService.getServiceList(serviceName);
        return loadBalance.select(providers);
    }

    private Socket genSocket(Service provider) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(provider.getIp(), provider.getPort()));
        System.out.println("connect server, ip:" + provider.getIp() + ", port:" + provider.getPort());
        return socket;
    }

    private Request genRequest(Method method, Object[] arguments) {
        Request request = new Request();
        request.setServiceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setArguments(arguments);
        return request;
    }

    private void writeAndFlushRequest(OutputStream outputStream, byte[] requestData) throws IOException {
        outputStream.write(requestData);
        outputStream.flush();
    }

    private byte[] receiveResponse(InputStream inputStream) throws IOException {
        byte[] responseData = new byte[1024];
        int bytesRead = inputStream.read(responseData);
        return Arrays.copyOf(responseData, bytesRead);
    }

    private Object getResult(Response response) {
        return response.getData();
    }

}
