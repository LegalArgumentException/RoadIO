package com.example.tg71223.roadio;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    SignUpFragmentListener mCallback;
    View view;

    public interface SignUpFragmentListener {
        public void transitionScene();
        public void cognitoSignUp(String userPassword, CognitoUserAttributes userAttributes);
    }

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (SignUpFragmentListener) getActivity();
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement SignUpFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        Button loginButton = (Button) view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.transitionScene();
            }
        });

        Button submitButton = (Button) view.findViewById(R.id.submitSignUpButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<EditText> editTexts = new ArrayList<EditText>();
                boolean correctFormat = true;
                Spinner countrySpinner;
                String password = "";
                LinearLayout content = (LinearLayout) view.findViewById(R.id.signUpContent);
                for(int i = 0; i < content.getChildCount(); i++) {
                    if(content.getChildAt(i) instanceof EditText) {
                        editTexts.add( (EditText) content.getChildAt(i) );
                    } else if(content.getChildAt(i) instanceof Spinner) {
                        countrySpinner = (Spinner) content.getChildAt(i);
                    }
                }
                for(EditText editText : editTexts) {
                    switch (editText.getId()) {
                        case R.id.signUpUserName:
                            if(editText.getText().toString().equals("")) {
                                Toast.makeText(view.getContext(), "Username must be provided", Toast.LENGTH_SHORT).show();
                                correctFormat = false;
                            }
                            break;
                        case R.id.signUpPassword :
                            if(editText.getText().toString().length() < 8) {
                                Toast.makeText(view.getContext(), "Password must be 8 or more characters", Toast.LENGTH_SHORT).show();
                                password = editText.getText().toString();
                                correctFormat = false;
                            }
                            break;
//                        case R.id.signUpPasswordConfirm :
//                            if(!editText.getText().toString().equals(password)) {
//                                Toast.makeText(view.getContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
//                                correctFormat = false;
//                            }
                        default:
                            break;
                    }
                }
                correctFormat = true; // TEST
                if(correctFormat) {
                    CognitoUserAttributes userAttributes = new CognitoUserAttributes();
                    String preferredUsername = ((EditText) view.findViewById(R.id.signUpUserName)).getText().toString();
                    String email = ((EditText) view.findViewById(R.id.signUpEmail)).getText().toString();
                    userAttributes.addAttribute("preferred_username", preferredUsername);
                    userAttributes.addAttribute("email", email);
                    userAttributes.addAttribute("custom:customerType", "driver");
                    userAttributes.addAttribute("locale", "en-US");
                    Log.d("RoadIO", "PreferredUsername: " + userAttributes.getAttributes().get("preferred_username") + ", Email: " + userAttributes.getAttributes().get("email")
                            + ", CustomerType: " + userAttributes.getAttributes().get("customerType") + ", locale: " + userAttributes.getAttributes().get("locale"));
                    mCallback.cognitoSignUp(((EditText) view.findViewById(R.id.signUpPassword)).getText().toString(), userAttributes);
                }
            }
        });

        return view;
    }

}
