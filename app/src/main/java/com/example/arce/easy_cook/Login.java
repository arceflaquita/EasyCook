package com.example.arce.easy_cook;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends AppCompatActivity {
    EditText editEmail, editContraseña;
    Button btnSesion;
    TextView txtCuenta;
    Context context = getApplicationContext();
    int duration = Toast.LENGTH_SHORT;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /*String url = "";
    String parametros = "";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editContraseña = (EditText) findViewById(R.id.editContraseña);
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
                ResultSet cdr = null;
                Statement sentenciaSQL = null;
                Conexion conecta = new Conexion();
                String sentencia = "select * from registro_usuario;";

                try {
                    cdr = sentenciaSQL.executeQuery(sentencia);
                    String nombre;
                    while(cdr.next()) {
                        nombre = cdr.getString("nombre");
                        Toast toast = Toast.makeText(context, "Hola: " + nombre, duration);
                        toast.show();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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