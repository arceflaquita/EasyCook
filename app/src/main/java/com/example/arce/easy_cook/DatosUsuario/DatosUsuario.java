package com.example.arce.easy_cook.DatosUsuario;

/**
 * Created by SNOOPY on 18/05/2017.
 */

public class DatosUsuario {
    private static int idUsuario;
    private String correo;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public static int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}