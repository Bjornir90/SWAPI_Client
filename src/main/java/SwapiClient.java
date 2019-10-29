import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.SslConfigurator;

import javax.net.ssl.SSLContext;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class SwapiClient {

    private static Client client;
    private static WebTarget baseTarget;
    private static String baseURL = "https://swapi.co/api";


    private static void getMovies() {
        WebTarget target = baseTarget.path("films/");
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        SwapiResponse response = null;
        try {
            response = builder.get(SwapiResponse.class);
        } catch (RedirectionException e) {
            System.out.println("Redirected to " + e.getLocation().toString());
        }

        System.out.println("Numbers of movies referenced : "+response.getCount());
    }

    private static void getPeopleNames(){
        WebTarget target = baseTarget.path("people/");
        Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
        SwapiResponse response = builder.get(SwapiResponse.class);

        List<String> names = new ArrayList<>();

        while(response.getNext() != null){
            response.getResults().forEach(result -> names.add(result.getName()));

            WebTarget nextTarget = client.target(response.getNext());
            builder = nextTarget.request(MediaType.APPLICATION_JSON);
            response = builder.get(SwapiResponse.class);
        }

        names.forEach(name -> System.out.println("Nom du personnage : "+name));
    }

    public static void main(String[] args) {
        SslConfigurator sslConfig = SslConfigurator.newInstance()
                .trustStoreFile("jssecacerts")
                .trustStorePassword("changeit");

        SSLContext sslContext = sslConfig.createSSLContext();

        client = ClientBuilder
                .newBuilder()
                .sslContext(sslContext)
                .register(JacksonJsonProvider.class)
                .build();

        baseTarget = client.target(baseURL);


        getMovies();
        getPeopleNames();

    }
}
