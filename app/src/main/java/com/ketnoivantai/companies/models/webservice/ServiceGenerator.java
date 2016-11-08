package com.ketnoivantai.companies.models.webservice;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ketnoivantai.utils.Constants;
import com.ketnoivantai.utils.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.R.attr.id;

/**
 * Created by Dandoh on 2/10/16.
 */
public class ServiceGenerator {


    private static final String TAG = "ServiceGenerator";

    /**
     * LOG all http request through a generic OkHttpClient
     */
    private static OkHttpClient httpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Utils.log("REQUEST", request.method() + ": " +
                                request.url() + " " + request.headers().toString());
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response original = chain.proceed(chain.request());
                        Response copy = original.newBuilder().build();
                        Utils.log("ServiceGenerator", "Response code: " + original.code());
                        if (original.body() != null) {
                            String res = original.body().string();
                            Utils.log("ServiceGenerator", "Response: " + res);

                            JsonParser parser = new JsonParser();
                            JsonObject jsonResponse = parser.parse(res).getAsJsonObject();

                            String status = jsonResponse.get("status").getAsString();
                            // check if status is equal to error
                            if (!status.equals("success")) {
                                String errorMessage = jsonResponse.get("message").getAsString();
                                // alternate key "message" by "error"
                                jsonResponse.addProperty("error", errorMessage);
                                jsonResponse.remove("message");
                                // return error response
                                copy = copy.newBuilder().body(ResponseBody.create(
                                        copy.body().contentType(), jsonResponse.toString().getBytes()
                                )).build();
                            } else {
                                if (jsonResponse.get("message").isJsonArray()) {
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.add("list", jsonResponse.get("message"));
                                    jsonObject.addProperty("error", Constants.NO_ERROR); // err blank

                                    copy = copy.newBuilder().body(ResponseBody.create(
                                            copy.body().contentType(), jsonObject.toString().getBytes()
                                    )).build();
                                } else {
                                    JsonObject actualResult = jsonResponse.get("message").getAsJsonObject();
                                    actualResult.addProperty("error", Constants.NO_ERROR); // err blank
                                    // return successful response
                                    copy = copy.newBuilder().body(ResponseBody.create(
                                            copy.body().contentType(), actualResult.toString().getBytes()
                                    )).build();
                                }
                            }
                        }

                        return copy;

                    }
                })
                .build();

        return okHttpClient;
    }


    /**
     * Generic retrofit builder
     */
    private static Retrofit.Builder builder() {
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());
    }

    private static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, builder(), httpClient());
    }

    private static <S> S createService(Class<S> serviceClass, Retrofit.Builder builder, OkHttpClient httpClient) {
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }


    public static void initialize(String accessToken, int id) {
        setAccessToken(accessToken);
        setId(id);
    }

    private static void setId(int id) {
        ServiceGenerator.id = id;
    }

    private static OkHttpClient getAuthenticatedHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request modifiedRequest = request.newBuilder()
                                .addHeader("Content-Type", "application/json")
                                .addHeader("account_type", "companies")
                                .addHeader("token", getAccessToken())
                                .addHeader("company_id", String.valueOf(getId()))
                                .build();


                        return chain.proceed(modifiedRequest);
                    }
                });

        for (Interceptor interceptor : httpClient().interceptors()) {
            builder.addInterceptor(interceptor);
        }

        return builder.build();
    }


    /**
     * Access token
     */
    private static String accessToken;

    private static String getAccessToken() {
        return accessToken;
    }

    private static int id;

    public static int getId() {
        return id;
    }

    private static void setAccessToken(String accessToken) {
        ServiceGenerator.accessToken = accessToken;
    }


    /**
     * Services
     */

    private static NonAuthenticatedService mNonAuthenticatedService =
            createService(NonAuthenticatedService.class);


    public static NonAuthenticatedService getNonAuthenticatedService() {
        return mNonAuthenticatedService;
    }




    private static AuthenticatedService mAuthenticatedService;

    public static AuthenticatedService getAuthenticatedService() {
        if (mAuthenticatedService == null) {
            mAuthenticatedService = createService(AuthenticatedService.class, builder(), getAuthenticatedHttpClient());
        }

        return mAuthenticatedService;
    }
}