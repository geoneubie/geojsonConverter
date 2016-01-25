package csb

import org.junit.Test

/**
 * Created by geoneubie on 1/19/16.
 */
class GeojsonOldToXyzTest {

    @Test
    void translateTest() {
        assert GeojsonOldToXyz.transform('/home/geoneubie/repos/geojsonConverter/src/main/resources/0bb4aec1710521c12ee76289d9440817.geojson') == 30631
    }
}
