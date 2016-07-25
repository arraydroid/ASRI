package asri_tools.devalkhatech.com.asri.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import asri_tools.devalkhatech.com.asri.R;
import asri_tools.devalkhatech.com.asri.application.AppConfig;
import asri_tools.devalkhatech.com.asri.controllers.AppController;
import asri_tools.devalkhatech.com.asri.controllers.DBController;
import asri_tools.devalkhatech.com.asri.helpers.SessionManager;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    //private static final int REQUEST_SIGNUP = 0;

    @InjectView(R.id.input_username)
    EditText _usernameText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.btn_login)
    Button _loginButton;

    private ProgressDialog pDialog;
    private SessionManager session;
    private DBController db;
    private CoordinatorLayout Clayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Clayout = (CoordinatorLayout) findViewById(R.id.coordinatorLoginLayout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Typeface myFont = Typeface.createFromAsset(getAssets(), "indy.ttf");
        TextView companyName = (TextView) findViewById(R.id.company);
        companyName.setTypeface(myFont);
        // Progress dialog

        pDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        pDialog.setCancelable(false);
        // SQLite database handler
        db = new DBController(getApplicationContext());
        // Session manager
        session = new SessionManager(getApplicationContext());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        ButterKnife.inject(this);
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                pDialog.setIndeterminate(true);
                pDialog.setMessage("Authenticating..");
                pDialog.setCancelable(false);
                pDialog.show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        String username = _usernameText.getText().toString().trim();
                        String password = _passwordText.getText().toString().trim();

                        if (!username.isEmpty() && !password.isEmpty()) {
                            // login user
                            checkLogin(username, password);

                            pDialog.hide();
                        } else {
                            // Prompt user to enter credentials
                            Snackbar snack = Snackbar.make(Clayout, "Please enter the credentials (Username and Password)", Snackbar.LENGTH_LONG);
                            snack.show();
                            hideDialog();
                            Toast.makeText(getApplicationContext(),
                                    "Please enter the credentials (Username and Password)", Toast.LENGTH_LONG)
                                    .show();

                        }
                        //login();
                    }
                }, 1000);//just mention the time when you want to launch your action

            }
        });

    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }


        // TODO: Implement your own authentication logic here.

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        hideDialog();
                    }
                }, 3000);*/
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        //finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("Enter a valid username");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Enter a valid password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (!username.isEmpty() && !password.isEmpty()) {
            // login user
            checkLogin(username, password);
        } else {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Please enter the credentials!", Toast.LENGTH_LONG)
                    .show();
        }

        return valid;
    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String uname, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        //_loginButton.setEnabled(false);

//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
//                R.style.AppTheme_Dark_Dialog);
        pDialog.setIndeterminate(true);
        pDialog.setMessage("Authenticating...");
        pDialog.show();

        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true, uname);
                        // Launch main activity
                        onLoginSuccess();

                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        Snackbar snack = Snackbar.make(Clayout, errorMsg, Snackbar.LENGTH_LONG);
                        snack.show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Snackbar snack = Snackbar.make(Clayout, "Json error: " + e.getMessage(), Snackbar.LENGTH_LONG);
                    snack.show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                Snackbar snack = Snackbar.make(Clayout, error.getMessage(), Snackbar.LENGTH_LONG);
                snack.show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", uname);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
