package com.example.arce.easy_cook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

public class InsReceta extends AppCompatActivity {

    EditText modoPrep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ins_receta);
        modoPrep = (EditText) findViewById(R.id.editModoPrep);
    }
}
