package com.github.randoapp.db.model;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class RandoFromJsonTest {

    private String readFileFromPath(String fileName) {
        InputStream is = getClass().getResourceAsStream(fileName);
        assertThat(is).isNotNull();

        StringBuilder theString = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                theString.append(sCurrentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertThat(theString).isNotEmpty();
        return theString.toString();
    }

    @Test
    public void shouldReturnNullWhenJsonIsCorrupted(){
        assertThat(Rando.fromJSON("NOT A JSON!!", Rando.Status.IN)).isNull();
    }

    @Test
    public void shouldBuildInRandoFromJsonWithoutDeprecatedAndRating(){

        String json = readFileFromPath("rando_no_detected_no_rating.json");
        assertThat(json).isNotNull().isNotEmpty();
        Rando rando = Rando.fromJSON(json, Rando.Status.IN);

        assertThat(rando).isNotNull();
        assertThat(rando.date).isEqualTo(new Date(1460143246286L));
        assertThat(rando.imageURL).isEqualTo("IMAGE_URL");
        assertThat(rando.mapURL).isEqualTo("MAP_URL");
        assertThat(rando.randoId).isEqualTo("62187b3b661732e3c8f0468f2824aebae04ab6d967");
        assertThat(rando.status).isEqualTo(Rando.Status.IN);
        assertThat(rando.id).isEqualTo(0);
        assertThat(rando.detected).isNull();
        assertThat(rando.rating).isNull();

        assertThat(rando.imageURLSize).isNotNull();
        assertThat(rando.imageURLSize.large).isEqualTo("IMAGE_URL_LARGE");
        assertThat(rando.imageURLSize.medium).isEqualTo("IMAGE_URL_MEDIUM");
        assertThat(rando.imageURLSize.small).isEqualTo("IMAGE_URL_SMALL");

        assertThat(rando.mapURLSize).isNotNull();
        assertThat(rando.mapURLSize.large).isEqualTo("MAP_URL_LARGE");
        assertThat(rando.mapURLSize.medium).isEqualTo("MAP_URL_MEDIUM");
        assertThat(rando.mapURLSize.small).isEqualTo("MAP_URL_SMALL");
    }

    @Test
    public void shouldBuildOutRandoFromJsonWithoutDeprecatedAndRating(){

        String json = readFileFromPath("rando_no_detected_no_rating.json");
        assertThat(json).isNotNull().isNotEmpty();
        Rando rando = Rando.fromJSON(json, Rando.Status.OUT);

        assertThat(rando).isNotNull();
        assertThat(rando.date).isEqualTo(new Date(1460143246286L));
        assertThat(rando.imageURL).isEqualTo("IMAGE_URL");
        assertThat(rando.mapURL).isEqualTo("MAP_URL");
        assertThat(rando.randoId).isEqualTo("62187b3b661732e3c8f0468f2824aebae04ab6d967");
        assertThat(rando.status).isEqualTo(Rando.Status.OUT);
        assertThat(rando.id).isEqualTo(0);
        assertThat(rando.detected).isNull();
        assertThat(rando.rating).isNull();

        assertThat(rando.imageURLSize).isNotNull();
        assertThat(rando.imageURLSize.large).isEqualTo("IMAGE_URL_LARGE");
        assertThat(rando.imageURLSize.medium).isEqualTo("IMAGE_URL_MEDIUM");
        assertThat(rando.imageURLSize.small).isEqualTo("IMAGE_URL_SMALL");

        assertThat(rando.mapURLSize).isNotNull();
        assertThat(rando.mapURLSize.large).isEqualTo("MAP_URL_LARGE");
        assertThat(rando.mapURLSize.medium).isEqualTo("MAP_URL_MEDIUM");
        assertThat(rando.mapURLSize.small).isEqualTo("MAP_URL_SMALL");
    }

    @Test
    public void shouldBuildOutRandoFromJsonWithSingleDetectedAndNoRating(){

        String json = readFileFromPath("rando_detected_single.json");
        assertThat(json).isNotNull().isNotEmpty();
        Rando rando = Rando.fromJSON(json, Rando.Status.OUT);

        assertThat(rando).isNotNull();
        assertThat(rando.date).isEqualTo(new Date(1460143246286L));
        assertThat(rando.imageURL).isEqualTo("IMAGE_URL");
        assertThat(rando.mapURL).isEqualTo("MAP_URL");
        assertThat(rando.randoId).isEqualTo("62187b3b661732e3c8f0468f2824aebae04ab6d967");
        assertThat(rando.status).isEqualTo(Rando.Status.OUT);
        assertThat(rando.id).isEqualTo(0);
        assertThat(rando.rating).isNull();
        assertThat(rando.detected).isEqualTo("\"unwanted\"");

        assertThat(rando.imageURLSize).isNotNull();
        assertThat(rando.imageURLSize.large).isEqualTo("IMAGE_URL_LARGE");
        assertThat(rando.imageURLSize.medium).isEqualTo("IMAGE_URL_MEDIUM");
        assertThat(rando.imageURLSize.small).isEqualTo("IMAGE_URL_SMALL");

        assertThat(rando.mapURLSize).isNotNull();
        assertThat(rando.mapURLSize.large).isEqualTo("MAP_URL_LARGE");
        assertThat(rando.mapURLSize.medium).isEqualTo("MAP_URL_MEDIUM");
        assertThat(rando.mapURLSize.small).isEqualTo("MAP_URL_SMALL");
    }

    @Test
    public void shouldBuildOutRandoFromJsonWithDetectedListAndNoRating(){

        String json = readFileFromPath("rando_detected_list.json");
        assertThat(json).isNotNull().isNotEmpty();
        Rando rando = Rando.fromJSON(json, Rando.Status.OUT);

        assertThat(rando).isNotNull();
        assertThat(rando.date).isEqualTo(new Date(1460143246286L));
        assertThat(rando.imageURL).isEqualTo("IMAGE_URL");
        assertThat(rando.mapURL).isEqualTo("MAP_URL");
        assertThat(rando.randoId).isEqualTo("62187b3b661732e3c8f0468f2824aebae04ab6d967");
        assertThat(rando.status).isEqualTo(Rando.Status.OUT);
        assertThat(rando.id).isEqualTo(0);
        assertThat(rando.rating).isNull();
        assertThat(rando.detected).isEqualTo("\"unwanted\",\"blabla\"");

        assertThat(rando.imageURLSize).isNotNull();
        assertThat(rando.imageURLSize.large).isEqualTo("IMAGE_URL_LARGE");
        assertThat(rando.imageURLSize.medium).isEqualTo("IMAGE_URL_MEDIUM");
        assertThat(rando.imageURLSize.small).isEqualTo("IMAGE_URL_SMALL");

        assertThat(rando.mapURLSize).isNotNull();
        assertThat(rando.mapURLSize.large).isEqualTo("MAP_URL_LARGE");
        assertThat(rando.mapURLSize.medium).isEqualTo("MAP_URL_MEDIUM");
        assertThat(rando.mapURLSize.small).isEqualTo("MAP_URL_SMALL");
    }

    @Test
    public void shouldBuildOutRandoFromJsonWithDetectedListIsEmpty(){

        String json = readFileFromPath("rando_detected_empty.json");
        assertThat(json).isNotNull().isNotEmpty();
        Rando rando = Rando.fromJSON(json, Rando.Status.OUT);

        assertThat(rando).isNotNull();
        assertThat(rando.date).isEqualTo(new Date(1460143246286L));
        assertThat(rando.imageURL).isEqualTo("IMAGE_URL");
        assertThat(rando.mapURL).isEqualTo("MAP_URL");
        assertThat(rando.randoId).isEqualTo("62187b3b661732e3c8f0468f2824aebae04ab6d967");
        assertThat(rando.status).isEqualTo(Rando.Status.OUT);
        assertThat(rando.id).isEqualTo(0);
        assertThat(rando.rating).isNull();
        assertThat(rando.detected).isEqualTo("");

        assertThat(rando.imageURLSize).isNotNull();
        assertThat(rando.imageURLSize.large).isEqualTo("IMAGE_URL_LARGE");
        assertThat(rando.imageURLSize.medium).isEqualTo("IMAGE_URL_MEDIUM");
        assertThat(rando.imageURLSize.small).isEqualTo("IMAGE_URL_SMALL");

        assertThat(rando.mapURLSize).isNotNull();
        assertThat(rando.mapURLSize.large).isEqualTo("MAP_URL_LARGE");
        assertThat(rando.mapURLSize.medium).isEqualTo("MAP_URL_MEDIUM");
        assertThat(rando.mapURLSize.small).isEqualTo("MAP_URL_SMALL");
    }

    @Test
    public void shouldBuildOutRandoFromJsonWithRating0(){

        String json = readFileFromPath("rando_rating_0.json");
        assertThat(json).isNotNull().isNotEmpty();
        Rando rando = Rando.fromJSON(json, Rando.Status.OUT);

        assertThat(rando).isNotNull();
        assertThat(rando.date).isEqualTo(new Date(1460143246286L));
        assertThat(rando.imageURL).isEqualTo("IMAGE_URL");
        assertThat(rando.mapURL).isEqualTo("MAP_URL");
        assertThat(rando.randoId).isEqualTo("62187b3b661732e3c8f0468f2824aebae04ab6d967");
        assertThat(rando.status).isEqualTo(Rando.Status.OUT);
        assertThat(rando.id).isEqualTo(0);
        assertThat(rando.rating).isEqualTo(0);
        assertThat(rando.detected).isEqualTo("");

        assertThat(rando.imageURLSize).isNotNull();
        assertThat(rando.imageURLSize.large).isEqualTo("IMAGE_URL_LARGE");
        assertThat(rando.imageURLSize.medium).isEqualTo("IMAGE_URL_MEDIUM");
        assertThat(rando.imageURLSize.small).isEqualTo("IMAGE_URL_SMALL");

        assertThat(rando.mapURLSize).isNotNull();
        assertThat(rando.mapURLSize.large).isEqualTo("MAP_URL_LARGE");
        assertThat(rando.mapURLSize.medium).isEqualTo("MAP_URL_MEDIUM");
        assertThat(rando.mapURLSize.small).isEqualTo("MAP_URL_SMALL");
    }

    @Test
    public void shouldBuildOutRandoFromJsonWithRating2(){

        String json = readFileFromPath("rando_rating_2.json");
        assertThat(json).isNotNull().isNotEmpty();
        Rando rando = Rando.fromJSON(json, Rando.Status.OUT);

        assertThat(rando).isNotNull();
        assertThat(rando.date).isEqualTo(new Date(1460143246286L));
        assertThat(rando.imageURL).isEqualTo("IMAGE_URL");
        assertThat(rando.mapURL).isEqualTo("MAP_URL");
        assertThat(rando.randoId).isEqualTo("62187b3b661732e3c8f0468f2824aebae04ab6d967");
        assertThat(rando.status).isEqualTo(Rando.Status.OUT);
        assertThat(rando.id).isEqualTo(0);
        assertThat(rando.rating).isEqualTo(2);
        assertThat(rando.detected).isEqualTo("");

        assertThat(rando.imageURLSize).isNotNull();
        assertThat(rando.imageURLSize.large).isEqualTo("IMAGE_URL_LARGE");
        assertThat(rando.imageURLSize.medium).isEqualTo("IMAGE_URL_MEDIUM");
        assertThat(rando.imageURLSize.small).isEqualTo("IMAGE_URL_SMALL");

        assertThat(rando.mapURLSize).isNotNull();
        assertThat(rando.mapURLSize.large).isEqualTo("MAP_URL_LARGE");
        assertThat(rando.mapURLSize.medium).isEqualTo("MAP_URL_MEDIUM");
        assertThat(rando.mapURLSize.small).isEqualTo("MAP_URL_SMALL");
    }
}
