package com.example.arce.easy_cook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class RespuestaBusquedaReceta extends AppCompatActivity {
        private TextView txtNombreReceta,txtIngredientes ,txtPreparacion;
        private ImageView imagenReceta;
        private Button btnyotube;
    private Uri imageUri;
    private Intent intent;
    Button compartir;
        String urlREST = "";
    String recs="\n";
    String idReceta="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_respuesta_busqueda_receta);
        //TODO: Cambiar por la ip de la PC que corre el servicio RestEC en archivo strings.xml
        urlREST = this.getResources().getString(R.string.urlREST);
        txtNombreReceta=(TextView) findViewById(R.id.textNombreReceta);
        txtIngredientes=(TextView) findViewById(R.id.textIngredientes);
        txtPreparacion=(TextView) findViewById(R.id.textPreparacion);
        imagenReceta=(ImageView) findViewById(R.id.imagenReceta);
        btnyotube=(Button) findViewById(R.id.youtubeReceta);
        compartir=(Button)findViewById(R.id.compartir);
        idReceta = getIntent().getStringExtra("id_rec");
        busquedaReceta();
        btnyotube.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                videoYoutube(v);
            }


        });

        compartir.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imageUri = Uri.parse("http://177.233.183.16:4848/RestEC/images/receta_1.jpg");

                intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, txtNombreReceta.getText().toString());
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.setType("image/*");
                startActivity(intent);
            }


        });


    }

    private void busquedaReceta() {


        //Toast.makeText(getApplicationContext(), "id de la Receta: "+idReceta, Toast.LENGTH_LONG).show();

            AsyncHttpClient ac = new AsyncHttpClient();
            HttpEntity entity = null;
            try {
                entity = new StringEntity(idReceta);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //TODO: Cambiar por la ip de la PC que corre el servicio RestEC
            String url = urlREST + "/consultaIdReceta";
            ac.post(this, url, entity, "application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {


                        //Status 200 quiere decir que se recibio respuesta
                        if (statusCode == 200) {
                            if(responseBody != null && responseBody.length > 0) {
                                JSONObject res = new JSONObject(new String(responseBody));

                                Object nombreReceta = res.get("nombre");
                                txtNombreReceta.setText(nombreReceta.toString());
                                Object ingredientes=  res.get("ingredientes");
                                JSONArray ress = new JSONArray(new String(String.valueOf(ingredientes)));
                                for (int i = 0; i < ress.length(); i++) {
                                    JSONObject obj = new JSONObject(ress.get(i).toString());

                                    recs+=(i+1)+"º-"+obj.get("nombre").toString()+"\n";
                                }
                                txtIngredientes.setText(recs);
                                Object preparacion = res.get("preparacion");
                                txtPreparacion.setText("\n"+preparacion.toString());


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
                    Toast.makeText(getApplicationContext(), "Error onFailure:  "+error, Toast.LENGTH_LONG).show();
                }
            });


    }

    private void videoYoutube(View v){

        String selRec = txtNombreReceta.getText().toString();
        Toast.makeText(getApplicationContext(), "Receta Seleccionada : " + selRec,   Toast.LENGTH_LONG).show();

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

                            String youtubeURL = "https://www.youtube.com/watch?v=" + video;
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURL));
                            startActivity(browserIntent);
                            //Toast.makeText(getApplicationContext(), video, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            //e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "No se encontraron videos!", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "No se encontraron videos!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Error Youtube", Toast.LENGTH_LONG).show();
            }
        });
    }
}
