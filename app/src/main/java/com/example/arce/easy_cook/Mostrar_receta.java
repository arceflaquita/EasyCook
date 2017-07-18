package com.example.arce.easy_cook;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.snowdream.android.widget.SmartImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Arce on 06/05/2017.
 */

public class Mostrar_receta extends AppCompatActivity {
    String urlREST = "";
    String urlImages = "";
    private ListView listaReceta;

    ArrayList id_receta = new ArrayList();
    ArrayList titulo = new ArrayList();
    ArrayList imagen = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_receta);
        urlREST = this.getResources().getString(R.string.urlREST);
        urlImages = this.getResources().getString(R.string.urlImages);
        listaReceta = (ListView)findViewById(R.id.listaRecetas);

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
        descargarImagen();
    }

    private void descargarImagen() {
        id_receta.clear();
        titulo.clear();
        imagen.clear();

        final ProgressDialog progressDialog = new ProgressDialog(Mostrar_receta.this);
        progressDialog.setMessage("Cargando Datos.....");
        progressDialog.show();
        AsyncHttpClient ac = new AsyncHttpClient();
        HttpEntity entity = null;
        try {
            entity = new StringEntity("{}");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = urlREST + "/listarRecetas";
        ac.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
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

    private class ImagenAdapter extends BaseAdapter{
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

            String font_path = "font/Genesis Handwriting.ttf";
            Typeface TF = Typeface.createFromAsset(getAssets(),font_path);
            tvtitulo.setTypeface(TF);
            String urlfinal = urlImages + File.separator +  imagen.get(position).toString();
            Rect rect = new Rect(smartImageView.getLeft(),smartImageView.getTop(),smartImageView.getRight(),smartImageView.getBottom());
            smartImageView.setImageUrl(urlfinal, rect);
            tvtitulo.setText(titulo.get(position).toString());
            return viewgroup;
        }
    }
}