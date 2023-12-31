package com.rslakra.jwtauthentication4;

import static org.junit.Assert.assertTrue;

import com.rslakra.jwtauthentication4.domain.Vehicle;
import org.junit.Test;

public class VehicleTest {

    @Test
    public void testVehicle() {
        Vehicle v = Vehicle.builder().name("test").build();
        v.setId(1L);
        assertTrue("id is 1L", 1L == v.getId());
        assertTrue("name is test", "test".equals(v.getName()));

        Vehicle v2 = Vehicle.builder().name("test2").build();
        v2.setId(2L);
        assertTrue("id is 2L", 2L == v2.getId());
        assertTrue("name is test2", "test2".equals(v2.getName()));
    }
}
