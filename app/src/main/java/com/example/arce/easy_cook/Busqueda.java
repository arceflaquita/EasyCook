package com.example.arce.easy_cook;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Busqueda extends AppCompatActivity {
    private static final int REQUEST_CODE = 1234;
    String urlREST = "";
    String urlImages = "";
    private ListView listaReceta;

    ArrayList id_receta = new ArrayList();
    ArrayList titulo = new ArrayList();
    ArrayList imagen = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busqueda);
        //TODO: Cambiar por la ip de la PC que corre el servicio RestEC en archivo strings.xml
        urlREST = this.getResources().getString(R.string.urlREST);
        urlImages = this.getResources().getString(R.string.urlImages);
        listaReceta = (ListView) findViewById(R.id.listRec);

        listaReceta.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String id_rec = String.valueOf(id_receta.get(position));
                //Toast.makeText(getApplicationContext(), "receta_id: " + id_rec, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), RecetaDetalle.class);
                intent.putExtra("id_rec", id_rec);
                startActivity(intent);
            }
        });
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
        id_receta.clear();
        titulo.clear();
        imagen.clear();

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
                                JSONArray jsonArray = new JSONArray(new String(responseBody));
                                for (int i=0; i<jsonArray.length();i++){
                                    id_receta.add(jsonArray.getJSONObject(i).getString("id"));
                                    titulo.add(jsonArray.getJSONObject(i).getString("nombre"));
                                    imagen.add(jsonArray.getJSONObject(i).getString("image"));
                                }
                                ImagenAdapter ia = new ImagenAdapter(getApplicationContext());
                                listaReceta.setAdapter(ia);
                            }else{
                                alerta("No se encontraron recetas!");
                                //Toast.makeText(getApplicationContext(), "No se encontraron recetas!", Toast.LENGTH_LONG).show();
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

    private class ImagenAdapter extends BaseAdapter {
        Context ctx;
        LayoutInflater layoutInflater;
        SmartImageView smartImageView;
        TextView tvtitulo , tvdescripcion;

        public ImagenAdapter(Context applicationContext) {
            this.ctx = applicationContext;
            layoutInflater = (LayoutInflater)ctx.getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imagen.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup viewgroup = (ViewGroup)layoutInflater.inflate(R.layout.mostrar_receta_item,null);
            smartImageView = (SmartImageView)viewgroup.findViewById(R.id.imagen1);
            tvtitulo = (TextView)viewgroup.findViewById(R.id.tvtitulo);
            String urlfinal = urlImages + File.separator +  imagen.get(position).toString();
            Rect rect = new Rect(smartImageView.getLeft(),smartImageView.getTop(),smartImageView.getRight(),smartImageView.getBottom());
            smartImageView.setImageUrl(urlfinal, rect);
            tvtitulo.setText(titulo.get(position).toString());
            return viewgroup;
        }
    }

    public void alerta(String cadena){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //seleccionamos la cadena a mostrar
        dialogBuilder.setMessage(cadena);
        dialogBuilder.setIcon(android.R.drawable.ic_dialog_info);

        //elegimo un titulo y configuramos para que se pueda quitar
        dialogBuilder.setCancelable(true).setTitle("Warning");

        //mostramos el dialogBuilder
        dialogBuilder.create().show();

    }
}
