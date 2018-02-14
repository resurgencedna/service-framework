package sf.provider.handler;

import sf.request.Request;
import sf.response.Response;
import sf.serialize.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class ServerHandler implements Runnable {

    private Socket socket;
    private String serviceName;
    private Object serviceImpl;

    public ServerHandler(Socket socket, String serviceName, Object serviceImpl) {
        this.socket = socket;
        this.serviceName = serviceName;
        this.serviceImpl = serviceImpl;
    }

    @Override
    public void run() {
        try {
            byte[] requestData = receiveRequest();
            Request request = (Request) Serializer.deserialize(requestData);

            Object result = callService(request);

            Response response = genResponse(result);
            byte[] responseData = Serializer.serialize(response);
            writeAndFlushResponse(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] receiveRequest() throws IOException {
        byte[] requestData = new byte[1024];
        int bytesRead = socket.getInputStream().read(requestData);
        return Arrays.copyOf(requestData, bytesRead);
    }

    private Object callService(Request request) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        if (!Objects.equals(serviceName, request.getServiceName())) {
            throw new IllegalArgumentException("service not found:" + request.getServiceName());
        }
        Method method = serviceImpl.getClass().getDeclaredMethod(request.getMethodName(),
                request.getParameterTypes());
        return method.invoke(serviceImpl, request.getArguments());
    }

    private Response genResponse(Object result) {
        return new Response(result);
    }

    private void writeAndFlushResponse(byte[] responseData) throws IOException {
        socket.getOutputStream().write(responseData);
        socket.getOutputStream().flush();
    }

}
