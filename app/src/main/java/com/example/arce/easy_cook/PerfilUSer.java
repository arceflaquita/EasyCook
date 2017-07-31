package com.example.arce.easy_cook;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arce.easy_cook.DatosUsuario.DatosUsuario;

import org.json.JSONException;
import org.json.JSONObject;

public class PerfilUSer extends AppCompatActivity {
        TextView nombre,correo,inici;
        String nom,corr,inicio,idUsuario;
        Button recetaFavorita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil_user);
        nombre = (TextView) findViewById(R.id.textNombre);
        correo = (TextView) findViewById(R.id.textCorreo);
        inici= (TextView) findViewById(R.id.textInicioPor);
        recetaFavorita=(Button)findViewById(R.id.btnRecetasFavoritas);
        corr= getIntent().getStringExtra("correo");
        nom= getIntent().getStringExtra("nombreUser");
        inicio=getIntent().getStringExtra("inicio");
        idUsuario=getIntent().getStringExtra("id_usuario");
        String font_path = "font/Genesis Handwriting.ttf";  //definimos un STRING con el valor PATH ( o ruta por                                                                                    //donde tiene que buscar ) de nuetra fuente


        if(inicio.equals("Usuario de Easy-Cook")){
            recetaFavorita.setVisibility(View.VISIBLE);
        }else{
            recetaFavorita.setVisibility(View.INVISIBLE);
        }

        nombre.setText(nom);
        correo.setText(corr);
        inici.setText(inicio);
        Typeface TF = Typeface.createFromAsset(getAssets(),font_path);
        nombre.setTypeface(TF);
        correo.setTypeface(TF);
        inici.setTypeface(TF);

        recetaFavorita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject jo = new JSONObject();
                try {
                   // Toast.makeText(getApplicationContext(), "id_usuario:"+new DatosUsuario().getIdUsuario(), Toast.LENGTH_LONG).show();
                    jo.put("id_usuario", DatosUsuario.getIdUsuario());

                    Intent busq = new Intent(PerfilUSer.this, ListaNomRecetas.class);
                    busq.putExtra("ingredientes", jo.toString());
                    busq.putExtra("funcion", "/recetasUser");
                    startActivity(busq);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
