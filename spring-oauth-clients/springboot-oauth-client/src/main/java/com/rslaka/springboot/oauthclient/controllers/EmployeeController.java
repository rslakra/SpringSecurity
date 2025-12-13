package com.rslaka.springboot.oauthclient.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rslaka.springboot.oauthclient.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class EmployeeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

    @Value("${authServiceBaseUrl}")
    private String authServiceBaseUrl;

    @Value("${clientBaseUrl}")
    private String clientBaseUrl;

    @Value("${oauth.clientId}")
    private String oauthClientId;

    @Value("${oauth.redirectUri}")
    private String oauthRedirectUri;

    @Value("${oauth.scope}")
    private String oauthScope;

    /**
     * Home page - API Explorer.
     *
     * @return ModelAndView for index page
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home() {
        LOGGER.debug("home()");
        ModelAndView model = new ModelAndView("index");
        addOAuthConfig(model);
        return model;
    }

    /**
     * Get employees form page.
     *
     * @return ModelAndView for getEmployees page
     */
    @RequestMapping(value = "/getEmployees", method = RequestMethod.GET)
    public ModelAndView getEmployeeInfo() {
        LOGGER.debug("getEmployeeInfo()");
        ModelAndView model = new ModelAndView("getEmployees");
        addOAuthConfig(model);
        return model;
    }

    /**
     * Show employees page.
     *
     * @param code OAuth authorization code (optional)
     * @return ModelAndView for showEmployees page
     * @throws JsonProcessingException if JSON parsing fails
     * @throws IOException if network error occurs
     */
    @RequestMapping(value = "/showEmployees", method = RequestMethod.GET)
    public ModelAndView showEmployees(@RequestParam(value = "code", required = false) String code)
        throws JsonProcessingException, IOException {
        LOGGER.debug("+showEmployees({})", code);
        ResponseEntity<String> response = null;
        List<Employee> employees = null;

        if (Objects.isNull(code)) {
            employees = new ArrayList<>();
            employees.add(new Employee(UUID.randomUUID().toString(), "Roh Lak"));
            employees.add(new Employee(UUID.randomUUID().toString(), "Roh Sin"));
            employees.add(new Employee(UUID.randomUUID().toString(), "RS Lak"));
        } else {
            LOGGER.debug("Authorization Code: {}", code);
            RestTemplate restTemplate = new RestTemplate();
            String credentials = oauthClientId + ":secret";
            String encodedCredentials = new String(Base64.getEncoder().encode(credentials.getBytes()));
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.add("Authorization", "Basic " + encodedCredentials);
            HttpEntity<String> request = new HttpEntity<String>(headers);
            // access-token-url
            StringBuilder urlBuilder = new StringBuilder(authServiceBaseUrl);
            urlBuilder.append("/oauth/token?code=").append(code);
            urlBuilder.append("&grant_type=authorization_code");
            urlBuilder.append("&redirect_uri=").append(oauthRedirectUri);
            LOGGER.debug("urlBuilder:{}", urlBuilder);
            response = restTemplate.exchange(urlBuilder.toString(), HttpMethod.POST, request, String.class);
            LOGGER.debug("response.body:{}", response.getBody());

            // Get the Access Token From the received JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.getBody());
            String token = node.path("access_token").asText();

            // Use the access token for authentication
            urlBuilder = new StringBuilder(authServiceBaseUrl);
            urlBuilder.append("/user/getEmployeesList");
            LOGGER.debug("urlBuilder:{}", urlBuilder);
            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.add("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(reqHeaders);
            ResponseEntity<Employee[]>
                responseEntity =
                restTemplate.exchange(urlBuilder.toString(), HttpMethod.GET, entity, Employee[].class);
            LOGGER.debug("responseEntity:{}", responseEntity);
            employees = Arrays.asList(responseEntity.getBody());
        }

        LOGGER.debug("employees:{}", employees);
        ModelAndView model = new ModelAndView("showEmployees");
        model.addObject("employees", employees);
        addOAuthConfig(model);
        LOGGER.debug("-showEmployees(), model: {}", model);
        return model;
    }

    /**
     * Adds OAuth configuration to the model for use in JSP views.
     *
     * @param model the ModelAndView to add configuration to
     */
    private void addOAuthConfig(ModelAndView model) {
        model.addObject("authServiceBaseUrl", authServiceBaseUrl);
        model.addObject("clientBaseUrl", clientBaseUrl);
        model.addObject("oauthClientId", oauthClientId);
        model.addObject("oauthRedirectUri", oauthRedirectUri);
        model.addObject("oauthScope", oauthScope);
    }
}
