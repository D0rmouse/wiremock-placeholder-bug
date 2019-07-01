package com.example.wiremockplaceholderbug;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToXml;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static wiremock.com.github.jknack.handlebars.internal.Files.read;

import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.nio.charset.Charset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
public class WiremockPlaceholderBugApplicationTests {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${wiremock.server.port}")
    private String serverPort;

    @Test
    public void contextLoads() throws Exception {
        stubFor(post(urlEqualTo("/test"))
//                .withHeader("Content-Type", matching("text/xml; charset=UTF-8"))
                .willReturn(aResponse().withStatus(200)));

        restTemplate.postForLocation("http://localhost:" + serverPort + "/test", "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                + "<test>this should be ignored!</test>");

        verify(postRequestedFor(urlEqualTo("/test"))
                .withRequestBody(equalToXml(read(new ClassPathResource("expectation.xml").getFile(),
                        Charset.defaultCharset()), true)));
    }

}
