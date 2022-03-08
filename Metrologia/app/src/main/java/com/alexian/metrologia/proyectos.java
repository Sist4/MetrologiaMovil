package com.alexian.metrologia;

import java.util.Date;

/**
 * Created by Ing.Iván on 2017-05-02.
 */

public class proyectos {
    private int codpro;
    private String estpro;
    private Date fecpro;
    private Date fesigcalpro;
    private int codcli;
    private int idepro;
    private int codmet;

    public int getCodpro() {
        return codpro;
    }

    public void setCodpro(int codpro) {
        this.codpro = codpro;
    }

    public String getEstpro() {
        return estpro;
    }

    public void setEstpro(String estpro) {
        this.estpro = estpro;
    }

    public Date getFecpro() {
        return fecpro;
    }

    public void setFecpro(Date fecpro) {
        this.fecpro = fecpro;
    }

    public Date getFesigcalpro() {
        return fesigcalpro;
    }

    public void setFesigcalpro(Date fesigcalpro) {
        this.fesigcalpro = fesigcalpro;
    }

    public int getCodcli() {
        return codcli;
    }

    public void setCodcli(int codcli) {
        this.codcli = codcli;
    }

    public int getIdepro() {
        return idepro;
    }

    public void setIdepro(int idepro) {
        this.idepro = idepro;
    }

    public void setCodmet(int codmet) {
        this.codmet = codmet;
    }

    public String toString()
    {
        return (Integer.toString(idepro));
    }

    public int getCodmet() {
        return codmet;
    }
}
