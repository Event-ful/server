package side.eventful;

import org.springframework.boot.SpringApplication;

public class TestEventfulApplication {

    public static void main(String[] args) {
        SpringApplication.from(EventfulApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
