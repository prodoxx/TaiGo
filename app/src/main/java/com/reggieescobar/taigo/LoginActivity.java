package com.reggieescobar.taigo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.reggieescobar.taigo.Helpers.AppPrefs;
import com.reggieescobar.taigo.Helpers.Config;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Lets Check if the user has a session.
        final AppPrefs myPrefs = AppPrefs.getInstance();
        myPrefs.initialize(LoginActivity.this);


        final EditText emailText = (EditText) findViewById(R.id.email);
        final EditText passwordText = (EditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email  = emailText.getText().toString();
                String password = passwordText.getText().toString();

                final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                        "Authenticating...", true);

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(Config.APPTAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                                dialog.hide();

                                if (!task.isSuccessful()) {
                                   Log.w(Config.APPTAG, "signInWithEmail", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();


                                } else {

                                    myPrefs.setStringPrefValue(Config.PREF_UID, task.getResult().getUser().getUid());

                                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }


                            }
                        });


            }
        });

    }
}
