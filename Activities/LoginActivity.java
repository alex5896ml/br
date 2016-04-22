package com.mobmedianet.trackergps.Project.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobmedianet.trackergps.Project.Google_Analitics.AnalyticsApplication;
import com.mobmedianet.trackergps.Project.Objects.UserObject;
import com.mobmedianet.trackergps.Project.Utility.Constants;
import com.mobmedianet.trackergps.Project.Utility.Utility;
import com.mobmedianet.trackergps.Project.Utility.WShelper;
import com.mobmedianet.trackergps.Project.Utility.WebService;
import com.mobmedianet.trackergps.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LoginActivity extends Activity {

    /*
    Preferences name (Constants)
     */
    private static final String nameUser = "user";
    private static final String namePassword = "pass";
    private static final String nameRemember = "remember";

    /*
    Views in xml
     */

    private EditText user, password;
    private TextView recoverPassword;
    private ImageView principalLogo, menu, menuDespl, go, rememberLogin,eraser;
    private LinearLayout option1, option2, option3, option4;
    private EditText editPassword;
    private DrawerLayout mDrawerLayout;
    private ArrayList<String> data = new ArrayList<>();
    Button submit,close;

    /*
    Model
     */

    // Boolean to remember or not user login
    private Boolean remember = false;
    // Dialog for AsyncTask
    private AlertDialog aDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set View
        setContentView(R.layout.activity_login);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        application.getDefaultTracker();
        Log.e(Constants.Tag + "GoogleAnalytics", "screen name: " + "login");

        Log.e("DeviceID: ", Utility.getDeviceId(getBaseContext()));
      //  Log.e("IMEI: ", Utility.getIMEI(getBaseContext()));

        /*
        Find Views in xml and set listeners
         */
        user = (EditText) findViewById(R.id.user);

        password = (EditText) findViewById(R.id.password);

        recoverPassword = (TextView) findViewById(R.id.forget);
        recoverPassword.setOnClickListener(new RecoverOnClickListener());

        principalLogo = (ImageView) findViewById(R.id.principal_logo);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        menu = (ImageView) findViewById(R.id.menu);
        menu.setOnClickListener(new MenuClosedOnClickListener());

        menuDespl = (ImageView) findViewById(R.id.menu_desple);
        menuDespl.setOnClickListener(new MenuOpenedOnClickListener());

        go = (ImageView) findViewById(R.id.go);
        go.setOnClickListener(new GoOnClickListener());
        eraser = (ImageView) findViewById(R.id.clean_text);
        eraser.setOnClickListener(new CleanTextOnClickListener());
        rememberLogin = (ImageView) findViewById(R.id.remember_user);
        rememberLogin.setOnClickListener(new RememberOnClickListener());

        option1 = (LinearLayout) findViewById(R.id.option1);
        option1.setOnClickListener(new LateralMenuOnClickListener(1));

        option2 = (LinearLayout) findViewById(R.id.option2);
        option2.setOnClickListener(new LateralMenuOnClickListener(2));

        option3 = (LinearLayout) findViewById(R.id.option3);
        option3.setOnClickListener(new LateralMenuOnClickListener(3));

        option4 = (LinearLayout) findViewById(R.id.option4);
        option4.setOnClickListener(new LateralMenuOnClickListener(4));

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    go.performClick();
                    return true;
                }
                return false;
            }
        });

        // Get status of remember user
        rememberUser();
        new UrlSideMenu().execute();
    }

    // Handle remember_user option
    private void rememberUser() {
        Log.e(Constants.Tag + "Remember User", Utility.get_preferences(nameRemember, getBaseContext()));
        if (Utility.get_preferences(nameRemember, getBaseContext()).contentEquals("true")) {
            remember = true;
            rememberLogin.setImageResource(R.drawable.select);
            user.setText(Utility.get_preferences(nameUser, getBaseContext()));
            password.setText(Utility.get_preferences(namePassword, getBaseContext()));
        }
    }

    // show popup to recover password
    private void showPopup(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout L1 = (LinearLayout) inflater.inflate(
                R.layout.popup_password, null);

        editPassword = (EditText) L1.findViewById(R.id.editPassword);
        editPassword.addTextChangedListener(textWatcher());
        // Getting a reference to Close button, and close the popup when clicked.
        close = (Button) L1.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                aDialog.dismiss();
            }
        });


        editPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    submit.performClick();
                    return true;
                }
                return false;
            }
        });


        submit = (Button) L1.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editPassword.getWindowToken(), 0);
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editPassword.getText().toString().trim()).matches()){
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_formatEmail), Toast.LENGTH_SHORT).show();

                    return;
                }

                new RecoverPassword().execute();
                aDialog.dismiss();

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(L1);
        aDialog = builder.create();
        aDialog.show();
        aDialog.setCanceledOnTouchOutside(false);
        aDialog.getWindow().setGravity(Gravity.CENTER);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(aDialog.getWindow().getAttributes());
