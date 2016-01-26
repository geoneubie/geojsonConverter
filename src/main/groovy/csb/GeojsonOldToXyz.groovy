package csb

import groovy.json.JsonSlurper
import groovy.json.JsonParserType

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


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
        // Add missing json characters to close features list and close json;
        // only needs to be done once
        // file << "\n        ]\n}"

        def jsonStr = file.text
        def jsonSlurper = new JsonSlurper().setType(JsonParserType.CHARACTER_SOURCE)
        def json
        try {
            json = jsonSlurper.parseText( jsonStr )
        } catch (Exception e) {
            println "JSON parse failure ${fileNm}"
            file << "\n        ]\n}"
            return 0
        }
        def coords = []

        def features = []
        def properties = []
        features = json.features
        properties = json.properties
        //println "${properties.size()}:${features.size()}"

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
            try {
                def lat = f.geometry.coordinates[0]
                def lon = f.geometry.coordinates[1]
                def z = f.properties.depthMeters - "\n"
                def zDbl = (new Double(z)).round(2)
                String et = f.properties.epochtime
                def t = Long.parseLong(et.trim()) // in secs
                LocalDateTime dt = LocalDateTime.ofEpochSecond(t, 0, ZoneOffset.UTC)
                def formatter = DateTimeFormatter.ISO_DATE_TIME
                String isoDt = dt.format(formatter) + "Z"
                pts << [lat, lon, zDbl, isoDt]
            } catch (Exception e) {
              //Skip that point
            }
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
