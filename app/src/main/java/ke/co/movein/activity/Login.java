package ke.co.movein.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ke.co.movein.R;
import ke.co.movein.utility.Consts;
import ke.co.movein.utility.Functions;
import ke.co.movein.utility.RequestHandler;

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText etUser;
    EditText etPass;
    SwipeRefreshLayout refresh;
    CardView cvForm;

    Functions fx;
    int idx;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fx = new Functions(this);
        sp = PreferenceManager.getDefaultSharedPreferences(Login.this);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        cvForm = (CardView) findViewById(R.id.cv_form);

        etUser = (EditText) findViewById(R.id.et_username);
        etPass = (EditText) findViewById(R.id.et_password);
        etUser.setText(sp.getString("userName",""));
        TextView tvSignup = (TextView) findViewById(R.id.tv_signup);
        Button btnLogin = (Button) findViewById(R.id.btn_login);

        tvSignup.setText(Html.fromHtml("<u>or sign up here</u>"));

        btnLogin.setOnClickListener(this);
        tvSignup.setOnClickListener(this);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });

        idx = getIntent().getIntExtra(Consts.ACTION, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                attemptLogin();
                break;

            case R.id.tv_signup:
                startActivity(new Intent(Login.this, SignUp.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cvForm.getVisibility() != View.VISIBLE)
            cvForm.setVisibility(View.VISIBLE);
    }

    boolean verifyForm(){

        if(etUser.getText().toString().trim().length() == 0){
            etUser.setError("Field cannot be null");
            return false;
        }

        if(etUser.getText().toString().trim().length() != 10){
            Toast.makeText(Login.this, "Use 07XX XXX XXX phone number format", Toast.LENGTH_LONG).show();
            return false;
        }

        if(etPass.getText().toString().trim().length() == 0){
            etPass.setError("Field cannot be null");
            return false;
        }

        return true;
    }

    void hideKeyBoard(){

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if(view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void attemptLogin(){
        if(verifyForm()) {
            hideKeyBoard();
            if(fx.isConnectionAvailable())
                new LoginTask(
                        etUser.getText().toString(), etPass.getText().toString()
                ).execute();
            else
                Toast.makeText(Login.this, getString(R.string.no_net), Toast.LENGTH_SHORT)
                        .show();
        }
    }

    class LoginTask extends AsyncTask<Void, Void, Boolean> {

        JSONObject toUpload = new JSONObject();
        RequestHandler rh = new RequestHandler();
        String message = "Invalid username and/or password!";

        String msisdn;
        String userName;
        String password;

        LoginTask(String user, String pass){
            userName = user;
            msisdn = fx.cleanMSISDN(user);
            password = pass;
            try{
                toUpload.put("msisdn", msisdn);
                toUpload.put("pass", password);
            }catch (JSONException ex){};
        }

        int success = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            cvForm.setVisibility(View.INVISIBLE);
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                String response = rh.postJSON(fx.endPoint("login"), toUpload);

                //Log.w("LOGIN", response);
                try {
                    JSONObject jObt = new JSONObject(response);
                    success = jObt.getInt("success");

                    if (success == 1) {
                        JSONObject userInfo = jObt.getJSONObject("data");
                        MyAccount.userInfo = userInfo;
                        message = "User authenticated!";
                        sp.edit().putInt("userId", userInfo.getInt("id")).apply();
                        sp.edit().putString("userName",userName).apply();
                    }

                } catch (JSONException ex) {
                    ex.getMessage();
                }
            }catch (Exception ex){
                message = ex.getMessage();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (refresh.isRefreshing())
                refresh.setRefreshing(false);

            if(success == 1) {
                etUser.setText("");
                etPass.setText("");
                MainActivity.isLoggedIn = true;
                switch (idx){
                    case Consts.PROCEED_TO_ADDPOST:
                        startActivity(new Intent(Login.this, NewPost.class));
                        finish();
                        break;
                    case Consts.PROCEED_TO_MYACCOUNT:
                        startActivity(new Intent(Login.this, MyAccount.class));
                        finish();
                        break;
                    case Consts.RETURN_TO_POSTDETAILS:
                        finish();
                        break;
                    default:
                        finish();
                }
            }else {
                cvForm.setVisibility(View.VISIBLE);
                Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
            }
        }

    }

}
