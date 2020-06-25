package com.example.vego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.vego.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {
    Button btn_signin, btn_signup;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;
    RelativeLayout mainLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_signin = findViewById(R.id.button_signin);
        btn_signup = findViewById(R.id.button_signup);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");
        mainLayer = findViewById(R.id.main_layer);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSigninWindow();
            }
        });

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignupWindow();
            }
        });


    } //oncreate

    private void showSignupWindow() {

        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setTitle("Sign Up");
        dialog.setMessage("Provide your data");

        LayoutInflater inflater = LayoutInflater.from(this);
        View signupWindow = inflater.inflate(R.layout.signup_window, null);
        dialog.setView(signupWindow);

        final MaterialEditText email = signupWindow.findViewById(R.id.email);
        final MaterialEditText name = signupWindow.findViewById(R.id.name);
        final MaterialEditText password = signupWindow.findViewById(R.id.password);
        final MaterialEditText phone = signupWindow.findViewById(R.id.phone);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(mainLayer, "Type your e-mail", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name.getText().toString())) {
                    Snackbar.make(mainLayer, "Type your name", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                if (password.getText().toString().length() < 5) {
                    Snackbar.make(mainLayer, "Type your password (must contain min. 5 symbols, min. 1 number)", Snackbar.LENGTH_SHORT).show();
                    return;
                }




                if (TextUtils.isEmpty(phone.getText().toString())) {
                    Snackbar.make(mainLayer, "Type your phone number", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                auth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                User user = new User();
                                user.setEmail(email.getText().toString());
                                user.setName(name.getText().toString());
                                user.setPhone(phone.getText().toString());
                                user.setPassword(password.getText().toString());

                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(mainLayer, "Success!", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });




                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mainLayer, "The e-mail is taken" + e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });


            }
        });
dialog.show();
    }//sign up

    private void showSigninWindow() {

        AlertDialog.Builder dialog= new AlertDialog.Builder(this);
        dialog.setTitle("Log In");
        dialog.setMessage("Provide your data to log in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View signinWindow = inflater.inflate(R.layout.signin_window, null);
        dialog.setView(signinWindow);

        final MaterialEditText email = signinWindow.findViewById(R.id.email);
        final MaterialEditText password = signinWindow.findViewById(R.id.password);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(mainLayer, "Type your e-mail", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (password.getText().toString().length() < 5) {
                    Snackbar.make(mainLayer, "Type your password (must contain min. 5 symbols, min. 1 number)", Snackbar.LENGTH_SHORT).show();
                    return;
                }


                auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                startActivity(new Intent(MainActivity.this, MapActivity.class));
                                finish();
                            }


                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mainLayer, "Wrong password or e-mail" + e.getMessage(), Snackbar.LENGTH_SHORT).show();

                    }
                });






            }
        });


        dialog.show();



    }//showsignin
}  //Main