package com.example.tg71223.roadio;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.AmazonCognitoIdentityProviderClient;

import java.net.URL;

public class LoginActivity extends AppCompatActivity implements LandingFragment.LoginFragmentListener, SignUpFragment.SignUpFragmentListener {

    private boolean login;
    CognitoUserPool mUserPool;
    EditText userEmail;
    EditText userPassword;
    CognitoUserSession uSession;
    CognitoDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Variables
        login = true;
        userEmail = (EditText) findViewById(R.id.userLogin);
        userPassword = (EditText) findViewById(R.id.userPassword);

        //Inflate UI with login fragment
        LandingFragment lf = new LandingFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, lf).commit();

        ClientConfiguration clientConfiguration = new ClientConfiguration();

        // Create a CognitoUserPool object to refer to your user pool
        AmazonCognitoIdentityProviderClient identityProviderClient = new AmazonCognitoIdentityProviderClient(new AnonymousAWSCredentials(), new ClientConfiguration());
        identityProviderClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        mUserPool = new CognitoUserPool(getApplicationContext(), getString(R.string.poolID), getString(R.string.clientID), getString(R.string.clientSecret), identityProviderClient);
        Log.d("RoadIO", mUserPool.toString());

    }

    public void transitionScene() {
        Fragment transitionFrag;
        if(login) {
            transitionFrag = new SignUpFragment();
        } else {
            transitionFrag = new LandingFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, transitionFrag)
                .commit();
        login = !login;
    }

    public void changeActivity() {
        Intent i = new Intent(LoginActivity.this, MapActivity.class);
        startActivity(i);
    }

    public void cognitoLogin(String uName, String uPassword) {

        if(uName == null) {
            Toast.makeText(this, "User name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if(uPassword == null) {
            Toast.makeText(this, "Password Cannot be Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        CognitoUser currentUser = mUserPool.getUser(uName);
        currentUser.getSessionInBackground(authenticationHandler);

        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        userAttributes.addAttribute("Username", uName);
        //mUserPool.getUser(uName).getSessionInBackground(authenticationHandler);
    }

    public void cognitoSignUp(String userPassword, CognitoUserAttributes userAttributes) {
        mUserPool.signUpInBackground(userAttributes.getAttributes().get("email"), userPassword, userAttributes, null, signupCallback);
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            uSession = userSession;
            device = newDevice;
            Log.d("RoadIO", "Authentication Successful");
            changeActivity();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String UserId) {
            Log.d("RoadIO", "GetMFACode");
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
            Log.d("RoadIO", "GetMFACode");
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            Log.d("RoadIO", "AuthenticationChallenge");
        }

        @Override
        public void onFailure(Exception exception) {
            Log.d("RoadIO", "Failure: " + exception.getMessage());
        }
    };

    SignUpHandler signupCallback = new SignUpHandler() {

        @Override
        public void onSuccess(CognitoUser cognitoUser, boolean userConfirmed, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Sign-up was successful
            Log.d("RoadIO", "Sign Up Successful! User Confirmed: " + String.valueOf(userConfirmed) + ", User ID: " + cognitoUser.getUserId());
            // Check if this user (cognitoUser) needs to be confirmed
            if(!userConfirmed) {
                // This user must be confirmed and a confirmation code was sent to the user
                // cognitoUserCodeDeliveryDetails will indicate where the confirmation code was sent
                // Get the confirmation code from user
            }
            else {
                // The user has already been confirmed
            }
        }

        @Override
        public void onFailure(Exception exception) {
            // Sign-up failed, check exception for the cause
            Log.d("RoadIO", "Failure: " + exception.getMessage());
        }
    };
}
