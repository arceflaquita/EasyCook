package com.example.arce.easy_cook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

public class ListaNomRecetas extends AppCompatActivity {


    String urlREST = "";
    String urlImages = "",nomEjecucion="";
    private ListView listaReceta;

    ArrayList id_receta = new ArrayList();
    ArrayList titulo = new ArrayList();
    ArrayList imagen = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_nom_recetas);
        //TODO: Cambiar por la ip de la PC que corre el servicio RestEC en archivo strings.xml
        urlREST = this.getResources().getString(R.string.urlREST);
        urlImages = this.getResources().getString(R.string.urlImages);
        listaReceta = (ListView) findViewById(R.id.listRec);
        nomEjecucion= getIntent().getStringExtra("funcion");

        listaReceta.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String id_rec = String.valueOf(id_receta.get(position));
                String id_re=String.valueOf(id_rec);
                //Toast.makeText(getApplicationContext(), "receta_id: " + id_rec, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), RecetaDetalle.class);
                intent.putExtra("id_rec", id_re);
                startActivity(intent);
            }
        });

        busquedaEspecifica();




    }


    private void busquedaEspecifica() {
        id_receta.clear();
        titulo.clear();
        imagen.clear();

        String lista = getIntent().getStringExtra("ingredientes");
        //Toast.makeText(getApplicationContext(), lista, Toast.LENGTH_LONG).show();

        final ProgressDialog progressDialog = new ProgressDialog(ListaNomRecetas.this);
        progressDialog.setMessage("Cargando Datos.....");
        progressDialog.show();
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
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) { try {
                //Status 200 quiere decir que se recibio respuesta
                if (statusCode == 200) {
                    progressDialog .dismiss();
                    if(responseBody != null && responseBody.length > 0) {
                        JSONArray jsonArray = new JSONArray(new String(responseBody));
                        for (int i=0; i<jsonArray.length();i++){
                            id_receta.add(jsonArray.getJSONObject(i).getString("id"));
                            titulo.add(jsonArray.getJSONObject(i).getString("nombre"));
                            imagen.add(jsonArray.getJSONObject(i).getString("image"));
                        }
                        ImagenAdapter ia = new ImagenAdapter(getApplicationContext());
                        listaReceta.setAdapter(ia);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Error onFailure: " + String.valueOf(statusCode) + " : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
}
