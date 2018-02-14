package sf.provider;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProviderMain {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:service-provider.xml");
        final ServiceProvider provider = (ServiceProvider) applicationContext.getBean("serviceProvider");
        final ServiceProvider provider2 = (ServiceProvider) applicationContext.getBean("serviceProvider2");
        new Thread(new Runnable() {
            @Override
            public void run() {
                provider.serve();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                provider2.serve();
            }
        }).start();
    }
}
