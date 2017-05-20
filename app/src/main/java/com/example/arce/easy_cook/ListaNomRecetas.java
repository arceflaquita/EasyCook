package com.example.arce.easy_cook;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ListaNomRecetas extends AppCompatActivity {

    ListView listRec;
    String urlREST = "";
    List<String> recs;
    String nomEjecucion="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_nom_recetas);
        //TODO: Cambiar por la ip de la PC que corre el servicio RestEC en archivo strings.xml
        urlREST = this.getResources().getString(R.string.urlREST);
        //Speech = (TextView)findViewById(R.id.speech);
        listRec = (ListView) findViewById(R.id.listRec);
        nomEjecucion= getIntent().getStringExtra("funcion");
        busquedaEspecifica();
      //  Toast.makeText(getApplicationContext(), "Salistes de la busqueda", Toast.LENGTH_LONG).show();




    }


    private void busquedaEspecifica() {
        String lista = getIntent().getStringExtra("ingredientes");
        //Toast.makeText(getApplicationContext(), lista, Toast.LENGTH_LONG).show();
        AsyncHttpClient ac = new AsyncHttpClient();
        HttpEntity entity = null;

        try {
            entity = new StringEntity(lista);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String url = urlREST + nomEjecucion;
            ac.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        //Status 200 quiere decir que se recibio respuesta
                        if (statusCode == 200) {
                            if (responseBody != null && responseBody.length > 0) {
                                JSONArray res = new JSONArray(new String(responseBody));
                               // Toast.makeText(getApplicationContext(), res.toString(), Toast.LENGTH_LONG).show();
                                if (res.length() == 0) {
                                    //Toast.makeText(getApplicationContext(), "No se encontraron recetas!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                recs = new ArrayList<String>();
                                for (int i = 0; i < res.length(); i++) {
                                    JSONObject obj = new JSONObject(res.get(i).toString());
                                    recs.add(obj.get("nombre").toString());
                                   // Toast.makeText(getApplicationContext(), obj.get("nombre").toString(), Toast.LENGTH_LONG).show();
                                }

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                        R.layout.list_recs, R.id.ItemName, recs);

                                listRec.setAdapter(arrayAdapter);


                            } else {
                                Toast.makeText(getApplicationContext(), "No se encontraron recetas!", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Error onFailure: " + String.valueOf(statusCode) + " : " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

