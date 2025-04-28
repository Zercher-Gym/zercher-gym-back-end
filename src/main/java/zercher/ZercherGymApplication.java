package zercher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class }, scanBasePackages = {"zercher"})
public class ZercherGymApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZercherGymApplication.class, args);
    }

}
