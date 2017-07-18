package com.example.arce.easy_cook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    WebView mWebView;
    Button compartir;
    private Button btnyotube;

    String urlREST = "";
    String urlImages = "";
    String id_rec;
    public Bitmap imageUri;
    private Intent intent;
    String urlVideo="";

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
        btnyotube=(Button) findViewById(R.id.youtubeReceta);
        //mWebView = (WebView) findViewById(R.id.mWebView);
        compartir=(Button)findViewById(R.id.compartir);

        String font_path = "font/Genesis Handwriting.ttf";  //definimos un STRING con el valor PATH ( o ruta por                                                                                    //donde tiene que buscar ) de nuetra fuente

        Typeface TF = Typeface.createFromAsset(getAssets(),font_path);

        //llamanos a la CLASS TYPEFACE y la definimos con un CREATE desde                                                    //ASSETS con la ruta STRING

        tvNombre.setTypeface(TF);
        tvTipo.setTypeface(TF);
        tvPorciones.setTypeface(TF);
        tvIngredientes.setTypeface(TF);
        tvPreparacion.setTypeface(TF);
        //evita error android.os.NetworkOnMainThreadException en metodo LoadImageFromWebOperations
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        compartir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                //aqui asginamos la accion a realizar
                //importamos las librerias con alt+intro

                imgFoto.buildDrawingCache();
                Bitmap bitmap = imgFoto.getDrawingCache();

                /***** COMPARTIR IMAGEN *****/
                try {
                    File file = new File(getCacheDir(), bitmap + ".jpg");
                    FileOutputStream fOut = null;
                    fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_TEXT, "Receta de EASY-COOK: "+tvNombre.getText());
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    intent.setType("image/jpg");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        btnyotube.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                videoYoutube(v);
            }


        });

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
                            JSONObject json = new JSONObject(new String(responseBody));
                            String url = urlImages + File.separator;
                            Rect rect = new Rect(imgFoto.getLeft(),imgFoto.getTop(),imgFoto.getRight(),imgFoto.getBottom());
                            url += json.getString("image");
                            Bitmap drawable = LoadImageFromWeb(url);
                            imageUri=drawable;
                            imgFoto.setImageBitmap(drawable);
                            tvNombre.setText(json.getString("nombre"));
                            tvTipo.setText(json.getString("comida"));
                            tvPorciones.setText(json.getString("porciones"));
                            tvPreparacion.setText(json.getString("preparacion"));
                            urlVideo = json.getString("url_video");
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

    private void videoYoutube(View v){

        String selRec = tvNombre.getText().toString();
       // Toast.makeText(getApplicationContext(), "Receta Seleccionada : " + selRec,   Toast.LENGTH_LONG).show();

        String yURL = "https://www.googleapis.com/youtube/v3/search?part=id&key=AIzaSyC83ctBM8zQz4tEH0RPboDnR9qkWH1LCZA&maxResults=1&q=" + selRec.replace(" ", "%20");
        AsyncHttpClient yac =  new AsyncHttpClient();
        yac.get(yURL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    if(responseBody != null && responseBody.length > 0) {
                        try {
                            JSONObject yvid = new JSONObject(new String(responseBody));
                            JSONArray yvarr = new JSONArray(yvid.getString("items"));
                            JSONObject youvid = yvarr.getJSONObject(0);
                            String video = youvid.getJSONObject("id").get("videoId").toString();

                            String youtubeURL = urlVideo;
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL));
                            startActivity(browserIntent);
                            //Toast.makeText(getApplicationContext(), video, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            //e.printStackTrace();
                            alerta( "Error: " + e.getMessage(),"Error");
                            //Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        alerta("No se encontraron videos!","Error");
                       // Toast.makeText(getApplicationContext(), "No se encontraron videos!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    alerta("No se encontraron videos!","Error");
                    //Toast.makeText(getApplicationContext(), "No se encontraron videos!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Error Youtube", Toast.LENGTH_LONG).show();
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