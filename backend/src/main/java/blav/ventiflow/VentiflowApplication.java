package blav.ventiflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class VentiflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(VentiflowApplication.class, args);
	}

}