//        lp.height = Math.round(context.getResources().getDimension(R.dimen.dialog_height));
//        lp.width = Math.round(context.getResources().getDimension(R.dimen.dialog_width));
//        aDialog.getWindow().setAttributes(lp);
        aDialog.show();

    }



    private TextWatcher textWatcher() {
        return new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editPassword.getText().length() > 0) {
                    submit.setVisibility(View.VISIBLE);

                } else {
                    submit.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    //Menu options button multiple listener
    private class LateralMenuOnClickListener implements View.OnClickListener {
        int position;

        LateralMenuOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {

            Intent browserIntent;
            switch (position) {
                case 1:
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utility.get_preferences("URL_News",getBaseContext())));
                    startActivity(browserIntent);
                    break;
                case 2:
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utility.get_preferences("URL_Offices",getBaseContext())));
                    startActivity(browserIntent);
                    break;
                case 3:
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utility.get_preferences("URL_Promotions",getBaseContext())));
                    startActivity(browserIntent);
                    break;
                case 4:
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utility.get_preferences("URL_Contacts",getBaseContext())));
                    startActivity(browserIntent);
                    break;
                default:
                    break;
            }
        }
    }

    //Remember Button listener
    private class RememberOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            remember = !remember;
            Utility.save_preferences(nameRemember, String.valueOf(remember), getBaseContext());
            if (remember) {
                rememberLogin.setImageResource(R.drawable.select);
                Utility.save_preferences(nameUser, user.getText().toString(), getBaseContext());
                Utility.save_preferences(namePassword, password.getText().toString(), getBaseContext());
            } else
                rememberLogin.setImageResource(R.drawable.unselect);
        }

    }

    //LoginButton listener
    private class GoOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (Utility.isConnected(getBaseContext())) {
                if (user.getText().toString().length() == 0) {
                    Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
                    user.startAnimation(shake);

                } else if (password.getText().toString().length() == 0) {
                    Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
                    password.startAnimation(shake);

                } else {
                    new AuthenticateUser().execute();
                }
            } else {
                Utility.showAlert(LoginActivity.this, getText(R.string.noconexion).toString());
            }
        }
    }

    private class CleanTextOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            user.setText("");
            password.setText("");
        }
    }
    // Recover password
    private class RecoverOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            showPopup(LoginActivity.this);
        }
    }

    // Menu closed Image listener
    private class MenuClosedOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    // Menu opened Image listener
    private class MenuOpenedOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
    }

    // Recover password AsyncTask
    private class RecoverPassword extends AsyncTask<Object, Void, String> {
        protected Dialog progressDialog;
        private String mEditPassword;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(LoginActivity.this, getText(R.string.recuperar_contraseña_dialogo).toString());
            mEditPassword = editPassword.getText().toString();
        }

        @Override
        protected String doInBackground(Object... parametros) {
            String result = "";
            ArrayList<NameValuePair> params = WShelper.paramsRecoverPassword(mEditPassword);
            result = WebService.Call(WebService.recover, params);
            if (result != null && !result.isEmpty())
                result = result.substring(result.indexOf("=") + 1, result.indexOf(";"));
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            JSONObject post;
            progressDialog.dismiss();
            try {
                post = new JSONObject(result);
                result =(post.getString("responseCode").equals(WebService.response_SUCCESS)) ? "Success": null;

            } catch (JSONException e) {
                result = null;
            }

            if (result == null) {
                recoverAnswer(1);
            } else {
                recoverAnswer(0);
            }

        }

        // show msg depending of ws answer
        public void recoverAnswer(int i) {

            if (i == 1) {
                Utility.showAlert(LoginActivity.this, getText(R.string.noRecoverMSG).toString());
            } else {
                Utility.showAlert(LoginActivity.this, getText(R.string.RecoverMSG).toString());
            }

        }
    }

    // Register user to get Push Alerts
    private class RegisterAlerts extends AsyncTask<Object, Void, String> {
        protected Dialog progressDialog;
        String email;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(LoginActivity.this, getText(R.string.Alertas_dialogo).toString());
            email = user.getText().toString().trim();
        }

        @Override
        protected String doInBackground(Object... parametros) {
            String result = "";
            //   WServiceCall call;

            String url = "http://www.mobilemediapush.com/Registro_Portalesv4/modules/Register/registration.php";
            String media = "MovilTrack";
            String source = "OTA";
            String status = "true";
            String reg = "";
            String country = "Venezuela";
            String Version = "1.0.0";
            String mili = "";
            String state2 = "Generic";

            String phone = "";
            String genere = "";
            String max = "2";
            long id = 0;
            String PhoneModel = android.os.Build.MODEL;
            String AndroidOS = android.os.Build.VERSION.RELEASE;
            String Brand = android.os.Build.BRAND;

            url = url + "?reg=" + reg + "&c=" + email +
                    "&t=" + phone + "&nac=" + mili + "&country=" + country + "&state=" + state2 +
                    "&gender=" + genere +
                    "&media=" + media +
                    "&source=" + source +
                    "&maxdaily=" + max + "&id=" + id +
                    "&status=" + status + "&os=" + AndroidOS +
                    "&brand=" + Brand.replace(" ", "") +
                    "&device=" + PhoneModel.replace(" ", "") + "&version=" + Version + "&z1=MovilTrack";

            Log.d("url", url);
            HttpClient client = new DefaultHttpClient();
            HttpGet method = new HttpGet(url);
            try {
                HttpResponse response = client.execute(method);
                BufferedReader rd = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));
                result = rd.readLine();
                Log.e("alertas", result);

            } catch (IOException e) {
                Utility.showAlert(getBaseContext(), getText(R.string.conErrorMSG).toString());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {


            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            progressDialog.dismiss();
            startActivity(intent);
            finish();


        }

    }
    // SideMenu AsyncTask
    private class UrlSideMenu extends AsyncTask<Void, Void, String> {
        //            protected Dialog progressDialog;
        String sProvider, mUser, mPassword;
        final int WS_ERROR = 1;
        final int AUT_ERROR = 2;
        final String PROVIDER_ERROR = "provider_error";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            data.clear();
        }

        @Override
        protected String doInBackground(Void... parametros) {
            sProvider = getConnectionCode();
            String result = "";

            if (sProvider.equals("")) {
                return PROVIDER_ERROR;
            } else {
                ArrayList<NameValuePair> params = WShelper.paramsGetLink();
                result = WebService.Call(WebService.getLinks, params);
                if (result != null && !result.isEmpty())
                    result = result.substring(result.indexOf("=") + 1, result.indexOf(";"));
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            CharSequence errorMessage = "";
            super.onPostExecute(result);
            if (!(result == null) && !result.contentEquals(PROVIDER_ERROR)) {
                int validLogin = 0;
                JSONObject post;
                try {
                    post = new JSONObject(result);
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {

                        post = new JSONObject(post.toString());

                        WShelper.responseGetLinks(post, data);
                        Utility.save_preferences("URL_News", data.get(0), getBaseContext());
                        Utility.save_preferences("URL_Offices",data.get(1),getBaseContext());
                        Utility.save_preferences("URL_Promotions",data.get(2),getBaseContext());
                        Utility.save_preferences("URL_Contacts",data.get(3),getBaseContext());
                    } else
                        validLogin = WS_ERROR;
                } catch (JSONException e) {
                    validLogin = WS_ERROR;
                }
            } else {
                if (result.contentEquals(PROVIDER_ERROR))
                    errorMessage = getText(R.string.providerErrorMSG);
                else
                    errorMessage = getText(R.string.conErrorMSG);
            }
        }

    }
    // Log in AsyncTask
    private class AuthenticateUser extends AsyncTask<Void, Void, String> {
        protected Dialog progressDialog;
        String sProvider, mUser, mPassword;
        final int WS_ERROR = 1;
        final int AUT_ERROR = 2;
        final String PROVIDER_ERROR = "provider_error";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = Utility.create_dialog(LoginActivity.this, getText(R.string.login_dialogo).toString());
            mUser = user.getText().toString();
            mPassword = password.getText().toString();
            UserObject.initInstance(LoginActivity.this);
        }

        @Override
        protected String doInBackground(Void... parametros) {
            String result = "";
            sProvider = getConnectionCode();
            if (sProvider.equals("")) {
                return PROVIDER_ERROR;
            } else {
                ArrayList<NameValuePair> params = WShelper.paramsAuthenticateUser(mUser, mPassword, sProvider,getBaseContext());
                result = WebService.Call(WebService.login, params);
                if (result != null && !result.isEmpty())
                    result = result.substring(result.indexOf("=") + 1, result.indexOf(";"));
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            CharSequence errorMessage = "";
            super.onPostExecute(result);
            if (!(result == null) && !result.contentEquals(PROVIDER_ERROR)) {
                int validLogin = 0;
                JSONObject post;
                try {
                    post = new JSONObject(result);
                    if (post.getString("responseCode").equals(WebService.response_SUCCESS)) {
                        post = new JSONObject(post.getJSONObject("AuthenticateUser").getJSONObject("DATA").toString());
                        if (post.getString("Result").equals("OK")) {
                            WShelper.responseAuthenticateUser(post, UserObject.getInstance(), sProvider);

                            if (remember) {
                                Utility.save_preferences(nameUser, user.getText().toString(), getBaseContext());
                                Utility.save_preferences(namePassword, password.getText().toString(), getBaseContext());
                            }
                        } else {
                            validLogin = AUT_ERROR;
                        }
                    } else
                        validLogin = WS_ERROR;
                } catch (JSONException e) {
                    validLogin = WS_ERROR;
                }

                switch (validLogin) {
                    case 0:
                        //new Register_Alerts().execute();
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case AUT_ERROR:
                        errorMessage = getText(R.string.autErrorMSG);
                        Utility.showAlert(LoginActivity.this, errorMessage.toString());
                        break;
                    case WS_ERROR:
                        errorMessage = getText(R.string.conErrorMSG);
                        Utility.showAlert(LoginActivity.this, errorMessage.toString());
                        break;
                }
            } else {
                if (result.contentEquals(PROVIDER_ERROR))
                    errorMessage = getText(R.string.providerErrorMSG);
                else
                    errorMessage = getText(R.string.conErrorMSG);

                Utility.showAlert(LoginActivity.this, errorMessage.toString());
            }
            progressDialog.dismiss();
        }
    }
    // get the provider code or connection code
    public String getConnectionCode() {

        ArrayList<NameValuePair> params = WShelper.paramsGetConnectionCode(user.getText().toString());
        String result = WebService.Call(WebService.provider, params);
        // TODO: Cableado
//        String result = "Get_Service_ProviderResponse{Get_Service_ProviderResult={'Get_Service_Provider':{'DATA':{'ConnectionCode':'Sateqmx_QA_PR','ConnectionData':'76.74.147.160,1816'}}}; }";

        try {
            if (result.indexOf("anyType{}") > -1) {
                return "";
            }

            if (result.indexOf("anyType{}") > -1) {
                return "";
            }
            result = result.substring(result.indexOf("DATA\":") + 6, result.indexOf(";") - 2);
            JSONObject post = new JSONObject(result);
            return post.getString("ConnectionCode");


        } catch (Exception e) {
            String message = "";
            return message;
        }
    }

}