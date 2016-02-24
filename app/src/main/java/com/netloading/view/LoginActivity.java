package com.netloading.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.netloading.R;
import com.netloading.common.GenericActivity;
import com.netloading.model.gcm.RegistrationIntentService;
import com.netloading.presenter.LoginPresenter;
import com.netloading.utils.Utils;
import com.netloading.utils.Validator;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends GenericActivity<LoginPresenter.View, LoginPresenter>
        implements LoginPresenter.View {


    @Bind(R.id.username)
    EditText mUsernameEditText;

    @Bind(R.id.password)
    EditText mPasswordEditText;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        super.onCreate(savedInstanceState, LoginPresenter.class, this);

        mProgressDialog = new ProgressDialog(this);
        if (getOps().isProcessing()) {
            showProgressDialog();
        }

//        Intent intent = RegistrationIntentService.makeIntent(this);
//        startService(intent);
//        Intent intent = new Intent(getApplicationContext(), PickLocationActivity.class);
//        startActivity(intent);
    }


    @OnClick(R.id.register_button)
    public void register() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.login_button)
    public void login() {
        // TODO - validate
        if (TextUtils.isEmpty(mPasswordEditText.getText())
                || TextUtils.isEmpty(mUsernameEditText.getText())) {
            Utils.toast(this, "Vui lòng nhập thông tin đăng nhập");
//            return;
        }
        showProgressDialog();
        String username = mUsernameEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        getOps().login(username, password);
    }

    private void showProgressDialog() {
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setTitle("Đang xử lí");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Vui lòng đợi trong giây lát");
        mProgressDialog.show();
    }


    @OnClick(R.id.login_forgot_password)
    public void forgotPassword() {

        // TODO - finish activity


        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public void loginSucceed() {
//        Utils.toast(this, "");

        mProgressDialog.dismiss();

        // TODO - get GCM registration token and send to nodejs server
        Intent intent = RegistrationIntentService.makeIntent(this);
        startService(intent);

        startActivity(PickLocationActivity.makeIntent(this));

    }

    @Override
    public void loginFailure(int status) {

        mProgressDialog.dismiss();

        if (status == NETWORK_ERROR) {
            Utils.toast(this, "Vui lòng kiểm tra đường truyền");
        } else if (status == USERNAME_PASSWORD_ERROR) {
            Utils.toast(this, "Sai mật khẩu hoặc tên đăng nhập");
        }
    }
}