package com.alura.literatura.Configuracion.iConfig;

public interface IConvertirDatos {

    <T> T convertirDatosJsonAJava(String json , Class<T> clase);
}
