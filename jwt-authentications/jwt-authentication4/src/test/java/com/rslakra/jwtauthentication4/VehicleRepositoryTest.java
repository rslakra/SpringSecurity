package com.rslakra.jwtauthentication4;

import static org.assertj.core.api.Assertions.assertThat;

import com.rslakra.jwtauthentication4.domain.Vehicle;
import com.rslakra.jwtauthentication4.repository.VehicleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicles;

    @Test
    public void mapping() {
        Vehicle saved = this.vehicles.save(Vehicle.builder().name("test").build());
        Vehicle v = this.vehicles.getOne(saved.getId());
        assertThat(v.getName()).isEqualTo("test");
        assertThat(v.getId()).isNotNull();
        assertThat(v.getId()).isGreaterThan(0);
    }
}
