package pl.rdors.follow_me3.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;

import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;

/**
 * Created by rdors on 2016-11-01.
 */
public class NewsFragment extends android.support.v4.app.Fragment implements IOnActivityResult {

    private TestActivity activity;
    private LoginButton loginButton;
    CallbackManager callbackManager;
    SharedPreferences prefs;


    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, null, false);

        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");


        prefs = activity.getSharedPreferences("follow-me", Context.MODE_PRIVATE);


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        System.out.println("asd");
//                        AuthService authService = ServiceGenerator.createService(AuthService.class);
//                        authService.facebook(loginResult.getAccessToken().getToken()).enqueue(new Callback<Object>() {
//                            @Override
//                            public void onResponse(Call<Object> call, Response<Object> response) {
//                                System.out.println("Suc");
//                                String token = (String) ((LinkedTreeMap) response.body()).get("token");
//                                prefs.edit().putString("token", token).apply();
//                            }
//
//                            @Override
//                            public void onFailure(Call<Object> call, Throwable t) {
//                                System.out.println("fail");
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        System.out.println("asd");
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        System.out.println("asd");
//                    }
//                });
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
