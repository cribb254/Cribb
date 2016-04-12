package ke.co.movein.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ke.co.movein.R;
import ke.co.movein.utility.Functions;
import ke.co.movein.utility.RequestHandler;


/**
 * Created by ekim on 2016/01/21.
 */
public class SignUp extends AppCompatActivity {

    SwipeRefreshLayout refresh;
    LinearLayout llPass;
    RequestHandler rh = new RequestHandler();
    Functions fx;
    //EditText etLname;
    EditText etFname,etPhone,etAddress,etOldPass,etPass,etPassConfirm;

    Spinner spGender;
    CheckBox chb;
    CardView cvPersonal;
    TextInputLayout tilOldPass;

    SharedPreferences sp;
    String TAG;
    boolean isEdit;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TAG = getClass().getSimpleName();

        fx = new Functions(this);
        sp =  PreferenceManager.getDefaultSharedPreferences(this);

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        //llHolder = (LinearLayout) findViewById(R.id.ll_holder);
        llPass = (LinearLayout) findViewById(R.id.ll_pass);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        chb = (CheckBox) findViewById(R.id.chb);
        etFname = (EditText) findViewById(R.id.et_fname);
        //etLname = (EditText) findViewById(R.id.et_lname);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etAddress = (EditText) findViewById(R.id.et_address);
        etOldPass = (EditText) findViewById(R.id.et_old_pass);
        etPass = (EditText) findViewById(R.id.et_pass);
        etPassConfirm = (EditText) findViewById(R.id.et_pass_confirm);
        TextInputLayout tilPass = (TextInputLayout) findViewById(R.id.til_pass);
        tilOldPass = (TextInputLayout) findViewById(R.id.til_old_pass);
        spGender = (Spinner) findViewById(R.id.sp_gender);
        cvPersonal = (CardView) findViewById(R.id.cv_personal);

