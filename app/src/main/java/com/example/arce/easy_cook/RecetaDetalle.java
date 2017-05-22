package com.example.arce.easy_cook;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.util.ByteArrayBuffer;

public class RecetaDetalle extends AppCompatActivity {
    ImageView imgFoto;
    TextView tvNombre;
    TextView tvTipo;
    TextView tvPorciones;
    TextView tvIngredientes;
    TextView tvPreparacion;
    VideoView video;
    String urlREST = "";
    String urlImages = "";
    String id_rec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receta_detalle);
        urlREST = this.getResources().getString(R.string.urlREST);
        urlImages = this.getResources().getString(R.string.urlImages);
        id_rec = getIntent().getStringExtra("id_rec");
        imgFoto = (ImageView)findViewById(R.id.imgFoto);
        tvNombre = (TextView)findViewById(R.id.tvNombre);
        tvTipo = (TextView)findViewById(R.id.tvTipo);
        tvPorciones = (TextView)findViewById(R.id.tvPorciones);
        tvIngredientes = (TextView)findViewById(R.id.tvIngredientes);
        tvPreparacion = (TextView)findViewById(R.id.tvPreparacion);
        video = (VideoView) findViewById(R.id.video);
        //evita error android.os.NetworkOnMainThreadException en metodo LoadImageFromWebOperations
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        mostrarInfo(id_rec);
    }

    private void mostrarInfo(String id_rec) {
        final ProgressDialog progressDialog = new ProgressDialog(RecetaDetalle.this);
        progressDialog.setMessage("Cargando Datos.....");
        progressDialog.show();
        JSONObject jo = new JSONObject();
        try {
            jo.put("id", id_rec);
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
        String url = urlREST + "/recetaDetalle";
        ac.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    //Status 200 quiere decir que se recibio respuesta
                    if (statusCode == 200) {
                        progressDialog .dismiss();
                        if(responseBody != null && responseBody.length > 0) {
                            //JSONArray jsonArray = new JSONArray(new String(responseBody));
                            JSONObject json = new JSONObject(new String(responseBody));
                            String url = urlImages + File.separator;
                            Rect rect = new Rect(imgFoto.getLeft(),imgFoto.getTop(),imgFoto.getRight(),imgFoto.getBottom());
                            url += json.getString("image");
                            Bitmap drawable = LoadImageFromWeb(url);
                            imgFoto.setImageBitmap(drawable);
                            tvNombre.setText(json.getString("nombre"));
                            tvTipo.setText(json.getString("comida"));
                            tvPorciones.setText(json.getString("porciones"));
                            tvPreparacion.setText(json.getString("preparacion"));
                            Uri vUri = Uri.parse(json.getString("url_video"));
                            video.setVideoURI(vUri);
                            //video.start();
                            JSONArray ings = json.getJSONArray("ingredientes");
                            for (int i=0; i < ings.length(); i++){
                                tvIngredientes.setText(tvIngredientes.getText() + ings.getJSONObject(i).getString("nombre"));
                                tvIngredientes.setText(tvIngredientes.getText() + "\n");
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: ", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Error onFailure: " + String.valueOf(statusCode) + " : " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Background
    protected Bitmap LoadImageFromWeb(String imageFullURL)
    {
        Bitmap bm = null;
        try {
            URL url = new URL(imageFullURL);
            URLConnection ucon = url.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            bm = BitmapFactory.decodeByteArray(baf.toByteArray(), 0,
                    baf.toByteArray().length);
        } catch (IOException e) {
            //Log.d("ImageManager", "Error: " + e);
        }
        return bm;
    }
}
