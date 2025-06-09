package zercher.be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ZercherApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZercherApplication.class, args);
    }

}
