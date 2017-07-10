package com.example.arce.easy_cook;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RecuperarPassword extends AppCompatActivity {
        private EditText correo;
        private Button enviar;
        String urlREST = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_password);
        urlREST = this.getResources().getString(R.string.urlREST);
        correo=(EditText)findViewById(R.id.correo);
        enviar=(Button)findViewById(R.id.enviar);
        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mandarPassword(v);
            }
        });
    }

    public void mandarPassword(View view) {
        String user = correo.getText().toString();

        if(user.compareTo("") != 0 ){
            JSONObject jo = new JSONObject();
            try {
                jo.put("correo", user);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            AsyncHttpClient ac = new AsyncHttpClient();
            HttpEntity entity = null;
            try {
                entity = new StringEntity(jo.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //TODO: Cambiar por la ip de la PC que corre el servicio RestEC
            String url = urlREST + "/recuperarPassword";
            ac.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {


                        //Status 200 quiere decir que se recibio respuesta
                        if (statusCode == 200) {
                           alerta("Contraseña Enviada","Enviado..");
                        } else {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }



                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getApplicationContext(), "Error onFailure:  "+error, Toast.LENGTH_LONG).show();
                }
            });

        }else{
            //  Toast.makeText(getApplicationContext(), "Por favor ingrese sus datos", Toast.LENGTH_LONG).show();
            alerta("Por favor ingrese su correo","Error");
        }
    }
    public void alerta(String cadena,String tipo){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //seleccionamos la cadena a mostrar
        dialogBuilder.setMessage(cadena);
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);

        //elegimo un titulo y configuramos para que se pueda quitar
        dialogBuilder.setCancelable(true).setTitle(tipo);

        //mostramos el dialogBuilder
        dialogBuilder.create().show();

    }
}
