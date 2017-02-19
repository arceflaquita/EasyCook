<<<<<<< HEAD

=======
>>>>>>> 93ce65fe1f48687722dc1303fa919e07c9409217
package com.example.arce.easy_cook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuUser extends AppCompatActivity {
    Button btnBusquedaEspecifica;
    Button btnAgregar;
    Button btnBusqueda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_user);

        btnBusquedaEspecifica= (Button)findViewById(R.id.btnBusEspecifica);
        btnAgregar= (Button)findViewById(R.id.btnAgregar);
        btnBusqueda= (Button)findViewById(R.id.btnBusqueda);
    }

    public void Buscar(View boton) {
        Intent busq = new Intent(MenuUser.this, Busqueda.class);
        startActivity(busq);
    }

    public void Agregar(View boton) {
        Intent busq = new Intent(MenuUser.this, InsReceta.class);
        startActivity(busq);
    }

    public void BuscarPersonalizada(View boton) {
        Intent busq = new Intent(MenuUser.this, MenuUser.class);
        startActivity(busq);
    }
}
