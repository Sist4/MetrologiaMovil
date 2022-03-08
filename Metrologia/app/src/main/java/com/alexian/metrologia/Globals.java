package com.alexian.metrologia;

import android.app.Application;

/**
 * Created by Ing.Iván on 2017-05-02.
 */

public class Globals extends Application {
    private String codmetrologo ="0";
    private String unidad="g";
    private String formato="%.3f";
    private String ftpServidor = "ftp.260mb.net";
    private String ftpUsuario = "n260m_20319832";
    private String ftpPassword = "Ares1977";


    public String getCodmetrologo(){
        return this.codmetrologo;
    }

    public void setCodmetrologo(String d)
    {
        this.codmetrologo=d;
    }

    public String getUnidad(){
        return this.unidad;
    }

    public void setUnidad(String u)
    {
        this.unidad=u;
    }


    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }


}
