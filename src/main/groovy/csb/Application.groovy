package csb

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import groovy.io.FileType

@SpringBootApplication

public class Application {

    public static void main( String[] args ) {

        def ctx = SpringApplication.run( Application.class, args )
        def path = "/home/geoneubie/repos/geojsonConverter/src/main/resources"

        def currentDir = new File( path )

        def files = []

        currentDir.eachFileMatch(FileType.FILES, ~/.*\.geojson/) {
          files << it.name
        }

        files.each { file ->

            GeojsonOldToXyz.transform("${path}", "${file}" )
            def newFileNm = file - ".geojson"
            def entries = [:]
            entries.BASEFILENM = "${path}/${newFileNm}"
            def metaFile = new File( "${entries.BASEFILENM}_meta.json" )
            entries.JSON = metaFile.text
            def gs = new GeoJsonService()
            gs.transform( entries )

        }


    }

}