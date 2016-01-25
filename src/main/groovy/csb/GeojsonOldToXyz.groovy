package csb

import groovy.json.JsonSlurper
import groovy.json.JsonParserType


/**
 * Created by geoneubie on 1/19/16.
 */
class GeojsonOldToXyz {


    public static int transform( String path, String fileNm ) {
        def tokens = []
        def pts = []
        def ids = []

        def sb = new StringBuffer()

        def file = new File( "${path}/${fileNm}" )
        // Add missing json characters to close features list and close json
        //file << "\n        ]\n}"

        def jsonStr = file.text
        def jsonSlurper = new JsonSlurper().setType(JsonParserType.CHARACTER_SOURCE)
        def json = jsonSlurper.parseText( jsonStr )
        def coords = []

        def features = []
        def properties = []
        features = json.features
        properties = json.properties
        println "${properties.size()}:${features.size()}"

        //write out metajson
        def newFileNm = fileNm - ".geojson"
        def metaFile = new File( "${path}/${newFileNm}_meta.json" )
        metaFile.newWriter()
        def draft = new Double (properties.platformDraftMeters)
        metaFile << "{\"shipname\":\"${properties.platformName}\",\"soundermake\":\"${properties.sounderMake}\",\"imonumber\":\"${properties.platformIMONumber}\""
        metaFile << ",\"soundermodel\":\"${properties.sounderModel}\",\"draft\":\"${draft.round(2)}\",\"sounderserialno\":\"${properties.sounderSerialNumber}\",\"longitudinalOffsetFromGPStoSonar\":\"${properties.sounderToGpsLongitudinalOffsetMeters}\""
        metaFile << ",\"lateralOffsetFromGPStoSonar\":\"${properties.sounderToGpsLateralOffsetMeters}\",\"velocity\":\"${properties.'soundSpeed_m/s'}\",\"gpsmake\":\"${properties.gpsMake}\",\"gpsmodel\":\"${properties.gpsModel}\",\"dataProvider\":\"Sea-ID\"}"

        //write out xyz
        def xyzFile = new File( "${path}/${newFileNm}.xyz" )
        xyzFile.newWriter()
        xyzFile << "LAT, LON, DEPTH, TIME\n"
        features.each { f ->
            //println idx + ":" + f.geometry.coordinates
            def lat =  f.geometry.coordinates[0]
            def lon =  f.geometry.coordinates[1]
            def z = f.properties.depthMeters - "\n"
            def t = f.properties.epochtime
            pts << [lat, lon, z, t]
            if ( pts.size() == 1000 ) {
                //write it out

                pts.each { pt ->
                    xyzFile << "${pt[0]}, ${pt[1]}, ${pt[2]}, ${pt[3]}\n"
                }
                pts = []
            }
        }

        // Write out last remaining pts
        pts.each { pt ->
            xyzFile << "${pt[0]}, ${pt[1]}, ${pt[2]}, ${pt[3]}\n"
        }

        return features.size()
    }

}
