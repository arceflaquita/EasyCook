package com.example.arce.easy_cook;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PerfilUSer extends AppCompatActivity {
        TextView nombre,correo,inici;
        String nom,corr,inicio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil_user);
        nombre = (TextView) findViewById(R.id.textNombre);
        correo = (TextView) findViewById(R.id.textCorreo);
        inici= (TextView) findViewById(R.id.textInicioPor);
        corr= getIntent().getStringExtra("correo");
        nom= getIntent().getStringExtra("nombreUser");
        inicio=getIntent().getStringExtra("inicio");
        String font_path = "font/Genesis Handwriting.ttf";  //definimos un STRING con el valor PATH ( o ruta por                                                                                    //donde tiene que buscar ) de nuetra fuente


        nombre.setText(nom);
        correo.setText(corr);
        inici.setText(inicio);
        Typeface TF = Typeface.createFromAsset(getAssets(),font_path);
        nombre.setTypeface(TF);
        correo.setTypeface(TF);
        inici.setTypeface(TF);

    }
}
