package csb

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication

public class Application {

    public static void main( String[] args ) {

        def ctx = SpringApplication.run( Application.class, args )

        println "Hello!"
        ConvertGeojson.translate( '/home/geoneubie/repos/geojsonConverter/src/main/resources/0bb4aec1710521c12ee76289d9440817.geojson' )

    }

}