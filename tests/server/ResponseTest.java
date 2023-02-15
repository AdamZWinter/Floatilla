package server;

import floatilla.Floatilla;
import floatilla.FloatillaConfig;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResponseTest {

    @Test
    void addSocket() {
        FloatillaConfig config = new FloatillaConfig("config.json");
        Floatilla floatilla = new Floatilla(config);
        Response response = new Response(config);
        response.addSocket("testHostname01", 443);
        response.addSocket("testHostname02", 443);
        response.addSocket("testHostname03", 443);
        //System.out.println(response);
        //JSONObject jsonObject = new JSONObject(new SimpleSocket("myHost", 42));

        Map<String, Integer> innerMap01= new HashMap<>();
        innerMap01.put("test01", 42);
        innerMap01.put("stuff", 69);
        //JSONObject inner01 = new JSONObject(innerMap01);

        Map<String, Integer> innerMap02= new HashMap<>();
        innerMap02.put("test02", 42);
        innerMap02.put("stuff02", 69);
        //JSONObject inner02 = new JSONObject(innerMap01);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("config", 123456);
        jsonObject.put("size", 42);
        jsonObject.put("map1", innerMap01);
        jsonObject.put("map2", innerMap02);


        System.out.println(jsonObject);
    }

}