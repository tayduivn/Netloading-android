package com.ketnoivantai.companies.models.webservice;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ketnoivantai.companies.models.entities.Company;
import com.ketnoivantai.companies.models.webservice.json.GenericResult;
import com.ketnoivantai.companies.models.webservice.json.LoginResult;
import com.ketnoivantai.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import retrofit2.Response;

import static org.junit.Assert.*;

/**
 * Created by Dandoh on 10/22/16.
 */
public class CompaniesAccountServiceTest {


    CompaniesAccountService mCompaniesAccountService;

    @Before
    public void setUp() throws Exception {
        mCompaniesAccountService = ServiceGenerator.getCompaniesAccountService();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void loginWithExistedAccount() throws Exception {
        Company company = new Company();
        company.setUsername("dandoh");
        company.setPassword("dandoh");
        Response<LoginResult> res = mCompaniesAccountService.login(company).execute();

        assertEquals(res.body().getError(), "");
        assertNotEquals(res.body().getToken(), null);
    }

    @Test
    public void loginWithNonExistedAccount() throws Exception {
        Company company = new Company();
        company.setUsername("asd");
        company.setPassword("213fasf");
        Response<LoginResult> res = mCompaniesAccountService.login(company).execute();

        assertNotEquals(res.body().getError(), "");
    }

    @Test
    public void testJSONOject() throws Exception {
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse("{\"status\":\"fail\",\"message\":\"Wrong username or password!\"}").getAsJsonObject();
        assertTrue(o.has("status"));
    }


}