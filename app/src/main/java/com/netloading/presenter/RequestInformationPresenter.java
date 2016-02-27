package com.netloading.presenter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.netloading.common.ConfigurableOps;
import com.netloading.common.ContextView;
import com.netloading.model.pojo.CompanyTripPOJO;
import com.netloading.model.webservice.ServiceGenerator;
import com.netloading.utils.NotAuthenticatedException;
import com.netloading.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AnhVu on 2/26/16.
 */
public class RequestInformationPresenter implements ConfigurableOps<RequestInformationPresenter.View>{

    private static final String TAG = "RequestInformationPresenter";
    private WeakReference<View> mView;

    @Override
    public void onConfiguration(View view, boolean firstTimeIn) {

        this.mView = new WeakReference<View>(view);

    }

    public void deleteRequest(int requestId) {
        try {
            ServiceGenerator.getNetloadingService()
                    .deleteRequest(requestId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
    //                processing = false;

                    try {
                        JSONObject result = new JSONObject(response.body().string());

                        Utils.log(TAG, result.toString());

                        if (result.getString("status").equals("success")) {
                            mView.get().onDeleteSuccess();
                        } else {
                            mView.get().onError(View.STATUS_UNHANDLED_ERROR);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();

                        mView.get().onError(View.STATUS_NETWORK_ERROR);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
    //                processing = false;

                    mView.get().onError(View.STATUS_NETWORK_ERROR);
                }
            });
        } catch (NotAuthenticatedException e) {
            e.printStackTrace();
        }
    }

    public void retryRequest(int requestId) {
        try {
            ServiceGenerator.getNetloadingService().retryRequest(requestId).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {

                        JSONObject result = new JSONObject(response.body().string());

                        Utils.log(TAG, result.toString());
                        if (result.getString("status").equals("success")) {

                            // Get company list
                            Gson gson = new Gson();
                            JSONArray companiesArray = result.getJSONObject("message").getJSONArray("trips");
                            Type listType = new TypeToken<ArrayList<CompanyTripPOJO>>() {
                            }.getType();
                            ArrayList<CompanyTripPOJO> companyTripPOJOs = gson.fromJson(companiesArray.toString(), listType);

                            // Get request id
                            Utils.log(TAG, companyTripPOJOs.size() + " ");

                            /// TODO - on result
                            mView.get().onRetrySuccess(companyTripPOJOs);

                        } else {
                            mView.get().onError(View.STATUS_NETWORK_ERROR);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        mView.get().onError(View.STATUS_NETWORK_ERROR);

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mView.get().onError(View.STATUS_NETWORK_ERROR);
                }
            });
        } catch (NotAuthenticatedException e) {
            e.printStackTrace();
        }
    }

    public interface View extends ContextView {

        int STATUS_UNHANDLED_ERROR = 888;
        int STATUS_NETWORK_ERROR = 999;

        void onDeleteSuccess();

        void onError(int status);

        void onRetrySuccess(ArrayList<CompanyTripPOJO> companyTripPOJOs);
    }

}