package floatilla;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FloatillaConfigTest {

    @Test
    void getMyHostname() {
        FloatillaConfig config = new FloatillaConfig("config.json");
        assertEquals("localhost", config.getMyHostname());
        assertEquals(8090, config.getListeningPort());
    }

    @Test
    void getListeningPort() {
    }

    @Test
    void testGetMyHostname() {
    }

    @Test
    void setMyHostname() {
    }

    @Test
    void testGetListeningPort() {
    }

    @Test
    void setListeningPort() {
    }

    @Test
    void getPeerCountLimit() {
    }

    @Test
    void setPeerCountLimit() {
    }

    @Test
    void getSeeds() {
        FloatillaConfig config = new FloatillaConfig("config.json");
        System.out.println(config.getSeeds());
    }

    @Test
    void setSeeds() {
    }

    @Test
    void useSecureConnections() {
    }

    @Test
    void getRootCertAuthorities() {
        FloatillaConfig config = new FloatillaConfig("config.json");
        System.out.println(config.getRootCertAuthorities());
    }
}