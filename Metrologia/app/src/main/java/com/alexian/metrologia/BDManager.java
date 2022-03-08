package com.alexian.metrologia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ing.Iván Villavicencio on 2017-04-30.
 */

public class BDManager extends SQLiteOpenHelper {

    String sqlCreate =  "CREATE TABLE Metrologos (CodMet INTEGER PRIMARY KEY AUTOINCREMENT, NomMet TEXT, ClaMet TEXT, inimet TEXT, estMet TEXT)";
    String sqlCreate2 = "CREATE TABLE [Proyectos] (\n" +
            "[CodPro] INTEGER  PRIMARY KEY AUTOINCREMENT NULL,\n" +
            "[EstPro] TEXT  NULL,\n" +
            "[FecPro] DATE  NULL,\n" +
            "[FecSigCalPro] DATE  NULL,\n" +
            "[CodCli] INTEGER  NULL,\n" +
            "[IdePro] INTEGER  NULL,\n" +
            "[CodMet] INTEGER  NULL,\n" +
            "[LocPro] TEXT  NULL\n" +
            ")";
    String sqlCreate3 = " CREATE TABLE [Balxpro] (" +
            "[CodBpr] INTEGER  PRIMARY KEY AUTOINCREMENT NULL," +
            "[NumBpr] INTEGER  NULL," +
            "[DesBpr] TEXT  NULL," +
            "[IdentBpr] TEXT  NULL," +
            "[MarBpr] TEXT  NULL," +
            "[ModBpr] TEXT  NULL," +
            "[SerBpr] TEXT  NULL," +
            "[CapMaxBpr] INTEGER  NULL," +
            "[UbiBpr] TEXT  NULL," +
            "[CapUsoBpr] INTEGER  NULL," +
            "[DivEscBpr] REAL  NULL," +
            "[UniDivEscBpr] TEXT  NULL," +
            "[DivEsc_dBpr] REAL  NULL," +
            "[UniDivEsc_dBpr] TEXT  NULL," +
            "[RanBpr] INTEGER  NULL," +
            "[ClaBpr] TEXT  NULL," +
            "[N1] INTEGER  NULL," +
            "[N2] INTEGER  NULL," +
            "[N2A] INTEGER  NULL," +
            "[N5] INTEGER  NULL," +
            "[N10] INTEGER  NULL," +
            "[N20] INTEGER  NULL," +
            "[N20A] INTEGER  NULL," +
            "[N50] INTEGER  NULL," +
            "[N100] INTEGER  NULL," +
            "[N200] INTEGER  NULL," +
            "[N200A] INTEGER  NULL," +
            "[N500] INTEGER  NULL," +
            "[N1000] INTEGER  NULL," +
            "[N2000] INTEGER  NULL," +
            "[N2000A] INTEGER  NULL," +
            "[N5000] INTEGER  NULL," +
            "[N10000] INTEGER  NULL," +
            "[N20000] INTEGER  NULL," +
            "[N500000] INTEGER  NULL," +
            "[N1000000] INTEGER  NULL," +
            "[BalLimpBpr] TEXT  NULL," +
            "[AjuBpr] TEXT  NULL," +
            "[IRVBpr] TEXT  NULL," +
            "[CodPro] INTEGER  NULL," +
            "[CodMet] Integer  NULL," +
            "[IdeBpr] TEXT  NULL," +
            "[EstBpr] TEXT  NULL," +
            "[LitBpr] TEXT  NULL," +
            "[IdeComBpr] TEXT  NULL," +
            "[RecPorCliBpr] TEXT  NULL," +
            "[DivEscCalBpr] TEXT  NULL," +
            "[CapCalBpr] TEXT  NULL," +
            "[est_esc] TEXT  NULL," +
            "[fec_cal] TEXT  NULL," +
            "[ObsVBpr] TEXT  NULL," +
            "[fec_proxBpr] TEXT  NULL," +
            "[es_adi] INTEGER  NULL," +
            "[lugcalBpr] TEXT  NULL" +
            ")";
    String sqlCreate4 = "CREATE TABLE [Clientes] (\n" +
            "[CodCli] INTEGER  PRIMARY KEY AUTOINCREMENT NULL,\n" +
            "[NomCli] TEXT  NULL,\n" +
            "[CiRucCli] TEXT  NULL,\n" +
            "[CiuCli] TEXT  NULL,\n" +
            "[DirCli] TEXT  NULL,\n" +
            "[EmaCli] TEXT  NULL,\n" +
            "[TelCli] TEXT  NULL,\n" +
            "[ConCli] TEXT  NULL,\n" +
            "[EstCli] TEXT  NULL,\n" +
            "[LugCalCli] TEXT  NULL\n" +
            ")";
    String sqlCreate5 = "CREATE TABLE [Ambientales] (" +
            "[CodAmb] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "[TemIniAmb] REAL  NULL," +
            "[TemFinAmb] REAL  NULL," +
            "[HumRelIniAmb] REAL  NULL," +
            "[HumRelFinAmb] REAL  NULL," +
            "[IdeComBpr] TEXT NULL" +    //"[CodBpr] INTEGER  NULL" +
            ")";
    String sqlCreate6="CREATE TABLE [Cert_Balxpro] (" +
            "[CodCbp] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "[NomCer] TEXT  NULL," +
            "[IdeComBpr] TEXT  NULL" +  //"[CodBpr] INTEGER  NULL" +
            ")";
    String sqlCreate7="CREATE TABLE [Certificados] (" +
            "[CodCer] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "[TipCer] TEXT  NULL," +
            "[NomCer] TEXT  NULL," +
            "[ValCer] TEXT  NULL," +
            "[UniCer] TEXT  NULL," +
            "[NumPzsCer] INTEGER  NULL," +
            "[FecCer] TEXT  NULL," +
            "[IdeCer] TEXT  NULL," +
            "[LocCer] TEXT  NULL," +
            "[EstCer] TEXT  NULL," +
            "[ClaCer] TEXT  NULL" +
            ")";
    String sqlCreate8="CREATE TABLE [ExecCam_Cab] (" +
            "[CodCam_c] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "[CarCam_c] REAL  NULL," +
            "[PrbCam_c] INTEGER  NULL," +
            "[SatCam_c] TEXT  NULL," +
            "[IdeComBpr] TEXT  NULL" +  //"[CodBpr] INTEGER  NULL" +
            ")";
    String sqlCreate9="CREATE TABLE [ExecCam_Det] (\n" +
            "[CodCam_d] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[pos1Cam_d] REAL  NULL,\n" +
            "[pos1rCam_d] REAL  NULL,\n" +
            "[pos2Cam_d] REAL  NULL,\n" +
            "[pos2rCam_d] REAL  NULL,\n" +
            "[pos3Cam_d] REAL  NULL,\n" +
            "[pos3rCam_d] REAL  NULL,\n" +
            "[ExecMaxCam_d] REAL  NULL,\n" +
            "[EmpCam_d] REAL  NULL,\n" +
            "[CodCam_c] TEXT  NULL\n" + //"[CodCam_c] INTEGER  NULL\n" Se cambia el tipo de dato que recibe la columna 09-11-2017 I.V.
            ")";
    String sqlCreate10="CREATE TABLE [ExecII_Cab] (\n" +
            "[CodEii_c] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[CarEii_c] REAL  NULL,\n" +
            "[PrbEii_c] INTEGER  NULL,\n" +
            "[IdeComBpr] TEXT  NULL,\n" +  //"[CodBpr] INTEGER  NULL,\n" +
            "[SatEii_c] TEXT  NULL\n" +
            ")";
    String sqlCreate11="CREATE TABLE [ExecII_Det] (\n" +
            "[CodEii_d] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "[Pos1Eii_d] REAL  NULL,\n" +
            "[Pos2Eii_d] REAL  NULL,\n" +
            "[Pos3Eii_d] REAL  NULL,\n" +
            "[Pos4Eii_d] REAL  NULL,\n" +
            "[Pos5Eii_d] REAL  NULL,\n" +
            "[ExecMaxEii_d] REAL  NULL,\n" +
            "[EmpEii_d] REAL  NULL,\n" +
            "[CodEii_c] TEXT  NULL\n" + //"[CodEii_c] INTEGER  NULL\n" Se cambia el tipo de dato que recibe la columna 09-11-2017 I.V.
            ")";
    String sqlCreate12="CREATE TABLE [PCarga_Cab] (\n" +
            "[CodPca_C] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[CarPca] REAL  NULL,\n" +
            "[NumPca] INTEGER  NULL,\n" +
            "[IdeComBpr] TEXT  NULL,\n" +  //"[CodBpr] INTEGER  NULL,\n" +
            "[SatPca_C] TEXT  NULL\n" +
            ")";
    String sqlCreate13="CREATE TABLE [PCarga_Det] (\n" +
            "[CodPca_D] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[LecAscPca] REAL  NULL,\n" +
            "[LecDscPca] REAL  NULL,\n" +
            "[ErrAscPca] REAL  NULL,\n" +
            "[ErrDscPca] REAL  NULL,\n" +
            "[EmpPca] REAL  NULL,\n" +
            "[CodPca_C] TEXT  NULL,\n" + //"[CodPca_C] INTEGER  NULL,\n" Se cambia el tipo de dato que recibe la columna 09-11-2017 I.V.
            "[SatPca_D] TEXT  NULL\n" +
            ")";
    String sqlCreate14="CREATE TABLE [Pesxpro] (\n" +
            "[CodPxp] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[IdeComBpr] TEXT  NULL,\n" +  //"[CodBpr] INTEGER  NULL,\n" +
            "[TipPxp] TEXT  NULL,\n" +
            "[NonCerPxp] TEXT  NULL,\n" +
            "[N1] INTEGER  NULL,\n" +
            "[N2] INTEGER  NULL,\n" +
            "[N2A] INTEGER  NULL,\n" +
            "[N5] INTEGER  NULL,\n" +
            "[N10] INTEGER  NULL,\n" +
            "[N20] INTEGER  NULL,\n" +
            "[N20A] INTEGER  NULL,\n" +
            "[N50] INTEGER  NULL,\n" +
            "[N100] INTEGER  NULL,\n" +
            "[N200] INTEGER  NULL,\n" +
            "[N200A] INTEGER  NULL,\n" +
            "[N500] INTEGER  NULL,\n" +
            "[N1000] INTEGER  NULL,\n" +
            "[N2000] INTEGER  NULL,\n" +
            "[N2000A] INTEGER  NULL,\n" +
            "[N5000] INTEGER  NULL,\n" +
            "[N10000] INTEGER  NULL,\n" +
            "[N20000] INTEGER  NULL,\n" +
            "[N500000] INTEGER  NULL,\n" +
            "[N1000000] INTEGER  NULL,\n" +
            "[CrgPxp1] REAL  NULL,\n" +
            "[CrgPxp2] REAL  NULL,\n" +
            "[CrgPxp3] REAL  NULL,\n" +
            "[CrgPxp4] REAL  NULL,\n" +
            "[CrgPxp5] REAL  NULL,\n" +
            "[CrgPxp6] REAL  NULL,\n" +
            "[CrgPxp7] REAL  NULL,\n" +
            "[CrgPxp8] REAL  NULL,\n" +
            "[CrgPxp9] REAL  NULL,\n" +
            "[CrgPxp10] REAL  NULL,\n" +
            "[CrgPxp11] REAL  NULL,\n" +
            "[CrgPxp12] REAL  NULL,\n" +
            "[AjsPxp] REAL  NULL\n" +
            ")";
    String sqlCreate15="CREATE TABLE [RepetIII_Cab] (\n" +
            "[CodRiii_C] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "[CarRiii] REAL  NULL,\n" +
            "[DifMaxRiii] REAL  NULL,\n" +
            "[empRiii] REAL  NULL,\n" +
            "[SatRiii] TEXT  NULL,\n" +
            "[IdeComBpr] TEXT  NULL\n" +  //"[CodBpr] INTEGER  NULL\n" +
            ")";
    String sqlCreate16="CREATE TABLE [RepetIII_Det] (\n" +
            "[CodRiii_D] INTEGER  NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "[Lec1] REAL  NULL,\n" +
            "[Lec1_0] REAL  NULL,\n" +
            "[Lec2] REAL  NULL,\n" +
            "[Lec2_0] REAL  NULL,\n" +
            "[Lec3] REAL  NULL,\n" +
            "[Lec3_0] REAL  NULL,\n" +
            "[CodRiii_C] TEXT  NULL\n" +  //"[CodRiii_C] INTEGER  NULL\n" +
            ")";
    String sqlCreate17="CREATE TABLE [RepetII_Cab] (\n" +
            "[CodRii_C] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[CarRii] REAL  NULL,\n" +
            "[DifMaxRii] REAL  NULL,\n" +
            "[empRii] REAL  NULL,\n" +
            "[SatRii] TEXT  NULL,\n" +
            "[IdeComBpr] TEXT  NULL\n" +  //"[CodBpr] INTEGER  NULL\n" +
            ")";
    String sqlCreate18="CREATE TABLE [RepetII_Det] (\n" +
            "[CodRii_D] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[Lec1] REAL  NULL,\n" +
            "[Lec1_0] REAL  NULL,\n" +
            "[Lec2] REAL  NULL,\n" +
            "[Lec2_0] REAL  NULL,\n" +
            "[Lec3] REAL  NULL,\n" +
            "[Lec3_0] REAL  NULL,\n" +
            "[Lec4] REAL  NULL,\n" +
            "[Lec4_0] REAL  NULL,\n" +
            "[Lec5] REAL  NULL,\n" +
            "[Lec5_0] REAL  NULL,\n" +
            "[Lec6] REAL  NULL,\n" +
            "[Lec6_0] REAL  NULL,\n" +
            "[CodRii_C] TEXT  NULL\n" +  //"[CodRii_C] INTEGER  NULL\n" +
            ")";
    String sqlCreate19="CREATE TABLE [Sustxpro] (\n" +
            "[CodSxp] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[IdeComBpr] TEXT  NULL,\n" +  //"[CodBpr] INTEGER  NULL,\n" +
            "[TipSxp] TEXT  NULL,\n" +
            "[CrgSxp1] REAL  NULL,\n" +
            "[CrgSxp2] REAL  NULL,\n" +
            "[CrgSxp3] REAL  NULL,\n" +
            "[CrgSxp4] REAL  NULL,\n" +
            "[CrgSxp5] REAL  NULL,\n" +
            "[CrgSxp6] REAL  NULL,\n" +
            "[CrgSxp7] REAL  NULL,\n" +
            "[CrgSxp8] REAL  NULL,\n" +
            "[CrgSxp9] REAL  NULL,\n" +
            "[CrgSxp10] REAL  NULL,\n" +
            "[Pesxpro_aso] INTEGER  NULL\n" +
            ")";
/*    String sqlCreate21="CREATE TABLE [ftp] (\n" +
            "[Codftp] INTEGER  PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
            "[ftpservidor] TEXT  NULL,\n" +
            "[ftpusuario] TEXT  NULL,\n" +
            "[ftppassword] TEXT  NULL,\n" +
            ")";*/
    String sqlCreate20="Insert into Metrologos values (null,'ADMIN','adm1','AD','A')";


    public BDManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(sqlCreate);
        db.execSQL(sqlCreate2);
        db.execSQL(sqlCreate3);
        db.execSQL(sqlCreate4);
        db.execSQL(sqlCreate5);
        db.execSQL(sqlCreate6);
        db.execSQL(sqlCreate7);
        db.execSQL(sqlCreate8);
        db.execSQL(sqlCreate9);
        db.execSQL(sqlCreate10);
        db.execSQL(sqlCreate11);
        db.execSQL(sqlCreate12);
        db.execSQL(sqlCreate13);
        db.execSQL(sqlCreate14);
        db.execSQL(sqlCreate15);
        db.execSQL(sqlCreate16);
        db.execSQL(sqlCreate17);
        db.execSQL(sqlCreate18);
        db.execSQL(sqlCreate19);
        db.execSQL(sqlCreate20);
        //db.execSQL(sqlCreate21);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
