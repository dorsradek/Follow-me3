package pl.rdors.follow_me3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.rdors.follow_me3.rest.ServiceGenerator;
import pl.rdors.follow_me3.rest.model.JwtAuthenticationRequest;
import pl.rdors.follow_me3.rest.model.JwtAuthenticationResponse;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.service.AuthService;
import pl.rdors.follow_me3.rest.service.MeetingService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    @BindView(R.id.login_button)
    LoginButton loginButton;
    @BindView(R.id.fb)
    Button facebookButton;

    CallbackManager callbackManager;
    SharedPreferences prefs;

    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        prefs = this.getSharedPreferences("follow-me", Context.MODE_PRIVATE);
        callbackManager = CallbackManager.Factory.create();

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCanceledOnTouchOutside(false);

        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        progressDialog.show();
                        asd(loginResult.getAccessToken());
                        AuthService authService = ServiceGenerator.createService(AuthService.class);
                        authService.facebook(loginResult.getAccessToken().getToken()).enqueue(new Callback<JwtAuthenticationResponse>() {
                            @Override
                            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                                handleAuthenticationResponse(response);
                            }

                            @Override
                            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable t) {
                                onLoginFailed();
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        onLoginFailed();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        onLoginFailed();
                    }
                });

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        AuthService authService = ServiceGenerator.createService(AuthService.class);
        JwtAuthenticationRequest request = new JwtAuthenticationRequest();
        request.setUsername(email);
        request.setPassword(password);
        authService.auth(request).enqueue(new Callback<JwtAuthenticationResponse>() {
            @Override
            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                handleAuthenticationResponse(response);
            }

            @Override
            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable t) {
                onLoginFailed();
            }
        });
    }

    private void handleAuthenticationResponse(Response<JwtAuthenticationResponse> response) {
        JwtAuthenticationResponse jwtAuthenticationResponse = response.body();
        if (jwtAuthenticationResponse != null) {
            String token = jwtAuthenticationResponse.getToken();
            prefs.edit().putString("token", token).apply();
            prefs.edit().putString("username", jwtAuthenticationResponse.getUser().getUsername()).apply();
            loadMeetings(token);
        } else {
            onLoginFailed();
        }
    }

    private void loadMeetings(String token) {
        MeetingService meetingService = ServiceGenerator.createService(MeetingService.class);
        meetingService.findAll(token).enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                MeetingManager.getMeetings().addAll(response.body());
                onLoginSuccess();
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                onLoginSuccess();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        progressDialog.dismiss();
        _loginButton.setEnabled(true);

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void onLoginFailed() {
        progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void onClick(View view) {
        if (view == facebookButton) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email, user_friends"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private void asd(AccessToken token) {
        Bundle args = new Bundle();
        args.putInt("limit", 150);
        GraphRequest request = new GraphRequest(token, "/me/friends", args, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                try {
                    JSONObject graphObject = graphResponse.getJSONObject();
                    JSONArray dataArray = graphObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        try {
                            JSONObject object = dataArray.getJSONObject(i);
                            String str_id = object.getString("id");
                            String str_name = object.getString("name");
                            JSONObject picture_obj = object.getJSONObject("picture");
                            JSONObject data_obj = picture_obj.getJSONObject("data");
                            String str_url = data_obj.getString("url");
                            System.out.println(str_id + " " + str_name + " " + str_url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Exception=" + e);
                    e.printStackTrace();
                }
            }
        });
        request.executeAsync();
    }
}