package com.example.arce.easy_cook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class BusquedaTipoReceta extends AppCompatActivity {
    private Spinner spnTipoComida;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_tipo_receta);
        spnTipoComida = (Spinner) findViewById(R.id.spnTipoComida);
        ArrayAdapter<CharSequence> tipo_comida_adap = ArrayAdapter.createFromResource(this,
                R.array.tipo_comida_arr, android.R.layout.simple_spinner_item);
        tipo_comida_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoComida.setAdapter(tipo_comida_adap);
    }
}
