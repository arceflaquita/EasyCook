package com.example.arce.easy_cook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class Cuenta extends AppCompatActivity {
    EditText editNombre, editPaterno, editMaterno;
    EditText editCorreo, editPass, editRepPass;
    Button btnCancelar, btnRegistrar;
    String urlREST = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuenta);
        //TODO: Cambiar por la ip de la PC que corre el servicio RestEC en archivo strings.xml
        urlREST = this.getResources().getString(R.string.urlREST);
        editNombre = (EditText)findViewById(R.id.editNombre);
        editPaterno = (EditText)findViewById(R.id.editPaterno);
        editMaterno = (EditText)findViewById(R.id.editMaterno);
        editCorreo = (EditText)findViewById(R.id.editCorreo);
        editPass = (EditText)findViewById(R.id.editPass);
        editRepPass = (EditText)findViewById(R.id.editRepPass);
        btnCancelar = (Button)findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRegistrar = (Button)findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                registrarUsuario(v);
            }
        });

    }

    private void registrarUsuario(View v) {
        String nombre = editNombre.getText().toString();
        String paterno = editPaterno.getText().toString();
        String materno = editMaterno.getText().toString();
        String correo = editCorreo.getText().toString();
        String password = editPass.getText().toString();
        String repPassword = editRepPass.getText().toString();

        if(0 == nombre.compareTo("") || 0 == paterno.compareTo("") || 0 == materno.compareTo("")
                || 0 == password.compareTo("") || 0 == repPassword.compareTo("")){
            alerta("Capture toda la informacion!","Warning");
            //Toast.makeText(getApplicationContext(), "Capture toda la informacion!", Toast.LENGTH_LONG).show();
            return;
        }
        if(0 != password.compareTo(repPassword))
        {
            alerta("Las contraseñas no coinciden!","Error");
           // Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden!", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject jo = new JSONObject();
        try {
            jo.put("nombre", nombre);
            jo.put("ap_paterno", paterno);
            jo.put("ap_materno", materno);
            jo.put("correo", correo);
            jo.put("password", password);
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

        String url = urlREST + "/nuevoUsuario";
        ac.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    //Status 200 quiere decir que se recibio respuesta
                    if (statusCode == 200) {
                        if(responseBody != null && responseBody.length > 0) {
                            JSONObject res = new JSONObject(new String(responseBody));
                            Object valido = res.get("userValido");
                            //si el usuario es valido lo redireccionamos al Activity principal
                            if(valido.toString().compareTo("true") == 0){
                                alerta("Se registro correctamente!","Exito");
                                //Toast.makeText(getApplicationContext(), "Se registro correctamente!", Toast.LENGTH_LONG).show();
                                editNombre.setText("");
                                editPaterno.setText("");
                                editMaterno.setText("");
                                editCorreo.setText("");
                                editPass.setText("");
                                editRepPass.setText("");
                                Intent log = new Intent(Cuenta.this, Login.class);
                                startActivity(log);
                            }else {
                               // Toast.makeText(getApplicationContext(), "Error al registrar el usuario!", Toast.LENGTH_LONG).show();
                                alerta("Error al registrar el usuario!","Error");
                            }
                        }
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
                Toast.makeText(getApplicationContext(), "Error onFailure", Toast.LENGTH_LONG).show();
            }
        });
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
