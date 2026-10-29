package rw.auca.verticalfarmingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VerticalFarmingWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerticalFarmingWebApplication.class, args);
    }
}
