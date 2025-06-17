package DAW.BrainbTestHub;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class BrainbTestHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrainbTestHubApplication.class, args);
	}

	@PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/El_Salvador"));
        System.out.println("Zona horaria configurada: " + TimeZone.getDefault());
    }

}
