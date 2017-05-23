package com.example.arce.easy_cook;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusquedaTipoReceta extends AppCompatActivity {
    private Spinner spnTipoComida;
    private Button btnBuscar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_tipo_receta);
        spnTipoComida = (Spinner) findViewById(R.id.spnTipoComida);
        btnBuscar=(Button) findViewById(R.id.btnBuscar);
        ArrayAdapter<CharSequence> tipo_comida_adap = ArrayAdapter.createFromResource(this,
                R.array.tipo_comida_arr, android.R.layout.simple_spinner_item);
        tipo_comida_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoComida.setAdapter(tipo_comida_adap);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if( 0 ==spnTipoComida.getSelectedItemPosition()){
                   // Toast.makeText(getApplicationContext(), "Capture un tipo de comida!", Toast.LENGTH_LONG).show();
                    alerta("Capture un tipo de comida!");
                    return;
                }
                String tipo_comida = String.valueOf(spnTipoComida.getSelectedItemPosition()+1);
                    JSONObject jo = new JSONObject();
                    try {

                        jo.put("tipo_comida", tipo_comida);

                        Intent busq = new Intent(BusquedaTipoReceta.this, ListaNomRecetas.class);
                        busq.putExtra("ingredientes", jo.toString());
                        busq.putExtra("funcion", "/consRecetaTipo");
                        startActivity(busq);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
        });
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
