package com.netloading.model.webservice;

import com.netloading.model.pojo.AcceptTripPOJO;
import com.netloading.model.pojo.GCMTokenPOJO;
import com.netloading.model.pojo.RequestPOJO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Dandoh on 2/15/16.
 */
public interface NetloadingService {

    @GET("/customers/company_info/{id}")
    Call<ResponseBody> getCompanyInfomation(@Path("id") int companyId);

    @POST("/customers/notification")
    Call<ResponseBody> sendRegistrationTokenToServer(@Body GCMTokenPOJO gcmTokenPOJO);


    @POST("/requests")
    Call<ResponseBody> sendRequest(@Body RequestPOJO requestPOJO);

    @POST("/requests/accept")
    Call<ResponseBody> acceptTrip(@Header("customer_id") int customer_id, @Body AcceptTripPOJO acceptTripPOJO);


    @DELETE("/requests/{id}")
    Call<ResponseBody> deleteRequest(@Path("id") int requestId);

    @GET("/requests/retry/{id}")
    Call<ResponseBody> retryRequest(@Path("id") int requestId);



}
