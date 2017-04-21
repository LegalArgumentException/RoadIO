package com.example.tg71223.roadio;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    SignUpFragmentListener mCallback;
    View view;

    public interface SignUpFragmentListener {
        public void transitionScene();
        public void changeActivity();
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
                        case R.id.signUpFirstName :
                            if(editText.getText().toString().equals("")) {
                                Toast.makeText(view.getContext(), "First name must be provided", Toast.LENGTH_SHORT).show();
                                correctFormat = false;
                            }
                            break;
                        case R.id.signUpSecondName :
                            if(editText.getText().toString().equals("")) {
                                Toast.makeText(view.getContext(), "Last name must be provided", Toast.LENGTH_SHORT).show();
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
                        case R.id.signUpPasswordConfirm :
                            if(!editText.getText().toString().equals(password)) {
                                Toast.makeText(view.getContext(), "Passwords must match", Toast.LENGTH_SHORT).show();
                                correctFormat = false;
                            }
                        default:
                            break;
                    }
                }
                correctFormat = true; // TEST
                if(correctFormat) {
                    mCallback.changeActivity();
                }
            }
        });

        return view;
    }

}
