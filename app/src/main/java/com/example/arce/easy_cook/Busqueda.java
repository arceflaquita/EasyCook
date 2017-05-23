package com.example.arce.easy_cook;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
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

public class Busqueda extends AppCompatActivity {
    private static final int REQUEST_CODE = 1234;
    //TextView Speech;
    ListView listRec;
    String urlREST = "";
    private ListView listaReceta;
    List<String> recs;
    List<String> recId;
    ArrayList titulo = new ArrayList();
    ArrayList imagen = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busqueda);
        //TODO: Cambiar por la ip de la PC que corre el servicio RestEC en archivo strings.xml
        urlREST = this.getResources().getString(R.string.urlREST);
        //Speech = (TextView)findViewById(R.id.speech);
        listRec = (ListView) findViewById(R.id.listRec);
        handleIntent(getIntent());
    }

    public  boolean isConnected()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net!=null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_ins_recetas:
                {
                    Intent rec = new Intent(Busqueda.this, InsReceta.class);
                    startActivity(rec);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //Speech.setText("Buscando receta: " + query);

            JSONObject jo = new JSONObject();
            try {
                jo.put("nombre", query);
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
            String url = urlREST + "/consReceta";
            ac.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        //Status 200 quiere decir que se recibio respuesta
                        if (statusCode == 200) {
                            if(responseBody != null && responseBody.length > 0) {
                                final JSONArray res = new JSONArray(new String(responseBody));
                                //Toast.makeText(getApplicationContext(), res.toString(), Toast.LENGTH_LONG).show();
                                if(res.length() == 0){
                                    Toast.makeText(getApplicationContext(), "No se encontraron recetas!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                recs = new ArrayList<String>();
                                for(int i=0; i < res.length(); i++){
                                    JSONObject obj = new JSONObject(res.get(i).toString());
                                    recs.add(obj.get("nombre").toString());
                                    recId.add(obj.get("id").toString());
                                    //Toast.makeText(getApplicationContext(), obj.get("nombre").toString(), Toast.LENGTH_LONG).show();
                                }

                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                        R.layout.list_recs, R.id.ItemName, recs);

                                listRec.setAdapter(arrayAdapter);
                                listRec.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                        String selRec = recs.get(position);
                                        int idReceta=Integer.valueOf(recId.get(position));
                                        //Toast.makeText(getApplicationContext(), "Receta Seleccionada : " + selRec,   Toast.LENGTH_LONG).show();

                                        JSONObject jo = new JSONObject();
                                        try {

                                            jo.put("id", idReceta);

                                            Intent busq = new Intent(Busqueda.this, RespuestaBusquedaReceta.class);
                                            busq.putExtra("idReceta", jo.toString());
                                            startActivity(busq);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        //Toast.makeText(getApplicationContext(), "Receta Seleccionada : " + selRec, Toast.LENGTH_LONG).show();
                                        //Toast.makeText(getApplicationContext(), "id de receta : " + idReceta, Toast.LENGTH_LONG).show();

                                    }
                                        });



                            }else{
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
                    Toast.makeText(getApplicationContext(), "Error onFailure", Toast.LENGTH_LONG).show();
                }
            });


        }
    }
}
