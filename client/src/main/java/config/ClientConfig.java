package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import service.BookController;
import service.ClientController;
import service.PurchaseController;

@Configuration
public class ClientConfig {
    @Bean
    RmiProxyFactoryBean rmiClientProxyFactoryBean() {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceInterface(ClientController.class);
        rmiProxyFactoryBean.setServiceUrl("rmi://localhost:1099/ClientController");
        return rmiProxyFactoryBean;
    }

    @Bean
    RmiProxyFactoryBean rmiBookProxyFactoryBean() {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceInterface(BookController.class);
        rmiProxyFactoryBean.setServiceUrl("rmi://localhost:1099/BookController");
        return rmiProxyFactoryBean;
    }

    @Bean
    RmiProxyFactoryBean rmiPurchaseProxyFactoryBean() {
        RmiProxyFactoryBean rmiProxyFactoryBean = new RmiProxyFactoryBean();
        rmiProxyFactoryBean.setServiceInterface(PurchaseController.class);
        rmiProxyFactoryBean.setServiceUrl("rmi://localhost:1099/PurchaseController");
        return rmiProxyFactoryBean;
    }

}
