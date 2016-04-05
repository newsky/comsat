/*
 * COMSAT
 * Copyright (c) 2013-2014, Parallel Universe Software Co. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 3.0
 * as published by the Free Software Foundation.
 */
/*
 * Based on the corresponding class in Spring Boot Samples.
 * Copyright the original author Dave Syer.
 * Released under the ASF 2.0 license.
 */
package comsat.sample.actuator.ui;

import java.util.Arrays;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic integration tests for demo application.
 *
 * @author Dave Syer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SampleActuatorUiApplication.class)
@IntegrationTest("server.port=0")
@WebAppConfiguration
@DirtiesContext
public class SampleActuatorUiApplicationTests {

    @Value("${local.server.port}")
    private int port;

    @Test
    public void testHome() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        ResponseEntity<String> entity = new TestRestTemplate().exchange(
                "http://localhost:" + this.port, HttpMethod.GET, new HttpEntity<Void>(
                        headers), String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue("Wrong body (title doesn't match):\n" + entity.getBody(), entity
                .getBody().contains("<title>Hello"));
    }

    @Test
    public void testCss() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/css/bootstrap.min.css", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue("Wrong body:\n" + entity.getBody(), entity.getBody().contains("body"));
    }

    @Test
    public void testMetrics() throws Exception {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/metrics", Map.class);
        assertEquals(HttpStatus.UNAUTHORIZED, entity.getStatusCode());
    }

    @Test
    public void testError() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.TEXT_HTML));
        ResponseEntity<String> entity = new TestRestTemplate().exchange(
            "http://localhost:" + this.port + "/error", HttpMethod.GET,
            new HttpEntity<Void>(headers), String.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(entity.getBody()).contains("<html>").contains("<body>")
            .contains("Please contact the operator with the above information");
    }

}
