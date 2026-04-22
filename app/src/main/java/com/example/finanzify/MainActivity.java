package com.example.finanzify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.finanzify.Login.LoginActivity;
import com.example.finanzify.Login.RegistrarActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);


        // boton empieza para ir al login
        Button boton_empezar = findViewById(R.id.buttonEmpezar);
        boton_empezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_empezar = new Intent(MainActivity.this, RegistrarActivity.class);
                startActivity(intent_empezar);
            }
        });

    }
}