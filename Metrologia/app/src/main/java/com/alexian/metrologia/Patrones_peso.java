package com.alexian.metrologia;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ing.Iván on 2017-05-11.
 */

public class Patrones_peso extends AppCompatActivity {

    private BDManager metrologia;
    private SQLiteDatabase db;
    private LinearLayout row3;
    private String valor,uni,nomb;
    private String[] pesas_n={"1","2","2*","5","10","20","20*","50","100",
            "200","200*","500","1000","2000","2000*","5000","10000","20000","500000","1000000"};
    private int[][] pesas={{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};
    private ArrayList<Patrones_peso.check> lista2 = new ArrayList<Patrones_peso.check>();
    private String cert_usado;
    private int[] pesasxcert={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    public static Activity cl_patrones_peso;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patrones_peso);
        {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            cl_patrones_peso=this;

            final String idecombpr=getIntent().getExtras().getString("idecombpr");
            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();
            int j=1;
            row3 = (LinearLayout) findViewById(R.id.lyContenedorPatrones);
            String[] args1=new String[]{idecombpr,"T"};
            Cursor c1=db.rawQuery("select distinct(c.NomCer) " +
                    "from Cert_Balxpro as c,Certificados as Cr " +
                    "where c.IdeComBpr=? and cr.tipcer!=? and c.nomcer=cr.nomcer", args1);
            if (c1.moveToFirst()){
                do {
                    cert_usado=c1.getString(0);
                    TextView tv = new TextView(this);
                    tv.setText(c1.getString(0) + ":");
                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(400,40);
                    layoutParams.setMargins(180,10,0,0);
                    tv.setLayoutParams(layoutParams);
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setTextSize(16);
                    row3.addView(tv);
                    String certi=c1.getString(0);
                    String args2[]=new String[]{certi,"T"};
                    Cursor c2=db.rawQuery("select ValCer, UniCer, NumPzsCer from Certificados where NomCer=? and TipCer!=?", args2);
                    if (c2.moveToFirst()){
                        do {
                            TextView tv1=new TextView(this);
                            uni = c2.getString(1);
                            if (uni.equals("k")){
                                valor = String.valueOf(Integer.valueOf(c2.getString(0))*1000);
                            }else{
                                valor = c2.getString(0);
                            }
                            tv1.setText(valor + " g.:");
                            layoutParams=new LinearLayout.LayoutParams(150,40);
                            layoutParams.setMargins(200,15,0,0);
                            tv1.setLayoutParams(layoutParams);
                            tv1.setTypeface(null, Typeface.BOLD);
                            tv1.setTextSize(14);
                            row3.addView(tv1);
                            Patrones_peso.check c=new  Patrones_peso.check(j,valor,c2.getInt(2),cert_usado);
                            EditText et = new EditText(this);
                            et.setId(c.cod);
                            et.setText(String.valueOf(c2.getInt(2)));
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            layoutParams=new LinearLayout.LayoutParams(100,80);
                            layoutParams.setMargins(360,15,0,0);
                            et.setLayoutParams(layoutParams);
                            et.setTypeface(null, Typeface.BOLD);
                            et.setTextSize(14);
                            row3.addView(et);
                            j++;
                            lista2.add(c);
                            String valor_num = valor;
                            //int valor_num= Integer.valueOf(valor);
                            for(int i=0;i<=19;i++){
                                if (pesas_n[i].equals(valor_num)){
                                    pesas[0][i]=pesas[0][i] + c2.getInt(2);
                                }
                            }
                        } while (c2.moveToNext());
                    }
                } while (c1.moveToNext());
            }
            Button bt=new Button(this);
            bt.setText("Guardar");
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(250,100);
            layoutParams.setMargins(260,10,0,0);
            bt.setLayoutParams(layoutParams);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String borracerts="Delete from pesxpro where IdeComBpr='" + idecombpr + "'";
                    db.execSQL(borracerts);
                    String Str="update balxpro set n1=0," +
                            "n2=0," +
                            "n2a=0," +
                            "n5=0," +
                            "n10=0," +
                            "n20=0," +
                            "n20a=0," +
                            "n50=0," +
                            "n100=0," +
                            "n200=0," +
                            "n200a=0," +
                            "n500=0," +
                            "n1000=0," +
                            "n2000=0," +
                            "n2000a=0," +
                            "n5000=0," +
                            "n10000=0," +
                            "n20000=0," +
                            "n500000=0," +
                            "n1000000=0 where IdeComBpr='" + idecombpr + "';";
                            //"n500000=0 where CodBpr=" + codigobpr + ";";
                    db.execSQL(Str);
                    for (int i =0;i<=19;i++){
                        pesas[1][i]=0;
                    }
                    for (Patrones_peso.check cf:lista2){
                        String nom=cf.nombre;
                        int cod=cf.cod;
                        EditText ets=(EditText)findViewById(cod);
                        String vale=ets.getText().toString();
                        //int valor_num= Integer.valueOf(nom);
                        String valor_num=nom;
                        for(int i=0;i<=19;i++){
                            if (pesas_n[i].equals(valor_num)){
                                int valmax =pesas[0][i];
                                pesas[1][i]=pesas[1][i] + Integer.valueOf(vale);
                                cf.pzs=Integer.valueOf(vale);
                                if (pesas[1][i]>valmax){
                                    Toast.makeText(getApplicationContext(),"No " +
                                            "se puede ingresar un número mayor de pesas a las disponibles. " +
                                            "Por favor revise y corrija las inconsistencias.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        }

                    }

                    Str="Delete from Pesxpro where IdeComBpr='" + idecombpr + "' and TipPxp='I'";
                    db.execSQL(Str);

                    for (Patrones_peso.check cf2:lista2){
                        String cadacert= cf2.certi;
                        String nom2=cf2.nombre;
                        for (int i =0;i<=19;i++){
                            pesasxcert[i]=0;
                        }
                        for (int i =0;i<=19;i++){
                            if (pesas_n[i].equals(nom2)){
                                pesasxcert[i]=cf2.pzs;
                            }else {
                                pesasxcert[i] = 0;
                            }
                        }
                        String paracert= "Insert into Pesxpro values (null," +
                                "'" + idecombpr + "','I','" + cf2.certi + "'," +
                                "" + pesasxcert[0] + "," +
                                "" + pesasxcert[1] + "," +
                                "" + pesasxcert[2] + "," +
                                "" + pesasxcert[3] +"," +
                                "" + pesasxcert[4] + "," +
                                "" + pesasxcert[5] + "," +
                                "" + pesasxcert[6] + "," +
                                "" + pesasxcert[7]  +"," +
                                "" + pesasxcert[8] + "," +
                                "" + pesasxcert[9] + "," +
                                "" + pesasxcert[10] + "," +
                                "" + pesasxcert[11] + "," +
                                "" + pesasxcert[12] + "," +
                                "" + pesasxcert[13] + "," +
                                "" + pesasxcert[14] + "," +
                                "" + pesasxcert[15] + "," +
                                "" + pesasxcert[16] + "," +
                                "" + pesasxcert[17] + "," +
                                "" + pesasxcert[18] + "," +
                                "" + pesasxcert[19] + ",0,0,0,0,0,0,0,0,0,0,0,0,0)";
                        //Toast.makeText(getApplicationContext(),paracert, Toast.LENGTH_SHORT).show();
                        db.execSQL(paracert);

                    }
                    Str="update balxpro set n1=" + pesas[1][0] + "," +
                            "n2=" + pesas[1][1] + "," +
                            "n2a=" + pesas[1][2] + "," +
                            "n5=" + pesas[1][3] +"," +
                            "n10=" + pesas[1][4] + "," +
                            "n20=" + pesas[1][5] + "," +
                            "n20a=" + pesas[1][6] + "," +
                            "n50=" + pesas[1][7]  +"," +
                            "n100=" + pesas[1][8] + "," +
                            "n200=" + pesas[1][9] + "," +
                            "n200a=" + pesas[1][10] + "," +
                            "n500=" + pesas[1][11] + "," +
                            "n1000=" + pesas[1][12] + "," +
                            "n2000=" + pesas[1][13] + "," +
                            "n2000a=" + pesas[1][14] + "," +
                            "n5000=" + pesas[1][15] + "," +
                            "n10000=" + pesas[1][16] + "," +
                            "n20000=" + pesas[1][17] + "," +
                            "n500000=" + pesas[1][18] + "," +
                            "n1000000=" + pesas[1][19] + " where IdeComBpr='" + idecombpr + "';";
                            //"n500000=" + pesas[1][18] + " where CodBpr=" + codigobpr + ";";
                    //Toast.makeText(getApplicationContext(),Str, Toast.LENGTH_SHORT).show();
                    db.execSQL(Str);
                    Toast.makeText(getApplicationContext(),"Información almacenada exitosamente.", Toast.LENGTH_SHORT).show();

                    String[] args = new String[]{idecombpr};
                    String clase="";
                    Cursor c = db.rawQuery(" SELECT ClaBpr FROM Balxpro where IdeComBpr=?", args);
                    if (c.moveToFirst()) {
                        do {
                            clase = c.getString(0);
                        } while (c.moveToNext());
                    }
                    c.close();
/*
                    if (clase.equals("Camionera")){
                        Intent excec_cam = new Intent(Patrones_peso.this,Excentricidad_cam.class);
                        excec_cam.putExtra("codigobpr",codigobpr);
                        excec_cam.putExtra("carga_escogida","");
                        excec_cam.putExtra("unidad","");
                        startActivity(excec_cam);
                    }else{
                        Intent excec_ii_iii = new Intent(Patrones_peso.this,Excentricidad_ii_iii.class);
                        excec_ii_iii.putExtra("codigobpr",codigobpr);
                        excec_ii_iii.putExtra("carga_escogida","");
                        excec_ii_iii.putExtra("unidad","");
                        startActivity(excec_ii_iii);
                    }
*/
                    Intent visual = new Intent(Patrones_peso.this,visuales.class);
                    visual.putExtra("idecombpr",idecombpr);
                    visual.putExtra("clase",clase);
                    startActivity(visual);
                }
            });
            row3.addView(bt);
        }
    }

    class check{
        private int cod;
        private String nombre;
        private int pzs;
        private String certi;

        public check(int cod, String nombre, int pzs, String certi) {
            this.cod = cod;
            this.nombre = nombre;
            this.pzs = pzs;
            this.certi = certi;
        }
    }
}