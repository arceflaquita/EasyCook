package com.example.arce.easy_cook;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Login extends AppCompatActivity {
    EditText editEmail, editContrasena;
    Button btnSesion;
    TextView txtCuenta;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editContrasena = (EditText) findViewById(R.id.editContrasena);
        btnSesion = (Button) findViewById(R.id.btnSesion);
        txtCuenta = (TextView) findViewById(R.id.txtCuenta);

        txtCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abreCuenta = new Intent(Login.this, Cuenta.class);
                startActivity(abreCuenta);
            }


        });

        btnSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(v);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void loginUser(View view) {
        String user = editEmail.getText().toString();
        String passwd = editContrasena.getText().toString();
        RequestParams params = new RequestParams();
        if(user.compareTo("") != 0 && passwd.compareTo("") != 0){
            params.put("usuario", user);
            params.put("password", passwd);
            params.setUseJsonStreamer(true);

            JSONObject jo = new JSONObject();
            try {
                jo.put("usuario", user);
                jo.put("password", passwd);
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
            String url = "http://192.168.0.14:8080/RestEC/services/EasyCook/validaUsuario";
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
                                    Intent abreCuenta = new Intent(Login.this, Busqueda.class);
                                    startActivity(abreCuenta);
                                }else {
                                    Toast.makeText(getApplicationContext(), "Usuario o password incorrecto!", Toast.LENGTH_LONG).show();
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

        }else{
            Toast.makeText(getApplicationContext(), "Por favor ingrese sus datos", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

    /*private class SolicitaDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {



                return conexion.postDatos(urls[0], parametros);


        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            textView.setText(result);
        }
    }
}
*/