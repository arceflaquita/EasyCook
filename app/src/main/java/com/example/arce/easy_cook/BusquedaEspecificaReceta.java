package com.example.arce.easy_cook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.StringEntity;

public class BusquedaEspecificaReceta extends AppCompatActivity {
    private EditText editIngrediente;
    private Button btnAgregar, btnBuscar;
    private ListView listIng;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    float dX, dY;
    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}
    String urlREST = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_especifica_receta);
        //TODO: Cambiar por la ip de la PC que corre el servicio RestEC en archivo strings.xml
        urlREST = this.getResources().getString(R.string.urlREST);
        editIngrediente = (EditText) findViewById(R.id.editIngrediente);
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        listIng = (ListView) findViewById(R.id.listIng);
        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        listIng.setAdapter(adapter);


        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this line adds the data of your EditText and puts in your array
                arrayList.add(editIngrediente.getText().toString());
                // next thing you have to do is check if your adapter has changed
                editIngrediente.setText("");
                adapter.notifyDataSetChanged();
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               busquedaEspecifica(view);
            }
        });

        listIng.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        historicX = event.getX();
                        historicY = event.getY();
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (event.getX() - historicX < -DELTA) {
                            int pos = listIng.pointToPosition((int)historicX, (int)historicY);
                            adapter.remove(adapter.getItem(pos));
                            return true;
                        }
                        /*else if (event.getX() - historicX > DELTA) {
                            FunctionDeleteRowWhenSlidingRight();
                            return true;
                        }*/
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });




    }
    public void busquedaEspecifica(View v) {
        JSONObject jo = new JSONObject();
        try {

            JSONArray ja = new JSONArray();
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) listIng.getAdapter();
            for(int i=0; i<adapter.getCount();i++){
                String item = adapter.getItem(i);
                ja.put(item);
            }
            jo.put("ingredientes", ja);
            Intent busq = new Intent(BusquedaEspecificaReceta.this, ListaNomRecetas.class);
            busq.putExtra("ingredientes", jo.toString());
            busq.putExtra("funcion","/consultaEspecifca");
            startActivity(busq);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
