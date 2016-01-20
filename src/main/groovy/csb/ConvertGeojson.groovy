package csb

import groovy.json.JsonSlurper
import groovy.json.JsonParserType


/**
 * Created by geoneubie on 1/19/16.
 */
class ConvertGeojson {

    public static int translate( String filename ) {
        def tokens = []
        def pts = []
        def ids = []

        def sb = new StringBuffer( )


        def jsonStr = new File( filename ).text
        def jsonSlurper = new JsonSlurper().setType(JsonParserType.CHARACTER_SOURCE)
        def json = jsonSlurper.parseText( jsonStr )
        def coords = []

        def features = []
        def properties = []
        features = json.features
        properties = json.properties
        println "${properties.size()}:${features.size()}"
        return features.size()
    }

}
