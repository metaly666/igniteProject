package testIgnite;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class App {

  public static void main(String[] args) {
    System.setProperty("IGNITE_QUIET", "false");
    System.setProperty("java.net.preferIPv4Stack", "true");

    SpringApplication.run(App.class, args);
  }

}
