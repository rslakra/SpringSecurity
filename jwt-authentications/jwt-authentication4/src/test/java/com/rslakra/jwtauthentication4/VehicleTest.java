package com.rslakra.jwtauthentication4;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rslakra.jwtauthentication4.domain.Vehicle;
import org.junit.jupiter.api.Test;

class VehicleTest {

    @Test
    void testVehicle() {
        Vehicle v = Vehicle.builder().name("test").build();
        v.setId(1L);
        assertEquals(1L, v.getId(), "id should be 1L");
        assertEquals("test", v.getName(), "name should be test");

        Vehicle v2 = Vehicle.builder().name("test2").build();
        v2.setId(2L);
        assertEquals(2L, v2.getId(), "id should be 2L");
        assertEquals("test2", v2.getName(), "name should be test2");
    }
}
