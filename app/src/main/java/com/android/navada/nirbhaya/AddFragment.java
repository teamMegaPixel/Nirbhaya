package com.android.navada.nirbhaya;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddFragment extends Fragment {

    private EditText emailEditText;
    private EditText nameEditText;
    private EditText phoneEditText;
    private Button add;
    private DatabaseReference mDatabaseReference;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(FirebaseAuth.getInstance().getUid());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        emailEditText = view.findViewById(R.id.emailEditText);
        nameEditText = view.findViewById(R.id.nameEditText);
        phoneEditText = view.findViewById(R.id.numberEditText);
        add= view.findViewById(R.id.add);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String number = phoneEditText.getText().toString().trim();

                if(name.isEmpty() || email.isEmpty()|| number.isEmpty())
                        makeToast("Fields cannot be empty");
                else if(number.length()!=10)
                    makeToast("Enter a valid number");
                else{

                    EmergencyContact ec = new EmergencyContact(name,email,number);

                    mDatabaseReference.push().setValue(ec);

                    makeToast("Success");

                    emailEditText.setText("");
                    nameEditText.setText("");
                    phoneEditText.setText("");

                }

            }
        });

    }

    private void makeToast(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
