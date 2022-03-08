package com.alexian.metrologia;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ing.Iván on 2017-05-16.
 */

public class SeteoCarga extends AppCompatActivity {

    private void msg(String method, String message)
    {
        Log.d("EXCEPTION: " + method, message);
        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(method); messageBox.setMessage(message); messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }

    private void mensaje(String method, String message)
    {
        Log.d("EXCEPTION: " + method, message);
        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(method); messageBox.setMessage(message); messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }



    private BDManager metrologia;
    private SQLiteDatabase db;
    private LinearLayout row3;
    private String Valpesas[]={"1","2","2*","5","10","20","20*","50","100","200","200*","500","1000","2000","2000*",
            "5000","10000","20000","500000","1000000"};
    //private int Valpesas_n[]={1,2,2,5,10,20,20,50,100,200,200,500,1000,2000,2000,
    //        5000,10000,20000,500000};
    private double Valpesas_n[]={1.00,2.00,2.00,5.00,10.00,20.00,20.00,50.00,100.00,200.00,200.00,500.00,1000.00,2000.00,2000.00,
            5000.00,10000.00,20000.00,500000.00,1000000.00};
    private TextView real,uniprop,unireal,txtadvertencia,txtacepta;
    private ImageButton nopesa;
    private EditText ed;
    private int llevacod1000,llevacod2000,llevacod2000a,llevacod5000,llevacod10000,llevacod20000,llevacod500000,llevacod1000000,llevacod200;
    private int factor=10;
    private ArrayList<check> lista;
    private String certuso;
    private int paragrabar[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private int conteo,cuenta_sust,sust_real; //Se utiliza para el numero de prueba de carga||sust_real lleva el número de carga de sustitució enviada por la prueba de carga;
    private String edta,estado_sw,cgr_sust,ajuste,masa,maspeso;//estado del switch de carga de sustitución en camioneras y clase III y IIII
    private int pasos9=1,pasos12=1,pasos13=1,pasos14=1,pasos15=1,pasos16=1,pasos17=1,pasos18=1,pasos19=1;
    private int uni_pc=0,etapa,seteo;
    private double real_rec;
    private double primera_carga=0.0;
   // private  String CSustitucion,ARequerido,contador_A;

    //variable donde se recibe el valor bandera de la prueba de carga
    private int bandera;
// Variables para las nuevas cargas de sustitucion
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seteo_carga);
        {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            final String carga = getIntent().getExtras().getString("carga");
            final String origen = getIntent().getExtras().getString("origen");
            final String idecombpr = getIntent().getExtras().getString("idecombpr");
            final String unid_uso = getIntent().getExtras().getString("unidad");

          //*******************Nuevas Variables*****************
            final String CSustitucion = getIntent().getExtras().getString("CSustitucion");
            final String ARequerido = getIntent().getExtras().getString("ARequerido");
            final String contador_A = getIntent().getExtras().getString("Contador_Actual");
            final String conteo_P = getIntent().getExtras().getString("conteo");//VERIFICAR SI ESTA VARIABLE NO ESTA ASIENDO NADA
            final int contadorN = getIntent().getExtras().getInt("contadorN");

            //******************************************************


            nopesa=(ImageButton)findViewById(R.id.ibNoPesa);
            txtacepta=(TextView)findViewById(R.id.txtAceptaSinPatron);
            txtadvertencia=(TextView)findViewById(R.id.txtAdvertenciaSinPatron);

            nopesa.setVisibility(View.INVISIBLE);
            txtacepta.setVisibility(View.INVISIBLE);
            txtadvertencia.setVisibility(View.INVISIBLE);

            if ((origen.equals("PruebaCarga"))||(origen.equals("PruebaCarga_dsc"))||(origen.equals("Pcarga_Sust"))){
                conteo=getIntent().getExtras().getInt("conteo");
                estado_sw=getIntent().getExtras().getString("estado_sw");
                cuenta_sust=getIntent().getExtras().getInt("cta_susti");
                cgr_sust=getIntent().getExtras().getString("cgr_sust");
                ajuste=getIntent().getExtras().getString("ajuste");
                masa=getIntent().getExtras().getString("masa");
                maspeso=getIntent().getExtras().getString("maspeso");
                etapa=getIntent().getExtras().getInt("etapa");
                real_rec=getIntent().getExtras().getDouble("real");
                seteo=getIntent().getExtras().getInt("seteo");
                bandera=getIntent().getExtras().getInt("bandera");
                sust_real=cuenta_sust;
                if (cuenta_sust>1) {
                    nopesa.setVisibility(View.VISIBLE);
                    txtacepta.setVisibility(View.VISIBLE);
                    txtadvertencia.setVisibility(View.VISIBLE);
                }
            }
            metrologia=new BDManager(this,"SistMetPrec.db",null,1);
            db = metrologia.getWritableDatabase();
            real=(TextView)findViewById(R.id.lblCargaReal);
            uniprop=(TextView)findViewById(R.id.lblUnid_propuesta);
            uniprop.setText(unid_uso);
            unireal=(TextView)findViewById(R.id.lblUnid_real);
            unireal.setText(unid_uso);

            nopesa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    real.setText(carga);
                    primera_carga=Double.valueOf(carga);
                }
            });

            //Comparamos para llenar la variable que controla si se divide o no por mil
            //de acuerdo a si trabajamos con Kilos o gramos.
            if (unid_uso.equals("kg.")){
                uni_pc=1000;
            }else {
                uni_pc = 1;
            }
            row3 = (LinearLayout) findViewById(R.id.lyCargas);
            lista = new ArrayList<check>();
            final TextView propuesta=(TextView)findViewById(R.id.lblCargaPropuesta);
            propuesta.setText(carga);
            String[] args1=new String[]{idecombpr,"I"};
            Cursor cu=db.rawQuery("select distinct(NonCerPxp) " +
                    "from Pesxpro " +
                    "where IdeComBpr=? and TipPxp=? ", args1);
            if (cu.moveToFirst()) {
                do {
                    TextView tv = new TextView(this);
                    tv.setText(cu.getString(0) + ":");
                    certuso=cu.getString(0);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(400, 40);
                    layoutParams.setMargins(90, 10, 0, 0);
                    tv.setLayoutParams(layoutParams);
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setTextSize(16);
                    row3.addView(tv);
                    String[] args = new String[]{idecombpr,cu.getString(0),"I"};
                    Cursor c1 = db.rawQuery("select n1,n2,n2a,n5,n10,n20,n20a,n50,n100,n200,n200a,n500,n1000,n2000,n2000a,n5000," +
                            "n10000,n20000,n500000,n1000000 " +
                            "from Pesxpro " +
                            "where IdeComBpr=? and NonCerPxp=? and TipPxp=?", args);
                    //int aux =c1.getInt(19);
                    //Toast.makeText(getApplicationContext(),aux, Toast.LENGTH_SHORT).show();
                    if (c1.moveToFirst()) {
                        do {
                            int pesan1 = c1.getInt(0);
                            if (pesan1 != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(0, Valpesas[0], pesan1,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[0] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[0]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[0]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }

                            int pesan2 = c1.getInt(1);
                            if (pesan2 != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(1, Valpesas[1], pesan2,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[1] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[1]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[1]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }


                            int pesan2a = c1.getInt(2);
                            if (pesan2a != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(2, Valpesas[2], pesan2a,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[2] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[2]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[2]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);

                                        }
                                    }
                                });
                            }

                            int pesan5 = c1.getInt(3);
                            if (pesan5 != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(3, Valpesas[3], pesan5,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[3] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[3]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[3]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }

                            int pesan10 = c1.getInt(4);
                            if (pesan10 != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(4, Valpesas[4], pesan10,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[4] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[4]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[4]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }

                            int pesan20 = c1.getInt(5);
                            if (pesan20 != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(5, Valpesas[5], pesan20,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[5] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[5]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[5]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }

                            int pesan20a = c1.getInt(6);
                            if (pesan20a != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(6, Valpesas[6], pesan20a,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[6] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[6]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[6]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }

                            int pesan50 = c1.getInt(7);
                            if (pesan50 != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(7, Valpesas[7], pesan50,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[7] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[7]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[7]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }

                            int pesan100 = c1.getInt(8);
                            if (pesan100 != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(8, Valpesas[8], pesan100,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[8] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[8]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[8]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }

                            final int pesan200 = c1.getInt(9);
                            ed = new EditText(this);
                            if (pesan200 != 0) {
                                ///pesas de 200 no se visualiiza mas de 1 pesa Angel Aucancela
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(9, Valpesas[9], pesan200,certuso);
                                int cod_pasos=9*pasos9;
                                pasos9=pasos9*10;
                                cb.setId(c.cod);
                                cb.setText(Valpesas[9] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                //si se agraga mas de una pesa se visualiza en la tablet una texbox
                                // para mas pesas de 1
                                if (pesan200 >= 2) {
                                    SeteoCarga.text t = new SeteoCarga.text(cod_pasos*factor, Valpesas[9], pesan200);
                                    llevacod200=t.cod;
                                    ed.setId(llevacod200);
                                    ed.setInputType(2);
                                    ed.setText(String.valueOf(pesan200));
                                    ed.setEnabled(true);
                                    layoutParams = new LinearLayout.LayoutParams(100, 80);
                                    layoutParams.setMargins(360, 10, 0, 0);
                                    ed.setLayoutParams(layoutParams);
                                    ed.setTypeface(null, Typeface.BOLD);
                                    ed.setTextSize(16);
                                    row3.addView(ed);
                                }

///                             fin de pesas
                              cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                         if (pesan200 >= 2) {
                                            int elcod=llevacod200;
                                            EditText edt=(EditText)findViewById(elcod);
                                            String es=edt.getText().toString();
                                            if ((es.equals("")) || (Integer.valueOf(edt.getText().toString()) <= 0)){
                                                Toast.makeText(getApplicationContext(), "El valor debe ser un número " +
                                                        "mayor que cero y menor o igual " +
                                                        "a " + String.valueOf(pesan200) + ".", Toast.LENGTH_SHORT).show();
                                                edt.setText(String.valueOf(pesan200));
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (Integer.valueOf(edt.getText().toString()) > pesan200) {
                                                Toast.makeText(getApplicationContext(), "No puede Ingresar un número " +
                                                        "mayor de pesas al disponible: " + String.valueOf(pesan200) + ". " +
                                                        "Por favor corrija e intente nuevamente", Toast.LENGTH_SHORT).show();
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[9] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(false);
                                            } else {
                                                double asuma = (Valpesas_n[9] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(true);
                                            }
                                        }else {


                                            if (cb.isChecked()) {
                                                double asuma = Valpesas_n[9] / uni_pc;
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta = formateado(real.getText().toString());
                                                real.setText(edta);
                                            } else {
                                                double asuma = Valpesas_n[9] / uni_pc;
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta = formateado(real.getText().toString());
                                                real.setText(edta);
                                            }
                                        }
                                        }
                                });
                            }

                            int pesan200a = c1.getInt(10);
                            if (pesan200a != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(10, Valpesas[10], pesan200a,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[10] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[10]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[10]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }

                            int pesan500 = c1.getInt(11);
                            if (pesan500 != 0) {
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(11, Valpesas[11], pesan500,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[11] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (cb.isChecked()) {
                                            double asuma = Valpesas_n[11]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        } else {
                                            double asuma = Valpesas_n[11]/uni_pc;
                                            double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                            real.setText(String.valueOf(suma));
                                            edta =formateado(real.getText().toString());
                                            real.setText(edta);
                                        }
                                    }
                                });
                            }

                            final int pesan1000 = c1.getInt(12);
                            ed = new EditText(this);
                            if (pesan1000 != 0) {
                                int cod_pasos=12*pasos12;
                                pasos12=pasos12*10;
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(cod_pasos, Valpesas[12], pesan1000,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[12] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                if (pesan1000 >= 2) {
                                    SeteoCarga.text t = new SeteoCarga.text(cod_pasos*factor, Valpesas[12], pesan1000);
                                    llevacod1000=t.cod;
                                    ed.setId(llevacod1000);
                                    ed.setInputType(2);
                                    ed.setText(String.valueOf(pesan1000));
                                    ed.setEnabled(true);
                                    layoutParams = new LinearLayout.LayoutParams(100, 80);
                                    layoutParams.setMargins(360, 10, 0, 0);
                                    ed.setLayoutParams(layoutParams);
                                    ed.setTypeface(null, Typeface.BOLD);
                                    ed.setTextSize(16);
                                    row3.addView(ed);
                                }
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (pesan1000 >= 2) {
                                            int elcod=llevacod1000;
                                            EditText edt=(EditText)findViewById(elcod);
                                            String es=edt.getText().toString();
                                            if ((es.equals("")) || (Integer.valueOf(edt.getText().toString()) <= 0)){
                                                Toast.makeText(getApplicationContext(), "El valor debe ser un número " +
                                                        "mayor que cero y menor o igual " +
                                                        "a " + String.valueOf(pesan1000) + ".", Toast.LENGTH_SHORT).show();
                                                edt.setText(String.valueOf(pesan1000));
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (Integer.valueOf(edt.getText().toString()) > pesan1000) {
                                                Toast.makeText(getApplicationContext(), "No puede Ingresar un número " +
                                                        "mayor de pesas al disponible: " + String.valueOf(pesan1000) + ". " +
                                                        "Por favor corrija e intente nuevamente", Toast.LENGTH_SHORT).show();
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[12] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(false);
                                            } else {
                                                double asuma = (Valpesas_n[12] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(true);
                                            }
                                        } else {
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[12]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            } else {
                                                double asuma = (Valpesas_n[12]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            }
                                        }
                                    }
                                });
                            }

                            final int pesan2000 = c1.getInt(13);
                            ed = new EditText(this);
                            if (pesan2000 != 0) {
                                int cod_pasos=13*pasos13;
                                pasos13=pasos13*10;
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(cod_pasos, Valpesas[13], pesan2000,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[13] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                if (pesan2000 >= 2) {
                                    SeteoCarga.text t = new SeteoCarga.text(cod_pasos*factor, Valpesas[13], pesan2000);
                                    llevacod2000=t.cod;
                                    ed.setId(llevacod2000);
                                    ed.setInputType(2);
                                    ed.setText(String.valueOf(pesan2000));
                                    ed.setEnabled(true);
                                    layoutParams = new LinearLayout.LayoutParams(100, 80);
                                    layoutParams.setMargins(360, 10, 0, 0);
                                    ed.setLayoutParams(layoutParams);
                                    ed.setTypeface(null, Typeface.BOLD);
                                    ed.setTextSize(16);
                                    row3.addView(ed);
                                }
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (pesan2000 >= 2) {
                                            int elcod=llevacod2000;
                                            EditText edt=(EditText)findViewById(elcod);
                                            String es=edt.getText().toString();
                                            if ((es.equals("")) || (Integer.valueOf(edt.getText().toString()) <= 0)){
                                                Toast.makeText(getApplicationContext(), "El valor debe ser un número " +
                                                        "mayor que cero y menor o igual " +
                                                        "a " + String.valueOf(pesan2000) + ".", Toast.LENGTH_SHORT).show();
                                                edt.setText(String.valueOf(pesan2000));
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (Integer.valueOf(edt.getText().toString()) > pesan2000) {
                                                Toast.makeText(getApplicationContext(), "No puede Ingresar un número " +
                                                        "mayor de pesas al disponible: " + String.valueOf(pesan2000) + ". " +
                                                        "Por favor corrija e intente nuevamente", Toast.LENGTH_SHORT).show();
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[13] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(false);
                                            } else {
                                                double asuma = (Valpesas_n[13] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(true);
                                            }
                                        } else {
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[13]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            } else {
                                                double asuma = (Valpesas_n[13]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            }
                                        }
                                    }
                                });
                            }

                            final int pesan2000a = c1.getInt(14);
                            ed = new EditText(this);
                            if (pesan2000a != 0) {
                                int cod_pasos=14*pasos14;
                                pasos14=pasos14*10;
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(cod_pasos, Valpesas[14], pesan2000a,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[14] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(200, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                if (pesan2000a >= 2) {
                                    SeteoCarga.text t = new SeteoCarga.text(cod_pasos*factor, Valpesas[14], pesan2000a);
                                    llevacod2000a=t.cod;
                                    ed.setId(llevacod2000a);
                                    ed.setInputType(2);
                                    ed.setText(String.valueOf(pesan2000a));
                                    ed.setEnabled(true);
                                    layoutParams = new LinearLayout.LayoutParams(100, 80);
                                    layoutParams.setMargins(360, 10, 0, 0);
                                    ed.setLayoutParams(layoutParams);
                                    ed.setTypeface(null, Typeface.BOLD);
                                    ed.setTextSize(16);
                                    row3.addView(ed);
                                }
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (pesan2000a >= 2) {
                                            int elcod=llevacod2000a;
                                            EditText edt=(EditText)findViewById(elcod);
                                            String es=edt.getText().toString();
                                            if ((es.equals("")) || (Integer.valueOf(edt.getText().toString()) <= 0)){
                                                Toast.makeText(getApplicationContext(), "El valor debe ser un número " +
                                                        "mayor que cero y menor o igual " +
                                                        "a " + String.valueOf(pesan2000a) + ".", Toast.LENGTH_SHORT).show();
                                                edt.setText(String.valueOf(pesan2000a));
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (Integer.valueOf(edt.getText().toString()) > pesan2000a) {
                                                Toast.makeText(getApplicationContext(), "No puede Ingresar un número " +
                                                        "mayor de pesas al disponible: " + String.valueOf(pesan2000a) + ". " +
                                                        "Por favor corrija e intente nuevamente", Toast.LENGTH_SHORT).show();
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[14] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(false);
                                            } else {
                                                double asuma = (Valpesas_n[14] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(true);
                                            }
                                        } else {
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[14]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            } else {
                                                double asuma = (Valpesas_n[14]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            }
                                        }
                                    }
                                });
                            }

                            final int pesan5000 = c1.getInt(15);
                            ed = new EditText(this);
                            if (pesan5000 != 0) {
                                int cod_pasos=15*pasos15;
                                pasos15=pasos15*10;
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(cod_pasos, Valpesas[15], pesan5000,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[15] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(100, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                if (pesan5000 >= 2) {
                                    SeteoCarga.text t = new SeteoCarga.text(cod_pasos*factor, Valpesas[15], pesan5000);
                                    llevacod5000=t.cod;
                                    ed.setId(llevacod5000);
                                    ed.setInputType(2);
                                    ed.setText(String.valueOf(pesan5000));
                                    ed.setEnabled(true);
                                    layoutParams = new LinearLayout.LayoutParams(100, 80);
                                    layoutParams.setMargins(360, 10, 0, 0);
                                    ed.setLayoutParams(layoutParams);
                                    ed.setTypeface(null, Typeface.BOLD);
                                    ed.setTextSize(16);
                                    row3.addView(ed);
                                }
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (pesan5000 >= 2) {
                                            int elcod=llevacod5000;
                                            EditText edt=(EditText)findViewById(elcod);
                                            String es=edt.getText().toString();
                                            if ((es.equals("")) || (Integer.valueOf(edt.getText().toString()) <= 0)){
                                                Toast.makeText(getApplicationContext(), "El valor debe ser un número " +
                                                        "mayor que cero y menor o igual " +
                                                        "a " + String.valueOf(pesan5000) + ".", Toast.LENGTH_SHORT).show();
                                                edt.setText(String.valueOf(pesan5000));
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (Integer.valueOf(edt.getText().toString()) > pesan5000) {
                                                Toast.makeText(getApplicationContext(), "No puede Ingresar un número " +
                                                        "mayor de pesas al disponible: " + String.valueOf(pesan5000) + ". " +
                                                        "Por favor corrija e intente nuevamente", Toast.LENGTH_SHORT).show();
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[15] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma) ;
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(false);
                                            } else {
                                                double asuma = (Valpesas_n[15] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(true);
                                            }
                                        } else {
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[15]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            } else {
                                                double asuma = (Valpesas_n[15]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            }
                                        }
                                    }
                                });
                            }

                            final int pesan10000 = c1.getInt(16);
                            ed = new EditText(this);
                            if (pesan10000 != 0) {
                                int cod_pasos=16*pasos16;
                                pasos16=pasos16*10;
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(cod_pasos, Valpesas[16], pesan10000,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[16] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(100, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                if (pesan10000 >= 2) {
                                    SeteoCarga.text t = new SeteoCarga.text(cod_pasos*factor, Valpesas[16], pesan10000);
                                    llevacod10000=t.cod;
                                    ed.setId(llevacod10000);
                                    ed.setInputType(2);
                                    ed.setText(String.valueOf(pesan10000));
                                    ed.setEnabled(true);
                                    layoutParams = new LinearLayout.LayoutParams(100, 80);
                                    layoutParams.setMargins(360, 10, 0, 0);
                                    ed.setLayoutParams(layoutParams);
                                    ed.setTypeface(null, Typeface.BOLD);
                                    ed.setTextSize(16);
                                    row3.addView(ed);
                                }
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (pesan10000 >= 2) {
                                            int elcod=llevacod10000;
                                            EditText edt=(EditText)findViewById(elcod);
                                            String es=edt.getText().toString();
                                            if ((es.equals("")) || (Integer.valueOf(edt.getText().toString()) <= 0)){
                                                Toast.makeText(getApplicationContext(), "El valor debe ser un número " +
                                                        "mayor que cero y menor o igual " +
                                                        "a " + String.valueOf(pesan10000) + ".", Toast.LENGTH_SHORT).show();
                                                edt.setText(String.valueOf(pesan10000));
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (Integer.valueOf(edt.getText().toString()) > pesan10000) {
                                                Toast.makeText(getApplicationContext(), "No puede Ingresar un número " +
                                                        "mayor de pesas al disponible: " + String.valueOf(pesan10000) + ". " +
                                                        "Por favor corrija e intente nuevamente", Toast.LENGTH_SHORT).show();
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[16] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(false);
                                            } else {
                                                double asuma = (Valpesas_n[16] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(true);
                                            }
                                        } else {
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[16]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            } else {
                                                double asuma = (Valpesas_n[16]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            }
                                        }

                                    }
                                });
                            }

                            final int pesan20000 = c1.getInt(17);
                            ed = new EditText(this);
                            if (pesan20000 != 0) {
                                int cod_pasos=17*pasos17;
                                pasos17=pasos17*10;
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(cod_pasos, Valpesas[17], pesan20000,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[17] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(100, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                if (pesan20000 >= 2) {
                                    SeteoCarga.text t = new SeteoCarga.text(cod_pasos*factor, Valpesas[17], pesan20000);
                                    llevacod20000=t.cod;
                                    ed.setId(llevacod20000);
                                    ed.setInputType(2);
                                    ed.setText(String.valueOf(pesan20000));
                                    ed.setEnabled(true);
                                    layoutParams = new LinearLayout.LayoutParams(100, 80);
                                    layoutParams.setMargins(360, 10, 0, 0);
                                    ed.setLayoutParams(layoutParams);
                                    ed.setTypeface(null, Typeface.BOLD);
                                    ed.setTextSize(16);
                                    row3.addView(ed);
                                }
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (pesan20000 >= 2) {
                                            int elcod=llevacod20000;
                                            EditText edt=(EditText)findViewById(elcod);
                                            String es=edt.getText().toString();
                                            if ((es.equals("")) || (Integer.valueOf(edt.getText().toString()) <= 0)){
                                                Toast.makeText(getApplicationContext(), "El valor debe ser un número " +
                                                        "mayor que cero y menor o igual " +
                                                        "a " + String.valueOf(pesan20000) + ".", Toast.LENGTH_SHORT).show();
                                                edt.setText(String.valueOf(pesan20000));
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (Integer.valueOf(edt.getText().toString()) > pesan20000) {
                                                Toast.makeText(getApplicationContext(), "No puede Ingresar un número " +
                                                        "mayor de pesas al disponible: " + String.valueOf(pesan20000) + ". " +
                                                        "Por favor corrija e intente nuevamente", Toast.LENGTH_SHORT).show();
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[17] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(false);
                                            } else {
                                                double asuma = (Valpesas_n[17] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(true);
                                            }
                                        } else {
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[17]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            } else {
                                                double asuma = (Valpesas_n[17]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            }
                                        }
                                    }
                                });
                            }

                            final int pesan500000 = c1.getInt(18);
                            ed = new EditText(this);
                            if (pesan500000 != 0) {
                                int cod_pasos=18*pasos18;
                                pasos18=pasos18*10;
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(cod_pasos, Valpesas[18], pesan500000,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[18] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(100, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                if (pesan500000 >= 2) {
                                    SeteoCarga.text t = new SeteoCarga.text(cod_pasos*factor, Valpesas[18], pesan500000);
                                    llevacod500000=t.cod;
                                    ed.setInputType(2);
                                    ed.setId(llevacod500000);
                                    ed.setText(String.valueOf(pesan500000));
                                    ed.setEnabled(true);
                                    layoutParams = new LinearLayout.LayoutParams(100, 80);
                                    layoutParams.setMargins(360, 10, 0, 0);
                                    ed.setLayoutParams(layoutParams);
                                    ed.setTypeface(null, Typeface.BOLD);
                                    ed.setTextSize(16);
                                    row3.addView(ed);
                                }
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (pesan500000 >= 2) {
                                            int elcod=llevacod500000;
                                            EditText edt=(EditText)findViewById(elcod);
                                            String es=edt.getText().toString();
                                            if ((es.equals("")) || (Integer.valueOf(edt.getText().toString()) <= 0)){
                                                Toast.makeText(getApplicationContext(), "El valor debe ser un número " +
                                                        "mayor que cero y menor o igual " +
                                                        "a " + String.valueOf(pesan500000) + ".", Toast.LENGTH_SHORT).show();
                                                edt.setText(String.valueOf(pesan500000));
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (Integer.valueOf(edt.getText().toString()) > pesan500000) {
                                                Toast.makeText(getApplicationContext(), "No puede Ingresar un número " +
                                                        "mayor de pesas al disponible: " + String.valueOf(pesan500000) + ". " +
                                                        "Por favor corrija e intente nuevamente", Toast.LENGTH_SHORT).show();
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[18] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(false);
                                            } else {
                                                double asuma = (Valpesas_n[18] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(true);
                                            }
                                        } else {
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[18]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            } else {
                                                double asuma = (Valpesas_n[18]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            }
                                        }
                                    }
                                });
                            }

                            //*Adición de código para peses de 1 tn 03-04-2019
                            final int pesan1000000 = c1.getInt(19);
                            ed = new EditText(this);
                            if (pesan1000000 != 0) {
                                int cod_pasos=19*pasos19;
                                pasos19=pasos19*10;
                                final CheckBox cb = new CheckBox(this);
                                SeteoCarga.check c = new SeteoCarga.check(cod_pasos, Valpesas[19], pesan1000000,certuso);
                                cb.setId(c.cod);
                                cb.setText(Valpesas[19] + " g.");
                                layoutParams = new LinearLayout.LayoutParams(250, 40);
                                layoutParams.setMargins(100, 10, 0, 0);
                                cb.setLayoutParams(layoutParams);
                                cb.setTypeface(null, Typeface.BOLD);
                                cb.setTextSize(16);
                                lista.add(c);
                                row3.addView(cb);
                                if (pesan1000000 >= 2) {
                                    SeteoCarga.text t = new SeteoCarga.text(cod_pasos*factor, Valpesas[19], pesan1000000);
                                    llevacod1000000=t.cod;
                                    ed.setInputType(2);
                                    ed.setId(llevacod1000000);
                                    ed.setText(String.valueOf(pesan1000000));
                                    ed.setEnabled(true);
                                    layoutParams = new LinearLayout.LayoutParams(100, 80);
                                    layoutParams.setMargins(360, 10, 0, 0);
                                    ed.setLayoutParams(layoutParams);
                                    ed.setTypeface(null, Typeface.BOLD);
                                    ed.setTextSize(16);
                                    row3.addView(ed);
                                }
                                cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (pesan1000000 >= 2) {
                                            int elcod=llevacod1000000;
                                            EditText edt=(EditText)findViewById(elcod);
                                            String es=edt.getText().toString();
                                            if ((es.equals("")) || (Integer.valueOf(edt.getText().toString()) <= 0)){
                                                Toast.makeText(getApplicationContext(), "El valor debe ser un número " +
                                                        "mayor que cero y menor o igual " +
                                                        "a " + String.valueOf(pesan1000000) + ".", Toast.LENGTH_SHORT).show();
                                                edt.setText(String.valueOf(pesan1000000));
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (Integer.valueOf(edt.getText().toString()) > pesan1000000) {
                                                Toast.makeText(getApplicationContext(), "No puede Ingresar un número " +
                                                        "mayor de pesas al disponible: " + String.valueOf(pesan1000000) + ". " +
                                                        "Por favor corrija e intente nuevamente", Toast.LENGTH_SHORT).show();
                                                cb.setChecked(false);
                                                return;
                                            }
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[19] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(false);
                                            } else {
                                                double asuma = (Valpesas_n[19] * Double.valueOf(edt.getText().toString())/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                                edt.setEnabled(true);
                                            }
                                        } else {
                                            if (cb.isChecked()) {
                                                double asuma = (Valpesas_n[19]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) + asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            } else {
                                                double asuma = (Valpesas_n[19]/uni_pc);
                                                double suma = (Double.valueOf(real.getText().toString()) - asuma);
                                                real.setText(String.valueOf(suma));
                                                edta =formateado(real.getText().toString());
                                                real.setText(edta);
                                            }
                                        }
                                    }
                                });
                            }
                            //*
                        } while (c1.moveToNext());
                    }
                } while (cu.moveToNext());

                Button bt=new Button(this);
                bt.setText("Guardar");
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(250,100);
                layoutParams.setMargins(260,10,0,0);
                bt.setLayoutParams(layoutParams);
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double sugerida=Double.valueOf(propuesta.getText().toString());
                        double escogida=Double.valueOf(real.getText().toString());
                        //Cambio realizado el 20-08-2018 por petición de Wilson Díaz.
                        //Se cambia la tolerancia del 50% al 100%
                        //--->double tolerancia=(((sugerida)*50)/100);
                        double tolerancia=(((sugerida)*100)/100);
                        if (escogida < (sugerida-tolerancia)){
                            Toast.makeText(getApplicationContext(),"La carga escogida está por debajo del 50%" +
                                    " de tolerancia permitida. Mínima carga permitida: " + (sugerida-tolerancia) + ".", Toast.LENGTH_SHORT).show();
                            return;
                        }else if (escogida > (sugerida+tolerancia)){
                            Toast.makeText(getApplicationContext(),"La carga escogida está por encima del 25%" +
                                    " de tolerancia permitida. Máxima carga permitida: " + (sugerida+tolerancia) + ".", Toast.LENGTH_SHORT).show();
                            return;
                        }else {

                            String vienede="";
                            if(origen.equals("Excentricidad_II(1)"))
                                vienede="EII1";
                            else if (origen.equals("Excentricidad_II(2)"))
                                vienede="EII2";
                            else if ((origen.equals("PruebaCarga"))||(origen.equals("Pcarga_Sust")))
                                vienede="C" + conteo + "+";
                            else if (origen.equals("PruebaCarga_dsc"))
                                vienede="C" + conteo + "-";
                            else if (origen.equals("repetibilidad_ii"))
                                vienede="RPII";
                            else if (origen.equals("Excentricidad_cam(1)"))
                                vienede="ECA1";
                            else if (origen.equals("Excentricidad_cam(2)"))
                                vienede="ECA2";
                            else if (origen.equals("repetibilidad_iii"))
                                vienede="RPIII";


                            String borraCerts="Delete from Pesxpro where IdeComBpr='" + idecombpr + "' and TipPxp = '" + vienede + "'";
                            db.execSQL(borraCerts);

                            for (int i=0 ; i<=19 ; i++){
                                paragrabar[i]=0;
                            }

                            for (check c:lista){
                                int cod = c.cod;
                                int cod2 = c.cod*10;
                                String Certif=c.cert;
                                String Pesa=c.nombre;
                                int NumeroPesas=0;
                                CheckBox cbt = (CheckBox)findViewById(cod);
                                if (cbt.isChecked()){
                                    int pzs_final=c.pzs;
                                    if (pzs_final >= 2){
                                        try
                                            {
                                        EditText edtt=(EditText)findViewById(cod2);

                                        if(NumeroPesas == 0)
                                        {
                                            //NumeroPesas=1;
                                            NumeroPesas = Integer.valueOf(edtt.getText().toString());

                                        }
                                        else {
                                            NumeroPesas = Integer.valueOf(edtt.getText().toString());
                                        }
                                        }catch (Exception e)
                                                {
                                                    mensaje("ERROR",e.getMessage());
                                                }
                                    }else{
                                        NumeroPesas=1;
                                    }
                                }
                                for ( int i=0;i<=19;i++){
                                    if (Pesa.equals(Valpesas[i])){
                                        paragrabar[i]=NumeroPesas;
                                    }else{
                                        paragrabar[i]=0;
                                    }
                                }

                                String sustituciones="";

                                switch (cuenta_sust) {
                                    case 0:
                                        sustituciones = ",0,0,0,0,0,0,0,0,0,0,0,0,0)";
                                        break;
                                    case 1:
                                        sustituciones = "," + real_rec + ",0,0,0,0,0,0,0,0,0,0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 2:
                                        sustituciones = "," + primera_carga + "," + real_rec + ",0,0,0,0,0,0,0,0,0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 3:
                                        sustituciones = "," + primera_carga + ",0," + real_rec + ",0,0,0,0,0,0,0,0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 4:
                                        sustituciones = "," + primera_carga + ",0,0," + real_rec + ",0,0,0,0,0,0,0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 5:
                                        sustituciones = "," + primera_carga + ",0,0,0," + real_rec + ",0,0,0,0,0,0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 6:
                                        sustituciones = "," + primera_carga + ",0,0,0,0," + real_rec + ",0,0,0,0,0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 7:
                                        sustituciones = "," + primera_carga + ",0,0,0,0,0," + real_rec + ",0,0,0,0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 8:
                                        sustituciones = "," + primera_carga + ",0,0,0,0,0,0," + real_rec + ",0,0,0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 9:
                                        sustituciones = "," + primera_carga + ",0,0,0,0,0,0,0," + real_rec + ",0,0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 10:
                                        sustituciones = "," + primera_carga + ",0,0,0,0,0,0,0,0," + real_rec + ",0,0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 11:
                                        sustituciones = "," + primera_carga + ",0,0,0,0,0,0,0,0,0," + real_rec + ",0," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                    case 12:
                                        sustituciones = "," + primera_carga + ",0,0,0,0,0,0,0,0,0,0," + real_rec + "," + ajuste + ")";
                                        cuenta_sust=0;
                                        break;
                                }

                                //Toast.makeText(getApplicationContext(), sustituciones, Toast.LENGTH_SHORT).show();
                                try {
                                    String Str = "Insert into Pesxpro values (null," +
                                            "'" + idecombpr + "','" + vienede + "','" + Certif + "'," +
                                            "" + paragrabar[0] + "," +
                                            "" + paragrabar[1] + "," +
                                            "" + paragrabar[2] + "," +
                                            "" + paragrabar[3] + "," +
                                            "" + paragrabar[4] + "," +
                                            "" + paragrabar[5] + "," +
                                            "" + paragrabar[6] + "," +
                                            "" + paragrabar[7] + "," +
                                            "" + paragrabar[8] + "," +
                                            "" + paragrabar[9] + "," +
                                            "" + paragrabar[10] + "," +
                                            "" + paragrabar[11] + "," +
                                            "" + paragrabar[12] + "," +
                                            "" + paragrabar[13] + "," +
                                            "" + paragrabar[14] + "," +
                                            "" + paragrabar[15] + "," +
                                            "" + paragrabar[16] + "," +
                                            "" + paragrabar[17] + "," +
                                            "" + paragrabar[18] + "," +
                                            "" + paragrabar[19] + sustituciones;
                                    //"" + paragrabar[18] + ",0,0,0,0,0,0,0,0,0,0,0,0,0)";
                                    db.execSQL(Str);

                                }catch(Exception e){
                                    msg("ERROR AL GUARDAR",e.getMessage());
                                }
                                //Toast.makeText(getApplicationContext(),Str, Toast.LENGTH_SHORT).show();
                            }

                            String edita=formateado(real.getText().toString());
                            if(origen.equals("Excentricidad_II(1)")) {

                                Intent excec_ii_iii = new Intent(SeteoCarga.this, Excentricidad_ii_iii.class);
                                excec_ii_iii.putExtra("idecombpr", idecombpr);
                                excec_ii_iii.putExtra("unidad",unid_uso);
                                //excec_ii_iii.putExtra("carga_escogida",real.getText().toString());
                                excec_ii_iii.putExtra("carga_escogida",edita);
                                startActivity(excec_ii_iii);
                                finish();
                            }

                            if(origen.equals("Excentricidad_II(2)")) {
                                Intent excec_ii_iii_2 = new Intent(SeteoCarga.this, Excentricidad_ii_iii_2.class);
                                excec_ii_iii_2.putExtra("idecombpr", idecombpr);
                                //excec_ii_iii_2.putExtra("carga_escogida",real.getText().toString());
                                excec_ii_iii_2.putExtra("carga_escogida",edita);
                                excec_ii_iii_2.putExtra("unidad",unid_uso);
                                startActivity(excec_ii_iii_2);
                                finish();
                            }

                            if (origen.equals("PruebaCarga")) {
                                try {
                                    //Toast.makeText(getApplicationContext(), String.valueOf(sust_real) , Toast.LENGTH_SHORT).show();
                                    Intent PCarga = new Intent(SeteoCarga.this, Carga.class);
                                    PCarga.putExtra("idecombpr", idecombpr);
                                    //PCarga.putExtra("carga_escogida",real.getText().toString());
                                    PCarga.putExtra("carga_escogida", edita);
                                    PCarga.putExtra("conteo", conteo);
                                    PCarga.putExtra("unidad", unid_uso);
                                    PCarga.putExtra("estado_sw", estado_sw);
                                    //PCarga.putExtra("cta_susti", cuenta_sust);
                                    PCarga.putExtra("cta_susti", sust_real);
                                    PCarga.putExtra("seteo", 2);
                                    PCarga.putExtra("bandera", bandera);
                                    //PCarga.putExtra("asociada",cod_aso);
                                    startActivity(PCarga);
                                    finish();
                                } catch (Exception e){
                                    msg("error de envio:",e.getMessage());

                                }
                            }

                            if (origen.equals("PruebaCarga_dsc")){
                                Intent PCarga_dsc = new Intent(SeteoCarga.this, Carga_dsc.class);
                                PCarga_dsc.putExtra("idecombpr", idecombpr);
                                //PCarga_dsc.putExtra("carga_escogida",real.getText().toString());
                                PCarga_dsc.putExtra("carga_escogida",edita);
                                PCarga_dsc.putExtra("conteo",conteo);
                                PCarga_dsc.putExtra("unidad",unid_uso);
                                startActivity(PCarga_dsc);
                                finish();
                            }

                            if (origen.equals("repetibilidad_ii")){
                                Intent PRepet = new Intent(SeteoCarga.this, Repetibilidad_ii.class);
                                PRepet.putExtra("idecombpr", idecombpr);
                                //PRepet.putExtra("carga_escogida",real.getText().toString());
                                PRepet.putExtra("carga_escogida",edita);
                                PRepet.putExtra("conteo",conteo);
                                PRepet.putExtra("unidad",unid_uso);
                                startActivity(PRepet);
                                finish();
                            }

                            if(origen.equals("Excentricidad_cam(1)")) {
                                Intent excec_cam = new Intent(SeteoCarga.this, Excentricidad_cam.class);
                                excec_cam.putExtra("idecombpr", idecombpr);
                                //excec_cam.putExtra("carga_escogida",real.getText().toString());
                                excec_cam.putExtra("carga_escogida",edita);
                                excec_cam.putExtra("unidad",unid_uso);
                                startActivity(excec_cam);
                                finish();
                            }

                            if(origen.equals("Excentricidad_cam(2)")) {
                                Intent excec_cam_2 = new Intent(SeteoCarga.this, Excentricidad_cam_2.class);
                                excec_cam_2.putExtra("idecombpr", idecombpr);
                                //excec_cam_2.putExtra("carga_escogida",real.getText().toString());
                                excec_cam_2.putExtra("carga_escogida",edita);
                                startActivity(excec_cam_2);
                                finish();
                            }

                            if (origen.equals("repetibilidad_iii")){

                                Intent PRepetiii = new Intent(SeteoCarga.this, Repetibilidad_iii.class);
                                PRepetiii.putExtra("idecombpr", idecombpr);
                                //PRepetiii.putExtra("carga_escogida",real.getText().toString());
                                PRepetiii.putExtra("carga_escogida",edita);
                                PRepetiii.putExtra("conteo",conteo);
                                PRepetiii.putExtra("unidad",unid_uso);
                                startActivity(PRepetiii);
                                finish();
                            }

                            if (origen.equals("Pcarga_Sust")){
                                try {
                                    Intent PCargaS = new Intent(SeteoCarga.this, Carga.class);
                                    PCargaS.putExtra("idecombpr", idecombpr);
                                    PCargaS.putExtra("carga_escogida", edita);
                                    PCargaS.putExtra("conteo", conteo);
                                    PCargaS.putExtra("unidad", unid_uso);
                                    PCargaS.putExtra("estado_sw", estado_sw);
                                    //PCargaS.putExtra("cta_susti", cuenta_sust);
                                    PCargaS.putExtra("cta_susti", sust_real);
                                    PCargaS.putExtra("ajuste", ajuste);
                                    PCargaS.putExtra("masa", edita);
                                    PCargaS.putExtra("maspeso", maspeso);
                                    PCargaS.putExtra("etapa", etapa);
                                    PCargaS.putExtra("cgr_sust", cgr_sust);
                                    PCargaS.putExtra("bandera", bandera);
                                    startActivity(PCargaS);
                                    finish();
                                }catch (Exception e ){
                                    msg("Error Pcarga_Sust",e.getMessage());
                                }
                            }
                            if (origen.equals("Pcarga_Sustitucion")){
                                Intent PCargaS = new Intent(SeteoCarga.this, Pcarga_Sustitucion.class);
                                PCargaS.putExtra("idecombpr", idecombpr);
                                PCargaS.putExtra("carga_escogida",edita);
                                PCargaS.putExtra("conteo",conteo);
                                PCargaS.putExtra("unidad",unid_uso);
                                PCargaS.putExtra("estado_sw", "Selcciono");
                                //PCargaS.putExtra("cta_susti", cuenta_sust);
                                PCargaS.putExtra("cta_susti", sust_real);
                                PCargaS.putExtra("ajuste",ajuste);
                                PCargaS.putExtra("masa",edita);
                                PCargaS.putExtra("maspeso",maspeso);
                                PCargaS.putExtra("etapa",etapa);
                                PCargaS.putExtra("cgr_sust",cgr_sust);
                                PCargaS.putExtra("bandera",bandera);
                                PCargaS.putExtra("CSustitucion",CSustitucion);
                                PCargaS.putExtra("ARequerido",ARequerido);
                                PCargaS.putExtra("Contador_Actual",contador_A);
                                PCargaS.putExtra("contadorN",contadorN);
                                startActivity(PCargaS);
                                finish();
                            }

                        }
                    }
                });
                row3.addView(bt);
            }
        }
    }
    class check{
        private int cod;
        private String nombre;
        private int pzs;
        private String cert;

        public check(int cod, String nombre, int pzs, String cert) {
            this.cod = cod;
            this.nombre = nombre;
            this.pzs = pzs;
            this.cert = cert;
        }
    }

    class text{
        private int cod;
        private String nombre;
        private int pzs;

        public text(int cod, String nombre, int pzs) {
            this.cod = cod;
            this.nombre = nombre;
            this.pzs = pzs;
        }
    }

    public String formateado(String sin_forma) {
        Globals g = (Globals) getApplication();
        final String formato = g.getFormato();

        String numero=sin_forma;
        double num_d=Double.valueOf(numero);
        String editado = String.format(formato,num_d);

        return editado;
    }

}