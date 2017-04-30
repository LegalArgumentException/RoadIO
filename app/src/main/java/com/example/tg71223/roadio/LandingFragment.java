package com.example.tg71223.roadio;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class LandingFragment extends Fragment {

    LoginFragmentListener mCallback;
    EditText userName;
    EditText userPassword;


    public interface LoginFragmentListener {
        public void transitionScene();
        public void cognitoLogin(String uName, String uPassword);
    }

    public LandingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mCallback = (LoginFragmentListener) getActivity();
        } catch(ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement SignUpFragmentListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_landing2, container, false);
        Button signUpButton = (Button) view.findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.transitionScene();
            }
        });
        Button submitButton = (Button) view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.cognitoLogin(userName.getText().toString(), userPassword.getText().toString());
            }
        });
        userName = (EditText) view.findViewById(R.id.userLogin);
        userPassword = (EditText) view.findViewById(R.id.userPassword);
        return view;
    }

}
