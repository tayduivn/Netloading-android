package com.netloading.presenter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.netloading.common.ConfigurableOps;
import com.netloading.common.ContextView;
import com.netloading.model.pojo.LoginPOJO;
import com.netloading.model.pojo.RegisterPOJO;
import com.netloading.model.webservice.AccountService;
import com.netloading.model.webservice.ServiceGenerator;
import com.netloading.utils.Constants;
import com.netloading.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dandoh on 2/13/16.
 */
public class LoginPresenter implements ConfigurableOps<LoginPresenter.View> {
    private final String TAG = getClass().getSimpleName();
    private AccountService mAccountService;

    private WeakReference<View> mView;
    private boolean processing;

    public boolean isProcessing() {
        return processing;
    }

    private void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public interface View extends ContextView {

        // TODO - status failure
        int NETWORK_ERROR = 3;
        int USERNAME_PASSWORD_ERROR = 1;

        void loginSucceed();

        void loginFailure(int status);
    }


    @Override
    public void onConfiguration(View view, boolean firstTimeIn) {
        mView = new WeakReference<View>(view);
        if (firstTimeIn) {
            mAccountService = ServiceGenerator.getAccountService();
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    mView.get().getApplicationContext()
            );



        } else {

        }

    }

    public void login(String username, String password) {
        setProcessing(true);
        final LoginPOJO loginPOJO = new LoginPOJO("dandoh", "dandoh");


        mAccountService.loginAndSaveToken(loginPOJO).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                setProcessing(false);
                try {

                    JSONObject result = new JSONObject(response.body().string());
                    String status = result.getString("status");
                    //TODO - check message and status
                    if (status.equals("success")) {

                        // Save id and initialize
                        String token = result.getJSONObject("message").getString("token");
                        int id = result.getJSONObject("message").getInt("id");

                        ServiceGenerator.initialize(token, id);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                                mView.get().getApplicationContext()
                        );
                        sharedPreferences.edit().putInt(Constants.SHARED_PREFERENCE_ID_TAG, id)
                                .putString(Constants.SHARED_PREFERENCE_TOKEN_TAG, token)
                                .apply();


                        mView.get().loginSucceed();

                    } else if (result.getString("status").equals("error")){
                        mView.get().loginFailure(View.USERNAME_PASSWORD_ERROR);
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();

                    // TODO - status login
                    mView.get().loginFailure(View.NETWORK_ERROR);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                setProcessing(false);

                mView.get().loginFailure(View.NETWORK_ERROR);
            }
        });
    }


}
