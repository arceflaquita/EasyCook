package com.example.arce.easy_cook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class InsReceta extends AppCompatActivity {

    private EditText editModoPrep;
    private EditText editIngrediente;
    private Button btnAgregar;
    private ListView list;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private Spinner spnTipoComida;
    private Spinner spnPorciones;

    float historicX = Float.NaN, historicY = Float.NaN;
    static final int DELTA = 50;
    enum Direction {LEFT, RIGHT;}
    float dX, dY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ins_receta);
        editModoPrep = (EditText) findViewById(R.id.editModoPrep);
        editIngrediente = (EditText) findViewById(R.id.editIngrediente);
        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        list = (ListView) findViewById(R.id.listIng);
        spnTipoComida = (Spinner) findViewById(R.id.spnTipoComida);
        spnPorciones = (Spinner) findViewById(R.id.spnPorciones);

        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        list.setAdapter(adapter);

        ArrayAdapter<CharSequence> tipo_comida_adap = ArrayAdapter.createFromResource(this,
                R.array.tipo_comida_arr, android.R.layout.simple_spinner_item);
        tipo_comida_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTipoComida.setAdapter(tipo_comida_adap);

        ArrayAdapter<CharSequence> porciones_adap = ArrayAdapter.createFromResource(this,
                R.array.porciones_arr, android.R.layout.simple_spinner_item);
        porciones_adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPorciones.setAdapter(porciones_adap);

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

        list.setOnTouchListener(new View.OnTouchListener() {
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
                            int pos = list.pointToPosition((int)historicX, (int)historicY);
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


}
