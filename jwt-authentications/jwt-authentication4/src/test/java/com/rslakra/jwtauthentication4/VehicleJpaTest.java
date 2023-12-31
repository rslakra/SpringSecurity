package com.rslakra.jwtauthentication4;

import static org.assertj.core.api.Assertions.assertThat;
import com.rslakra.jwtauthentication4.domain.Vehicle;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class VehicleJpaTest {

    @Autowired
    private TestEntityManager tem;

    @Test
    public void mapping() {
        Vehicle v = this.tem.persistFlushFind(Vehicle.builder().name("test").build());
        assertThat(v.getName()).isEqualTo("test");
        assertThat(v.getId()).isNotNull();
        assertThat(v.getId()).isGreaterThan(0);
        //assertThat(v.getCreatedDate()).isNotNull();
    }
}
