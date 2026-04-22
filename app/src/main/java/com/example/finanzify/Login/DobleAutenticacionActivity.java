package com.example.finanzify.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finanzify.PantallaInicioActivity;
import com.example.finanzify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class DobleAutenticacionActivity extends AppCompatActivity {

    private EditText editTextTelefono, editTextCodigo;
    private Button botonEnviarCodigo, botonVerificarCodigo;

    private FirebaseAuth mAuth;
    private String mVerificationId;
    int metodo =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doble_autenticacion);

        editTextTelefono = findViewById(R.id.editTextTelefono);
        editTextCodigo = findViewById(R.id.editTextCodigo);
        botonEnviarCodigo = findViewById(R.id.botonEnviarCodigo);
        botonVerificarCodigo = findViewById(R.id.botonVerificarCodigo);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.getPhoneNumber();
        Log.d("telefono", "numero de telefono actual " + currentUser.getPhoneNumber());
        //if telefono = null que use metodo 1 y si si tiene un telefono que checkee que es igual al guardado y metodo 2

        botonEnviarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editTextTelefono.getText().toString();
                if(currentUser.getPhoneNumber()==null || currentUser.getPhoneNumber().isEmpty()) {
                    metodo = 1;
                    enviarCodigoVerificacion(phoneNumber);
                }
                if(currentUser.getPhoneNumber()!=null){
                    if(currentUser.getPhoneNumber().equals(phoneNumber)) {
                        metodo = 2;
                        sendVerificationCode2(phoneNumber);
                    } else {
                        Toast.makeText(DobleAutenticacionActivity.this, "numero debe coincidir con el registrado", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });

        botonVerificarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = editTextCodigo.getText().toString();
                if(metodo == 1)
                    verificarCodigo(code);
                if(metodo == 2)
                verifyVerificationCode2(code);
            }
        });
    }

    private void verificarCodigo(String code) {
        if (mVerificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential(credential);
        } else {
            Toast.makeText(DobleAutenticacionActivity.this, "Primero envía el código de verificación", Toast.LENGTH_SHORT).show();
        }
    }
    // Método para enviar el código de verificación al número de teléfono proporcionado
    private void enviarCodigoVerificacion(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(DobleAutenticacionActivity.this, "Error al enviar el código: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("presupuesto", "Error al enviar el código: " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        mVerificationId = verificationId;
                        Toast.makeText(DobleAutenticacionActivity.this, "Código enviado correctamente", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // La autenticación se completó exitosamente
                            Toast.makeText(DobleAutenticacionActivity.this, "Doble autenticación exitosa", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(DobleAutenticacionActivity.this, PantallaInicioActivity.class));
                            finish(); // Finaliza esta actividad para evitar que el usuario regrese a la pantalla de doble autenticación
                        } else {
                            // La autenticación falló
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(DobleAutenticacionActivity.this, "Código incorrecto", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DobleAutenticacionActivity.this, "Error al autenticar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private void signInWithPhoneNumber(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        // Se llama automáticamente cuando se completa la verificación del teléfono
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // Se llama si la verificación del teléfono falla
                        Toast.makeText(DobleAutenticacionActivity.this, "Error al verificar el número de teléfono: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("DobleAutenticacion", "Error al verificar el número de teléfono: " + e.getMessage());
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        // Se llama cuando se envía el código de verificación al número de teléfono
                        mVerificationId = verificationId;
                        Toast.makeText(DobleAutenticacionActivity.this, "Código de verificación enviado", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void sendVerificationCode2(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential2(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Toast.makeText(DobleAutenticacionActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // The SMS verification code has been sent to the provided phone number,
            // we now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            mVerificationId = verificationId;
        }
    };

    private void verifyVerificationCode2(String code) {
        if (mVerificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            signInWithPhoneAuthCredential2(credential);
        } else {
            Toast.makeText(DobleAutenticacionActivity.this, "Primero envía el código de verificación", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential2(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(DobleAutenticacionActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();

                            Intent intentPantallaInicio = new Intent(getApplicationContext(), PantallaInicioActivity.class);
                            startActivity(intentPantallaInicio);
                            finish();
                            // Aquí puedes redirigir a la siguiente actividad o realizar cualquier otra acción después de autenticar al usuario.
                        } else {
                            // Sign in failed, display a message and update the UI
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(DobleAutenticacionActivity.this, "Invalid Verification Code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private boolean isPhoneNumberRegistered(String phoneNumber) {
        // Lista de números de teléfono registrados
        String[] phoneNumbers = {
                "34722526037",
                "+34669842926",
                "+34777666555",
                "+34622526037"
                // Agrega los demás números aquí
        };

        // Recorre la lista y verifica si el número de teléfono dado está presente
        for (String number : phoneNumbers) {
            if (number.equals(phoneNumber)) {
                // Si se encuentra el número, devuelve true
                return true;
            }
        }
        // Si el número no se encuentra, devuelve false
        return false;
    }

}