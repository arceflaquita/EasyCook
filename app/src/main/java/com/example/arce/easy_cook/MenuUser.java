package com.example.arce.easy_cook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MenuUser extends AppCompatActivity {
    Button btnBusquedaEspecifica;
    Button btnAgregar;
    Button btnBusqueda,btnBusquedaNombre;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);


        btnBusquedaEspecifica= (Button)findViewById(R.id.btnBusEspecifica);
        btnAgregar= (Button)findViewById(R.id.btnAgregar);
        btnBusqueda= (Button)findViewById(R.id.btnBusqueda);
        btnBusquedaNombre=(Button)findViewById(R.id.btnBusquedaNombre);

    }

    private void goLoginScreen() {
        Intent intent=new Intent(this,Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void Buscar(View boton) {
        Intent busq = new Intent(MenuUser.this, BusquedaTipoReceta.class);
        startActivity(busq);
    }

    public void Agregar(View boton) {
        Intent busq = new Intent(MenuUser.this, InsReceta.class);
        startActivity(busq);
    }

    public void BuscarPersonalizada(View boton) {
        Intent busq = new Intent(MenuUser.this, BusquedaEspecificaReceta.class);
        startActivity(busq);
    }
    public void BuscarNombre(View boton) {
        Intent busq = new Intent(MenuUser.this, Busqueda.class);
        startActivity(busq);
    }
}