        chb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                llPass.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                tilOldPass.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        try{
            isEdit = getIntent().getBooleanExtra("isEdit", false);
            etFname.setText(MyAccount.userInfo.getString("fname"));
            //etLname.setText(MyAccount.userInfo.getString("lname"));
            etPhone.setText(MyAccount.userInfo.getString("msisdn"));
            etAddress.setText(MyAccount.userInfo.getString("location"));
            spGender.setSelection(Integer.parseInt(MyAccount.userInfo.getString("gender")));
            etPhone.setFocusable(false); etPhone.setClickable(false);
            chb.setVisibility(View.VISIBLE);
            llPass.setVisibility(View.GONE);
            tilPass.setHint("new password");
        }catch (Exception ex){
            isEdit = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_tick, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_tick:
                /*
                if(sp.getBoolean("regStarted", false))
                    verifyUser();
                else
                */
                    register();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    void hideKeyBoard(){

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if(view != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    boolean validatePersonalForm(){


        if(fx.isEditTextBlank(etFname)){
            etFname.setError("Field cannot be null");
            return false;
        }

        if(!fx.isCleanString(etFname)){
            Toast.makeText(this, "Name must not contain digit or special character", Toast.LENGTH_SHORT).show();
            return false;
        }
/*
        if(fx.isEditTextBlank(etLname)){
            etLname.setError("Field cannot be null");
            return false;
        }

        if(!fx.isCleanString(etLname)){
            Toast.makeText(this, "Name must not contain digit or special character", Toast.LENGTH_SHORT).show();
            return false;
        }
*/
        if(spGender.getSelectedItemPosition() == 0){
            Toast.makeText(SignUp.this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(fx.isEditTextBlank(etAddress)){
            etAddress.setError("Field cannot be null");
            return false;
        }

        //TODO better method to check number validity
        if(!isEdit && etPhone.getText().toString().trim().length() != 10){
            Toast.makeText(this, "Use 07XX XXX XXX phone number format", Toast.LENGTH_LONG).show();
            return false;
        }

        if(chb.isChecked() && fx.isEditTextBlank(etOldPass)){
            etOldPass.setError("Field cannot be null");
            return false;
        }

        if(chb.isChecked() || !isEdit) {
            if (etPass.getText().toString().length() < 8) {
                Toast.makeText(SignUp.this, "Password too short! Must be 8-32 characters long", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!(etPass.getText().toString().matches(etPassConfirm.getText().toString()))) {
                Toast.makeText(SignUp.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }
/*
    boolean validateVerificationForm(){

        if(fx.isEditTextBlank(etVcode)){
            etVcode.setError("Field cannot be null");
            return false;
        }

        if(fx.isEditTextBlank(etPass)){
            etPass.setError("Field cannot be null");
            return false;
        }

        if(fx.isEditTextBlank(etPassConfirm)){
            etPassConfirm.setError("Field cannot be null");
            return false;
        }

        if(etVcode.getText().toString().trim().length() != 6){
            Toast.makeText(SignUp.this, "Invalid verification code!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(etPass.getText().toString().length() < 8){
            Toast.makeText(SignUp.this, "Password too short! Must be 8-32 characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(etPass.getText().toString().matches(etPassConfirm.getText().toString()))){
            Toast.makeText(SignUp.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
*/
    void register(){
        //Log.w("ValidateForm", String.valueOf(validatePersonalForm()));
        if(validatePersonalForm()) {
            hideKeyBoard();
            if(fx.isConnectionAvailable())
                new PostPersonalDetails().execute();
            else
                Toast.makeText(SignUp.this, getString(R.string.no_net),
                        Toast.LENGTH_SHORT).show();
        }
    }
/*
    void verifyUser(){
        if(validateVerificationForm()) {
            hideKeyBoard();
            if(fx.isConnectionAvailable())
                new PostVerificationDetails().execute();
            else
                Toast.makeText(SignUp.this, getString(R.string.no_net),
                        Toast.LENGTH_SHORT).show();
        }
    }
*/
    protected JSONObject getParams(){
        JSONObject toUpload = new JSONObject();

        try {
            toUpload.put("fname", etFname.getText().toString());
            //toUpload.put("lname", etLname.getText().toString());
            toUpload.put("msisdn", isEdit ? etPhone.getText().toString()
                    : fx.cleanMSISDN(etPhone.getText().toString()));
            toUpload.put("location", etAddress.getText().toString());
            toUpload.put("gender", "" + spGender.getSelectedItemPosition());
            if(!isEdit)
                toUpload.put("pass", etPass.getText().toString());
            if(chb.isChecked()){
                toUpload.put("old_pass", etOldPass.getText().toString());
                toUpload.put("pass", etPass.getText().toString());
            }

            Log.w("JSON", toUpload.toString());

        }catch (JSONException ex){
            ex.getCause();
        }

        return toUpload;
    }

    class PostPersonalDetails extends AsyncTask<Void, Void, Boolean> {

        int success = 0;
        String message = "An error occurred! Please try again later";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            cvPersonal.setVisibility(View.INVISIBLE);
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
                String response = rh.postJSON(fx.endPoint(isEdit ? "update_user" : "add_user"), getParams());
                Log.w(getClass().getSimpleName(), response);
                try {
                    JSONObject jObt = new JSONObject(response);
                    success = jObt.getInt("success");
                    message = jObt.getString("message");
                } catch (JSONException ex) {
                    ex.getMessage();
                }

            }catch (Exception ex){
                message = getString(R.string.net_timeout);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(refresh.isRefreshing())
                refresh.setRefreshing(false);

            cvPersonal.setVisibility(View.VISIBLE);

            if(success == 1) {

                if(isEdit) {
                    try {
                        MyAccount.userInfo.put("fname", etFname.getText().toString());
                        //MyAccount.userInfo.put("lname", etLname.getText().toString());
                        MyAccount.userInfo.put("location", etAddress.getText().toString());
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    finish();
                }
                else{
                    sp.edit().putString("phoneNumber", etPhone.getText().toString().replaceAll("//s", "")).apply();
                    finish();
                }
            }

            Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
        }

    }
/*
    class PostVerificationDetails extends AsyncTask<Void, Void, Boolean>{

        int success = 0;
        String message = "An error occurred! Please try again later";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            llHolder.setVisibility(View.INVISIBLE);
            refresh.post(new Runnable() {
                @Override
                public void run() {
                    refresh.setRefreshing(true);
                }
            });

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try{
                String response = rh.postJSON(fx.endPoint("verify_user"), getParams(false));

                //Log.w("VERIFICATION", response);
                try{
                    JSONObject jObt = new JSONObject(response);
                    success = jObt.getInt("success");
                    message = jObt.getString("message");

                    if(success == 1) {
                        sp.edit().putInt("userId", jObt.getInt("user_id")).apply();
                    }

                }catch (JSONException ex){
                    ex.getMessage();
                }
            }catch (Exception ex){
                message = ex.getMessage();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(refresh.isRefreshing())
                refresh.setRefreshing(false);

            llHolder.setVisibility(View.VISIBLE);

            //pDialog.dismiss();
            if(success == 1) {
                sp.edit().putBoolean("regCompleted", true).apply();
                sp.edit().putBoolean("regStarted", false).apply();
                signUpForChat();
                finish();
            }

            Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
        }

        void signUpForChat(){

            QBAuth.createSession(new QBEntityCallback<QBSession>() {
                @Override
                public void onSuccess(QBSession session, Bundle params) {

                    QBUsers.signUp(configNewUser(), new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser user, Bundle args) {
                            sp.edit().putString("password", configNewUser().getPassword()).apply();
                            Log.e(getClass().getSimpleName(), "chat sign up success "+user.toString());
                        }

                        @Override
                        public void onError(QBResponseException errors) {
                            Log.e(TAG, "chat sign up error "+errors);

                        }
                    });
                }

                @Override
                public void onError(QBResponseException errors) {

                }
            });
        }
    }
*/
}
