package pl.rdors.follow_me3.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.internal.LinkedTreeMap;

import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.rest.ServiceGenerator;
import pl.rdors.follow_me3.rest.service.AuthService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by rdors on 2016-11-01.
 */
public class NewsFragment extends android.support.v4.app.Fragment implements IOnActivityResult {

    private TestActivity activity;
    private LoginButton loginButton;
    CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    SharedPreferences prefs;


    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, null, false);

        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                System.out.println("qwe");
            }
        };


        prefs = activity.getSharedPreferences("follow-me", Context.MODE_PRIVATE);


        // If using in a fragment
//        loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                System.out.println("asd");
//            }
//
//            @Override
//            public void onCancel() {
//                System.out.println("asd");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                System.out.println("asd");
//            }
//        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        System.out.println("asd");
                        AuthService authService = ServiceGenerator.createService(AuthService.class);
                        authService.facebook(loginResult.getAccessToken().getToken()).enqueue(new Callback<Object>() {
                            @Override
                            public void onResponse(Call<Object> call, Response<Object> response) {
                                System.out.println("Suc");
                                String token = (String) ((LinkedTreeMap) response.body()).get("token");
                                prefs.edit().putString("token", token).apply();
                            }

                            @Override
                            public void onFailure(Call<Object> call, Throwable t) {
                                System.out.println("fail");
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("asd");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("asd");
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (TestActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void apply(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }
}
