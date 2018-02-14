package sf.consumer;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sf.iface.ServiceInterface;

public class ConsumerMain {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:service-consumer.xml");
        ServiceInterface proxy = (ServiceInterface) applicationContext.getBean("serviceConsumer");
        String result = proxy.sayHello("xiaohuiyun");
        System.out.println(result);
    }
}
