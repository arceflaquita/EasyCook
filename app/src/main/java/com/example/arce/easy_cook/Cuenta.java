package com.example.arce.easy_cook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Cuenta extends AppCompatActivity {
    EditText editNombre,editApellidos,editCorreo,editPass;
    Button btnCancelar,btnRegistrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuenta);

        editNombre = (EditText)findViewById(R.id.editNombre);
        editApellidos = (EditText)findViewById(R.id.editApellidos);
        editCorreo = (EditText)findViewById(R.id.editCorreo);
        btnCancelar = (Button)findViewById(R.id.btnCancelar);
        btnRegistrar = (Button)findViewById(R.id.btnRegistrar);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
