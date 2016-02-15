package com.netloading.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.netloading.R;
import com.netloading.common.GenericActivity;
import com.netloading.model.gcm.RegistrationIntentService;
import com.netloading.presenter.LoginPresenter;
import com.netloading.utils.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends GenericActivity<LoginPresenter.View, LoginPresenter>
        implements LoginPresenter.View {


    @Bind(R.id.username)
    EditText mUsernameEditText;

    @Bind(R.id.password)
    EditText mPasswordEditText;

    @Bind(R.id.login_button)
    Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState, LoginPresenter.class, this);

//        Intent intent = RegistrationIntentService.makeIntent(this);
//        startService(intent);

    }


    @OnClick(R.id.register_button)
    public void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.login_button)
    public void login() {
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // TODO - validate
        getOps().login(username, password);
    }

    @Override
    public void loginSucceed() {
        Utils.toast(this, "Login succeed");

        // TODO - get GCM registration token and send to nodejs server
        Intent intent = RegistrationIntentService.makeIntent(this);
        startService(intent);

    }

    @Override
    public void loginFailure(int status) {

    }
}