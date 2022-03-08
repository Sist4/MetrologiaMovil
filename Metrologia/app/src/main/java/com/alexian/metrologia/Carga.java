package com.alexian.metrologia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ing.Iván on 2017-06-05.
 */

public class Carga extends AppCompatActivity {
    private BDManager metrologia;
    private SQLiteDatabase db;
    private String idecombpr, escogida, Clase_eq, DivisionUso, carga, CargaUso,uni_recibida,est_sw,mantiene;
    private String viene_carga,viene_ajuste,viene_masa,viene_maspeso,viene_origen;
    private int maxima, uso, carga_calculo;
    private EditText etcarga, etlectura,carga_sust;
    private EditText txtCrgSust_crg,txtAjuste,txtMaspeso,txtCrg_mas_peso,txtLecturaCombinada;
    private ImageButton ibvisto,ibseteo2,ibmas,ibvisto2,ibborra;
    private ImageButton btcargaCgr, btguardaLecturaCgr;
    private Button btguardaAsc,btAplica;
    private TextView titulo, numero,uni_carga,uni_lectura,masa_pesas,uni_masa_pesas,etiqueta_sust,uni_sust,etiqueta_masa;
    private TextView lbEtiq_crg,lbAjuste,lbUnidadAjuste,lbEtiq_crg_maspeso,lbLecturaSust,lbUni_pesasAjuste,lbUnidadLec_Comb,lbUni_crg_sust,lbUni_masa_crg;
    private int contador,cod_aso;
    private int corrige=4;
    private int cod_corrige=0,seteo;
    private String origen = "PruebaCarga";
    private String alterno="Pcarga_Sust";
    private Double e, d, division_calculo, lacarga, ultima_carga;
    private Double porcentaje[] = {0.00, 0.00, 0.005, 0.01, 0.02, 0.05, 0.1, 0.2, 0.50, 1.00};
    private List<String> li = new ArrayList<String>();
    private GridView gv;
    private RelativeLayout rl;
    private Switch swSustitucion;
    private int cuenta_sustitucion,etapa_sust,realS;
    //Variable bandera para controlar que la 3ra carga no sea igual o menor a la segunda (e * 20)
    private int bandera;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba_carga_asc);
        {

            idecombpr = getIntent().getExtras().getString("idecombpr");
            escogida = getIntent().getExtras().getString("carga_escogida");
            contador = getIntent().getExtras().getInt("conteo");
            uni_recibida = getIntent().getExtras().getString("unidad");
            est_sw = getIntent().getExtras().getString("estado_sw");
            cuenta_sustitucion = getIntent().getExtras().getInt("cta_susti");
            seteo=getIntent().getExtras().getInt("seteo");

            //---
            viene_carga= getIntent().getExtras().getString("cgr_sust");
            viene_ajuste= getIntent().getExtras().getString("ajuste");
            viene_masa= getIntent().getExtras().getString("masa");
            viene_maspeso= getIntent().getExtras().getString("maspeso");
            etapa_sust=getIntent().getExtras().getInt("etapa");
            bandera=getIntent().getExtras().getInt("bandera");
            //---
            //Toast.makeText(getApplicationContext(), String.valueOf(cuenta_sustitucion) , Toast.LENGTH_SHORT).show();
            if (cuenta_sustitucion<=0){
                cuenta_sustitucion=1;
            }
            //Toast.makeText(getApplicationContext(), String.valueOf(cuenta_sustitucion) , Toast.LENGTH_SHORT).show();
            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();

            String[] args = new String[]{idecombpr};
            Cursor c = db.rawQuery(" SELECT ClaBpr,DivEscBpr,DivEsc_dBpr,DivEscCalBpr,CapCalBpr," +
                    "CapMaxBpr,CapUsoBpr FROM Balxpro where IdeComBpr=?", args);
            if (c.moveToFirst()) {
                do {
                    Clase_eq = c.getString(0);
                    e = c.getDouble(1);
                    d = c.getDouble(2);
                    DivisionUso = c.getString(3);
                    CargaUso = c.getString(4);
                    maxima = c.getInt(5);
                    uso = c.getInt(6);
                } while (c.moveToNext());
            }
            c.close();

            if (DivisionUso.equals("e")) {
                division_calculo = e;
            } else {
                division_calculo = d;
            }

            if (CargaUso.equals("uso")) {
                carga_calculo = uso;
            } else {
                carga_calculo = maxima;
            }

            etcarga = (EditText) findViewById(R.id.txtCargaCrg);
            etlectura = (EditText) findViewById(R.id.txtLecturaCrg);
            btcargaCgr = (ImageButton) findViewById(R.id.ibSelecCargaCrg);
            btguardaLecturaCgr = (ImageButton) findViewById(R.id.ibGuardaLectura);
            btguardaAsc = (Button) findViewById(R.id.btGuardarAsc);
            titulo = (TextView) findViewById(R.id.lblTituloNro);
            numero = (TextView) findViewById(R.id.lblnro);
            gv = (GridView) findViewById(R.id.gvVistaAsc);
            rl=(RelativeLayout)findViewById(R.id.lyCargAsc);
            swSustitucion=(Switch)findViewById(R.id.swCrgSust_crg_asc);
            uni_carga=(TextView)findViewById(R.id.lbuniCrg_crg_asc);
            uni_carga.setText(uni_recibida);
            uni_lectura=(TextView)findViewById(R.id.lbUniLec_crg_asc);
            uni_lectura.setText(uni_recibida);
            btAplica=(Button)findViewById(R.id.btAplicar_Crg_asc);
            //---Nuevas definiciones
            etiqueta_sust=(TextView)findViewById(R.id.lbEtiq_crg_sust_crg_asc);
            carga_sust=(EditText)findViewById(R.id.txtCrgSust_crg_asc);
            etiqueta_masa=(TextView)findViewById(R.id.lbEtiq_masa_crg_asc);
            lbAjuste=(TextView)findViewById(R.id.lbajuste);
            lbUnidadAjuste=(TextView)findViewById(R.id.lbUnidadAjuste);
            lbUnidadAjuste.setText(uni_recibida);
            lbUnidadLec_Comb=(TextView)findViewById(R.id.lblUnidadLec_comb);
            lbUnidadLec_Comb.setText(uni_recibida);
            txtAjuste=(EditText)findViewById(R.id.txtAjuste);
            txtMaspeso=(EditText)findViewById(R.id.txtMasPeso);
            txtCrg_mas_peso=(EditText)findViewById(R.id.txtCrg_mas_peso);
            txtLecturaCombinada=(EditText)findViewById(R.id.txtLecturaCombinada);
            ibmas=(ImageButton)findViewById(R.id.ibmas);
            ibvisto=(ImageButton)findViewById(R.id.ibvisto);
            ibseteo2=(ImageButton)findViewById(R.id.ibseteo2);
            lbEtiq_crg_maspeso=(TextView)findViewById(R.id.lbEtiq_crg_sust_crg_asc_maspeso);
            lbLecturaSust=(TextView)findViewById(R.id.lbLecturaSust);
            lbUni_crg_sust=(TextView)findViewById(R.id.lbUni_crg_sust_crg_asc);
            lbUni_crg_sust.setText(uni_recibida);
            lbUni_masa_crg=(TextView)findViewById(R.id.lbuni_masa_crg_asc);
            lbUni_masa_crg.setText(uni_recibida);
            lbUni_pesasAjuste=(TextView)findViewById(R.id.lbUni_pesasAjuste);
            lbUni_pesasAjuste.setText(uni_recibida);
            ibvisto2=(ImageButton)findViewById(R.id.ibvisto2);
            ibborra=(ImageButton)findViewById(R.id.ibborra);
            //-----
            if (seteo==1){
                btguardaLecturaCgr.setVisibility(View.INVISIBLE);
            }else{
                btguardaLecturaCgr.setVisibility(View.VISIBLE);
            }

            //-----
            //******
            btAplica.setVisibility(View.INVISIBLE);
            //******
            if (contador > 6) {
                btguardaAsc.setVisibility(View.VISIBLE);
                //Toast.makeText(getApplicationContext(),String.valueOf(contador), Toast.LENGTH_SHORT).show();
            }

            //---*
            if (est_sw.equals("B"))
                swSustitucion.setEnabled(false);
            //---*
            muestra_datos();

            //---Ocultamos los elementos de Carga de sustitución cuando el Switch está en estado Off
            if (est_sw.equals("I")) {
                desactivo();
                //---*
                btguardaLecturaCgr.setEnabled(false);
                etcarga.setEnabled(false);
                etlectura.setEnabled(false);

                if (contador <= 1) {
                    borra_inicio();
                    btcargaCgr.setEnabled(false);
                    btguardaLecturaCgr.setEnabled(true);
                    btguardaLecturaCgr.setVisibility(View.VISIBLE);
                    String cero=formateado("0");
                    etcarga.setText(cero);
                    etlectura.setText(cero);
                    etlectura.setEnabled(true);
                    swSustitucion.setVisibility(View.INVISIBLE);
                    etlectura.requestFocus();
                } else {
                    if (Clase_eq.equals("II")){
                        swSustitucion.setVisibility(View.INVISIBLE);
                    }else {
                        swSustitucion.setVisibility(View.VISIBLE);
                    }
                    if (contador >=  10) {
                        etcarga.setEnabled(true);
                        if (escogida == null) {
                            if (Clase_eq.equals("Camionera")){
                                if ((contador>2)&&(contador<16)) {
                                    lacarga = ((contador) - 2) * 1000.00;
                                    carga = String.valueOf(lacarga);
                                }else if(contador==2){
                                    lacarga = 500.00;
                                    carga = String.valueOf(lacarga);
                                }else if (contador == 16){
                                    lacarga=13500.00;
                                    carga = String.valueOf(lacarga);
                                }else{
                                    carga = "0";
                                    etcarga.setEnabled(true);
                                    etlectura.setEnabled(true);
                                }
                            }else {
                                carga = "0";
                                etcarga.setEnabled(true);
                            }
                        } else {
                            carga = escogida;
                            btguardaLecturaCgr.setEnabled(true);
                            etcarga.setEnabled(false);
                            etlectura.setEnabled(true);
                            etlectura.requestFocus();
                        }
                    } else {
                        if (escogida == null) {
                            etcarga.setEnabled(true);
                            if (Clase_eq.equals("Camionera")) {
                                if (contador == 2) {
                                    lacarga = 500.00;
                                } else if ((contador > 2) && (contador < 16)) {
                                    lacarga = ((contador) - 2) * 1000.00;
                                } else if (contador == 16) {
                                    lacarga = 13500.00;
                                }
                            }else {
                                if (est_sw.equals("I")) {
                                    if ((bandera == 1)&&(contador == 9)){
                                        lacarga = 0.00;
                                        etcarga.setEnabled(true);
                                    }else {
                                        if (contador == 2) {
                                            lacarga = division_calculo * 20;
                                        } else {
                                            double siguiente = carga_calculo * porcentaje[contador];
                                            double por20 = division_calculo * 20;
                                            if (siguiente <= por20) {
                                                bandera = 1;
                                            }
                                            lacarga = carga_calculo * porcentaje[contador + bandera];
                                        }
                                    }
                                }else{
                                    lacarga = 0.00;
                                    etcarga.setEnabled(true);
                                }
                            }
                            carga = String.valueOf(lacarga);
                            rl.requestFocus();
                        } else {
                            carga = escogida;
                            etlectura.setEnabled(true);
                            etlectura.requestFocus();
                            btguardaLecturaCgr.setEnabled(true);
                        }
                    }

                    String crg=formateado(carga);
                    etcarga.setText(crg);
                    btcargaCgr.setEnabled(true);

                    btcargaCgr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String es_carga=etcarga.getText().toString();
                            if (TextUtils.isEmpty(es_carga)||(Double.valueOf(es_carga) < 0)) {
                                Toast.makeText(getApplicationContext(), "Primero debe ingresar la carga propuesta.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), es_carga, Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                carga = es_carga;
                            }
                            Intent acarga = new Intent(Carga.this, SeteoCarga.class);
                            String crg_a=formateado(carga);
                            acarga.putExtra("carga", crg_a);
                            acarga.putExtra("origen", origen);
                            acarga.putExtra("idecombpr", idecombpr);
                            acarga.putExtra("unidad",uni_recibida);
                            acarga.putExtra("conteo", contador);
                            acarga.putExtra("estado_sw", est_sw);
                            acarga.putExtra("cta_susti", 0);
                            acarga.putExtra("seteo",1);
                            acarga.putExtra("bandera",bandera);
                            startActivity(acarga);
                        }
                    });
                }
            }else{
                //--Función que activa el bloque de los elementos necesarios para ingresar y registrar cargas de sustitución
                if (viene_masa.equals("")) {
                    etcarga.setEnabled(false);
                    etlectura.setEnabled(false);
                    btguardaLecturaCgr.setEnabled(false);
                    btcargaCgr.setEnabled(false);
                    activo();
                }else{
                    activo();
                    txtMaspeso.setText(viene_masa);
                    carga_sust.setText(viene_carga);
                    txtAjuste.setText(viene_ajuste);
                    etcarga.setEnabled(false);
                    etlectura.setEnabled(false);
                    btcargaCgr.setVisibility(View.INVISIBLE);
                    btguardaLecturaCgr.setVisibility(View.INVISIBLE);
                    carga_sust.setEnabled(false);
                    ibvisto2.setVisibility(View.VISIBLE);
                    ibseteo2.setVisibility(View.VISIBLE);
                    ibseteo2.setEnabled(true);
                    ibvisto2.setEnabled(true);
                    txtMaspeso.setEnabled(true);
                }
            }

            String etq="Carga de Sustitución N° " + cuenta_sustitucion;
            etiqueta_sust.setText(etq);

            String leyenda = titulo.getText().toString();
            leyenda = leyenda + " " + String.valueOf(contador);
            titulo.setText(leyenda);
            numero.setText(String.valueOf(contador));
            if (contador<=6){
            btguardaAsc.setVisibility(View.INVISIBLE);
            }

            btguardaLecturaCgr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    guarda_lectura(1);
                    muestra_datos();
                }
            });

            btguardaAsc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialogo1=new AlertDialog.Builder(Carga.this);
                    dialogo1.setTitle("Finalizar Lecturas Ascendentes");
                    dialogo1.setMessage("¿Esta seguro de finalizar la prueba de Carga? ");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent descendente = new Intent(Carga.this, Carga_dsc.class);
                            descendente.putExtra("idecombpr", idecombpr);
                            descendente.putExtra("conteo", contador-1);
                            descendente.putExtra("unidad",uni_recibida);
                            startActivity(descendente);
                            finish();
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    dialogo1.show();
                }
            });

            swSustitucion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String texto;
                    if (swSustitucion.isChecked()){
                        activo();
                    }else{
                        if (mantiene.equals("")){
                            texto=formateado("0");
                        }else {
                            texto=formateado(mantiene);
                        }
                        etcarga.setText(texto);
                        est_sw="I";
                        btcargaCgr.setEnabled(true);
                        desactivo();
                        carga_sust.setText("");
                        etcarga.setEnabled(false);
                        etlectura.setEnabled(false);
                    }
                }
            });

            btAplica.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // if (etlectura.isEnabled()) {
                        String sust = carga_sust.getText().toString();
                        if (sust.equals("")) {
                            Toast.makeText(getApplicationContext(), "Debe Ingresar un valor" +
                                    " diferente de 0(cero) ", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (Double.valueOf(carga_sust.getText().toString()) <= 0) {
                            Toast.makeText(getApplicationContext(), "El valor" +
                                    " debe ser diferente de 0(cero) " , Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            //double sumado = Double.valueOf(masa_pesas.getText().toString()) + Double.valueOf(carga_sust.getText().toString());
                            ///--
                            double sumado=0.0;
                            ///--
                            String lacarga = formateado(String.valueOf(sumado));
                            etcarga.setText(lacarga);
                            btguardaLecturaCgr.setEnabled(true);
                            etlectura.setEnabled(true);
                            swSustitucion.setEnabled(false);
                            carga_sust.setEnabled(false);
                        }
                }
            });

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etlectura.getWindowToken(), 0);
                }
            });

            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (position < 4)
                        cod_corrige=0;
                    else if ((position >= 5)&&(position <= 9))
                        cod_corrige=5;
                    else if ((position >= 10)&&(position <= 14))
                        cod_corrige=10;
                    else if ((position >= 15)&&(position <= 19))
                        cod_corrige=15;
                    else if ((position >= 20)&&(position <= 24))
                        cod_corrige=20;
                    else if ((position >= 25)&&(position <= 29))
                        cod_corrige=25;
                    else if ((position >= 30)&&(position <= 34))
                        cod_corrige=30;
                    else if ((position >= 35)&&(position <= 39))
                        cod_corrige=35;
                    else if ((position >= 40)&&(position <= 44))
                        cod_corrige=40;
                    else if ((position >= 45)&&(position <= 49))
                        cod_corrige=45;
                    else if ((position >= 50)&&(position <= 54))
                        cod_corrige=50;
                    else if ((position >= 55)&&(position <= 59))
                        cod_corrige=55;
                    else if ((position >= 60)&&(position <= 64))
                        cod_corrige=60;
                    else if ((position >= 65)&&(position <= 69))
                        cod_corrige=65;
                    else if ((position >= 70)&&(position <= 74))
                        cod_corrige=70;
                    else if ((position >= 75)&&(position <= 79))
                        cod_corrige=75;
                    else if ((position >= 80)&&(position <= 84))
                        cod_corrige=80;
                    else if ((position >= 85)&&(position <= 89))
                        cod_corrige=85;
                    else if ((position >= 90)&&(position <= 94))
                        cod_corrige=90;
                    else if ((position >= 95)&&(position <= 99))
                        cod_corrige=95;
                    else if ((position >= 100)&&(position <= 104))
                        cod_corrige=100;
                    else if ((position >= 105)&&(position <= 109))
                        cod_corrige=105;
                    else if ((position >= 110)&&(position <= 114))
                        cod_corrige=110;
                    else if ((position >= 115)&&(position <= 119))
                        cod_corrige=115;
                    else if ((position >= 120)&&(position <= 124))
                        cod_corrige=120;
                    else if ((position >= 125)&&(position <= 129))
                        cod_corrige=125;
                    else if ((position >= 130)&&(position <= 134))
                        cod_corrige=130;
                    else if ((position >= 135)&&(position <= 139))
                        cod_corrige=135;
                    else if ((position >= 140)&&(position <= 144))
                        cod_corrige=140;
                    else if ((position >= 145)&&(position <= 149))
                        cod_corrige=145;
                    if (position >= 5) {
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Carga.this);
                        dialogo1.setTitle("Corrección de lecturas");
                        dialogo1.setMessage("La corrección de lecturas permite al Metrólogo volver a tomar los datos desde " +
                                "una iteración determinada. Tenga en cuenta que toda la información subsiguiente se eliminará por " +
                                "lo que habrá que ingresarla nuevamente. " +
                                "¿Está seguro de corregir las lecturas a partir de la iteración N° " + li.get(cod_corrige) + "? ");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Corregir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cod_cabecera="";
                                String carga="";
                                String[] args = new String[]{idecombpr,li.get(cod_corrige)};
                                //Toast.makeText(getApplicationContext(), li.get(cod_corrige) , Toast.LENGTH_SHORT).show();
                                Cursor c = db.rawQuery("SELECT CodPca_C,NumPca FROM PCarga_Cab where IdeComBpr=? and NumPca>=?", args);
                                if (c.moveToFirst()) {
                                    do {
                                        //cod_cabecera=c.getString(0);
                                        cod_cabecera=idecombpr + c.getString(1);

                                        String Str="delete from PCarga_Det where CodPca_C='" + cod_cabecera + "'";
                                        db.execSQL(Str);

                                        String tip_pesxpro="C" + c.getString(1) + "+";
                                        String Str2="delete from Pesxpro where IdeComBpr='" + idecombpr  +"' and TipPxp = '" + tip_pesxpro + "'";
                                        db.execSQL(Str2);

                                        cuenta_sustitucion=cuenta_sustitucion-1;

                                    } while (c.moveToNext());
                                }
                                c.close();

                                args=new String[]{idecombpr,li.get(cod_corrige)};
                                c = db.rawQuery("SELECT CarPca FROM PCarga_Cab where IdeComBpr=? and NumPca=?", args);
                                if (c.moveToFirst()) {
                                    do {
                                        carga=c.getString(0);
                                    } while (c.moveToNext());
                                }
                                c.close();

                                String Str1="delete from PCarga_Cab where IdeComBpr = '" + idecombpr + "' and NumPca >= " + li.get(cod_corrige) + " ";
                                db.execSQL(Str1);
                                contador=Integer.valueOf(li.get(cod_corrige));
                                etcarga.setText("");
                                etlectura.setText("");
                                String cgr_sust=carga_sust.getText().toString();
                                String aju = txtAjuste.getText().toString();
                                String msa = txtMaspeso.getText().toString();
                                String crg_mas_peso=txtCrg_mas_peso.getText().toString();
                                Intent recarga = new Intent(Carga.this, Carga.class);
                                recarga.putExtra("carga_escogida", carga);
                                recarga.putExtra("idecombpr", idecombpr);
                                recarga.putExtra("conteo", Integer.valueOf(li.get(cod_corrige)));
                                recarga.putExtra("unidad", uni_recibida);
                                recarga.putExtra("estado_sw", est_sw);
                                recarga.putExtra("cta_susti", cuenta_sustitucion);
                                recarga.putExtra("cgr_sust",cgr_sust);
                                recarga.putExtra("ajuste",aju);
                                recarga.putExtra("masa",msa);
                                recarga.putExtra("maspeso",crg_mas_peso);
                                recarga.putExtra("etapa",1);
                                recarga.putExtra("seteo",1);
                                recarga.putExtra("bandera",bandera);
                                startActivity(recarga);
                            }
                        });
                        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        dialogo1.show();
                    }
                }
            });

            //Controlamos el paso al Edit de ajuste con la acción "Siguiente" de carga_sust
            carga_sust.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT){
                        String ft=formateado(carga_sust.getText().toString());
                        carga_sust.setText(ft);
                        txtAjuste.setEnabled(true);
                        ibvisto.setEnabled(true);
                        txtAjuste.requestFocus();
                    }
                    return false;
                }
            });
            //Controlamos la visibilidad del imagebutton visto con la acción del edittext
            txtAjuste.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT){
                        String ft = formateado(txtAjuste.getText().toString());
                        txtAjuste.setText(ft);
                        ibvisto.setVisibility(View.VISIBLE);
                        ibvisto.setEnabled(true);
                    }
                    return false;
                }
            });

            txtMaspeso.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_NEXT){
                        String msp=txtMaspeso.getText().toString();
                        if (msp.equals("")){
                            Toast.makeText(getApplicationContext(), "Debe especificar la carga en pesas " +
                                    "que se debe setear." + String.valueOf(contador), Toast.LENGTH_SHORT).show();
                            return false;
                        }else {
                            ibseteo2.setEnabled(true);
                            ibseteo2.setVisibility(View.VISIBLE);
                            //ibvisto2.setEnabled(true);
                            //ibvisto2.setVisibility(View.VISIBLE);
                        }
                    }
                    return false;
                }
            });

            ibvisto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //guarda_lectura(2);
                    carga_sust.setEnabled(false);
                    txtAjuste.setEnabled(false);
                    txtMaspeso.setEnabled(true);
                    txtMaspeso.requestFocus();
                    ibvisto.setVisibility(view.INVISIBLE);
                }
            });
            ibborra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   desactivo();
                    activo();
                }
            });
            ibmas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    est_sw="B";
                    guarda_lectura(2);
                }
            });
            ibvisto2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String msp=txtMaspeso.getText().toString();
                    if (msp.equals("")){
                        Toast.makeText(getApplicationContext(),"No se acepta dejar este campo vacío.", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        txtMaspeso.setEnabled(false);
                        ibseteo2.setVisibility(View.INVISIBLE);
                        Double suma = (Double.valueOf(carga_sust.getText().toString()) + Double.valueOf(txtMaspeso.getText().toString()));
                        String ft = formateado(String.valueOf(suma));
                        txtCrg_mas_peso.setText(ft);
                        ibmas.setEnabled(true);
                        ibmas.setVisibility(View.VISIBLE);
                        ibborra.setEnabled(true);
                        ibborra.setVisibility(View.VISIBLE);
                        txtLecturaCombinada.setEnabled(true);
                        ibvisto2.setVisibility(View.INVISIBLE);
                    }
                }
            });

            ibseteo2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String msp=txtMaspeso.getText().toString();
                    if (msp.equals("")){
                        Toast.makeText(getApplicationContext(), "Debe especificar la carga en pesas " +
                                "que se debe setear.", Toast.LENGTH_SHORT).show();
                        return;
                    }else {

                        double crgenv=Double.valueOf(carga_sust.getText().toString());
                        double ajuenv=Double.valueOf(txtAjuste.getText().toString());
                        double realenv=crgenv-ajuenv+(division_calculo/2);
                        String cgr_sust = carga_sust.getText().toString();
                        String aju = txtAjuste.getText().toString();
                        String msa = txtMaspeso.getText().toString();
                        String crg_mas_peso = txtCrg_mas_peso.getText().toString();
                        Intent acarga = new Intent(Carga.this, SeteoCarga.class);
                        String crg_a = formateado(txtMaspeso.getText().toString());
                        acarga.putExtra("carga", crg_a);
                        acarga.putExtra("origen", alterno);
                        acarga.putExtra("idecombpr", idecombpr);
                        acarga.putExtra("unidad", uni_recibida);
                        acarga.putExtra("conteo", contador);
                        acarga.putExtra("estado_sw", est_sw);
                        acarga.putExtra("cta_susti", cuenta_sustitucion);
                        acarga.putExtra("cgr_sust", cgr_sust);
                        acarga.putExtra("ajuste", aju);
                        acarga.putExtra("masa", msa);
                        acarga.putExtra("maspeso", crg_mas_peso);
                        acarga.putExtra("etapa", 2);
                        acarga.putExtra("real",realenv);
                        acarga.putExtra("seteo",1);
                        acarga.putExtra("bandera",bandera);
                        startActivity(acarga);
                    }
                }
            });
            return;
        }
    }

    public void borra_inicio(){
        String[] args = new String[]{idecombpr};
        String borra="Delete from PCarga_Det where CodPca_C like '" + idecombpr + '%' + "'";
        db.execSQL(borra);
        borra="Delete from PCarga_Cab where IdeComBpr = '" + idecombpr + "'";
        db.execSQL(borra);
    }

    public void guarda_lectura(int vienede) {
        int codcab = 0;
        double carga=0;
        double Ajuste=0;
        double lectura = 0;
        double diferencia = 0;

        if (vienede==1){
            carga = Double.valueOf(etcarga.getText().toString());
            Ajuste=0;
            String SCarga = etcarga.getText().toString();
            String SLectura = etlectura.getText().toString();
            if ((SLectura.equals("")) || (SCarga.equals(""))) {
                Toast.makeText(getApplicationContext(), "Debe Ingresar la " +
                        "lectura Ascendente N° " + String.valueOf(contador), Toast.LENGTH_SHORT).show();
                return;
            } else {
                lectura = Double.valueOf(etlectura.getText().toString());
                diferencia = Math.abs(carga - lectura);
            }

        }else if (vienede==2){
            //carga = Double.valueOf(txtLecturaCombinada.getText().toString());
            carga = Double.valueOf(txtCrg_mas_peso.getText().toString());
            Ajuste=Double.valueOf(txtAjuste.getText().toString());
            String SCarga = txtCrg_mas_peso.getText().toString();
            String SLectura = txtLecturaCombinada.getText().toString();
            if ((SLectura.equals("")) || (SCarga.equals(""))) {
                Toast.makeText(getApplicationContext(), "Debe Ingresar la " +
                        "lectura Ascendente (con Carga de Sustitución) N° " + String.valueOf(contador), Toast.LENGTH_SHORT).show();
                return;
            } else {
                lectura = Double.valueOf(txtLecturaCombinada.getText().toString());
                diferencia = Math.abs(carga - lectura);
            }

        }

        if ((contador > 1) && (carga<=0)){
            Toast.makeText(getApplicationContext(), "Para esta iteración, " +
                    "la carga no puede ser igual a 0(cero)" + String.valueOf(contador), Toast.LENGTH_SHORT).show();
            return;
        }

        String Str = "Insert into PCarga_Cab values (null," + carga + "," +
                "" + contador + ",'" + idecombpr + "','SATISFACTORIA')";
        db.execSQL(Str);
        //Código reemplazado 09-11-2017 por Ivan Villavicencio. Se cambia el dato que se envía a la columna del código de cabecera.
        //en lugar de ello se envía el IdeComBpr + el número de prueba.
        String Scodcab=idecombpr + contador;

        String Str1 = "Insert into PCarga_Det (CodPca_D,LecAscPca,ErrAscPca,CodPca_C,SatPca_D) values (null," + lectura + "," +
                "" + diferencia + ",'" + Scodcab + "','SATISFACTORIA')";
        db.execSQL(Str1);

        if (vienede==1) {
            contador++;
            etcarga.setText("");
            etlectura.setText("");
            String cgr_sust=carga_sust.getText().toString();
            String aju = txtAjuste.getText().toString();
            String msa = txtMaspeso.getText().toString();
            String crg_mas_peso=txtCrg_mas_peso.getText().toString();
            Intent recarga = new Intent(Carga.this, Carga.class);
            recarga.putExtra("carga_escogida", carga);
            recarga.putExtra("idecombpr", idecombpr);
            recarga.putExtra("conteo", contador++);
            recarga.putExtra("unidad", uni_recibida);
            recarga.putExtra("estado_sw", est_sw);
            recarga.putExtra("cta_susti", cuenta_sustitucion);
            recarga.putExtra("cgr_sust",cgr_sust);
            recarga.putExtra("ajuste",aju);
            recarga.putExtra("masa",msa);
            recarga.putExtra("maspeso",crg_mas_peso);
            recarga.putExtra("etapa",1);
            recarga.putExtra("seteo",1);
            recarga.putExtra("bandera",bandera);
            startActivity(recarga);
            finish();
        }else{
            cuenta_sustitucion++;
            contador++;
            etcarga.setText("");
            etlectura.setText("");
            String cgr_sust=carga_sust.getText().toString();
            String aju = txtAjuste.getText().toString();
            String msa = txtMaspeso.getText().toString();
            String crg_mas_peso=txtCrg_mas_peso.getText().toString();
            Intent recarga = new Intent(Carga.this, Carga.class);
            recarga.putExtra("carga_escogida", carga);
            recarga.putExtra("idecombpr", idecombpr);
            recarga.putExtra("conteo", contador++);
            recarga.putExtra("unidad", uni_recibida);
            recarga.putExtra("estado_sw", "B");
            recarga.putExtra("cta_susti", cuenta_sustitucion);
            recarga.putExtra("cgr_sust",cgr_sust);
            recarga.putExtra("ajuste",aju);
            recarga.putExtra("masa","");
            recarga.putExtra("maspeso",crg_mas_peso);
            recarga.putExtra("etapa",1);
            recarga.putExtra("seteo",1);
            recarga.putExtra("bandera",bandera);
            startActivity(recarga);
            finish();
        }
    }

    public void muestra_datos() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, li);
        String Carga_tomada = "", num_toma = "", lectura_tomada = "", error_tomado = "";

        li.add("N°");
        li.add("Carga");
        li.add("Lectura Asc");
        li.add("Err Asc");
        li.add("Opción");

       try {
            String[] args = new String[]{idecombpr};
            Cursor c = db.rawQuery("select cab.CarPca as Carga,cab.NumPca as numero,LecAscPca as LecturaAsc,ErrAscPca as Error_Asc" +
                    " from Pcarga_cab as cab,Pcarga_det as det" +
                    " where cab.idecombpr=? and cab.CodPca_C = det.CodPca_D", args);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        Carga_tomada = String.valueOf(c.getDouble(0));
                        num_toma = String.valueOf(c.getInt(1));
                        lectura_tomada = String.valueOf(c.getDouble(2));
                        error_tomado = String.valueOf(c.getDouble(3));

                        li.add(String.valueOf(num_toma));
                        String crgtmd=formateado(Carga_tomada);
                        li.add(String.valueOf(crgtmd));
                        String lectmd=formateado(lectura_tomada);
                        li.add(String.valueOf(lectmd));
                        String errtmd=formateado(error_tomado);
                        li.add(String.valueOf(errtmd));
                        li.add("Corregir");
                    } while (c.moveToNext());
                }
            }
            c.close();

        } catch (Exception e) {
           Toast.makeText(getApplicationContext(),"Existió algún problema.", Toast.LENGTH_SHORT).show();
        }
        gv.setAdapter(dataAdapter);
    }

    public String formateado(String sin_forma) {
        Globals g = (Globals) getApplication();
        final String formato = g.getFormato();

        String numero=sin_forma;
        double num_d=Double.valueOf(numero);
        String editado = String.format(formato,num_d);

        return editado;
    }

    private void activo(){
        mantiene=etcarga.getText().toString();
        est_sw="A";
        swSustitucion.setChecked(true);

        etiqueta_sust.setVisibility(View.VISIBLE);
        //String etq="Carga de Sustitución N° " + cuenta_sustitucion;
        //etiqueta_sust.setText(etq);
        carga_sust.setVisibility(View.VISIBLE);
        carga_sust.setEnabled(true);
        lbUni_crg_sust.setVisibility(View.VISIBLE);
        lbAjuste.setVisibility(View.VISIBLE);
        txtAjuste.setVisibility(View.VISIBLE);
        txtAjuste.setEnabled(false);
        lbUni_pesasAjuste.setVisibility(View.VISIBLE);
        ibvisto.setVisibility(View.INVISIBLE);
        ibvisto.setEnabled(false);
        etiqueta_masa.setVisibility(View.VISIBLE);
        txtMaspeso.setVisibility(View.VISIBLE);
        txtMaspeso.setEnabled(false);
        lbUni_masa_crg.setVisibility(View.VISIBLE);
        ibseteo2.setVisibility(View.INVISIBLE);
        ibseteo2.setEnabled(false);
        lbEtiq_crg_maspeso.setVisibility(View.VISIBLE);
        txtCrg_mas_peso.setVisibility(View.VISIBLE);
        txtCrg_mas_peso.setEnabled(false);
        lbUnidadAjuste.setVisibility(View.VISIBLE);
        lbLecturaSust.setVisibility(View.VISIBLE);
        txtLecturaCombinada.setVisibility(View.VISIBLE);
        txtLecturaCombinada.setEnabled(false);
        lbUnidadLec_Comb.setVisibility(View.VISIBLE);
        ibmas.setVisibility(View.INVISIBLE);
        ibmas.setEnabled(false);
        ibborra.setVisibility(View.INVISIBLE);
        ibvisto2.setVisibility(View.INVISIBLE);
        if (contador>=7){
            btguardaAsc.setVisibility(View.VISIBLE);
        }
    }

    private void desactivo(){
        etiqueta_masa.setVisibility(View.INVISIBLE);
        etiqueta_sust.setVisibility(View.INVISIBLE);
        carga_sust.setVisibility(View.INVISIBLE);
        //---Nuevas definiciones
        lbAjuste.setVisibility(View.INVISIBLE);
        lbUnidadAjuste.setVisibility(View.INVISIBLE);
        lbUnidadLec_Comb.setVisibility(View.INVISIBLE);
        txtAjuste.setVisibility(View.INVISIBLE);
        txtMaspeso.setVisibility(View.INVISIBLE);
        txtCrg_mas_peso.setVisibility(View.INVISIBLE);
        txtLecturaCombinada.setVisibility(View.INVISIBLE);
        ibmas.setVisibility(View.INVISIBLE);
        ibvisto.setVisibility(View.INVISIBLE);
        ibseteo2.setVisibility(View.INVISIBLE);
        ibvisto2.setVisibility(View.INVISIBLE);
        ibborra.setVisibility(View.INVISIBLE);
        lbEtiq_crg_maspeso.setVisibility(View.INVISIBLE);
        lbLecturaSust.setVisibility(View.INVISIBLE);
        lbUni_masa_crg.setVisibility(View.INVISIBLE);
        lbUni_crg_sust.setVisibility(View.INVISIBLE);
        lbUni_pesasAjuste.setVisibility(View.INVISIBLE);
        carga_sust.setText("");
        txtAjuste.setText("");
        txtMaspeso.setText("");
        txtCrg_mas_peso.setText("");
        txtLecturaCombinada.setText("");
    }
    @Override
    public void onBackPressed() {
        int lugar=Integer.valueOf(numero.getText().toString());
        etcarga.setText("");
        etlectura.setText("");
        String cgr_sust=carga_sust.getText().toString();
        String aju = txtAjuste.getText().toString();
        String msa = txtMaspeso.getText().toString();
        String crg_mas_peso=txtCrg_mas_peso.getText().toString();
        Intent recarga = new Intent(Carga.this, Carga.class);
        recarga.putExtra("carga_escogida", carga );
        recarga.putExtra("idecombpr", idecombpr);
        recarga.putExtra("conteo", lugar );
        recarga.putExtra("unidad", uni_recibida);
        recarga.putExtra("estado_sw", est_sw);
        recarga.putExtra("cta_susti", cuenta_sustitucion);
        recarga.putExtra("cgr_sust",cgr_sust);
        recarga.putExtra("ajuste",aju);
        recarga.putExtra("masa",msa);
        recarga.putExtra("maspeso",crg_mas_peso);
        recarga.putExtra("etapa",1);
        recarga.putExtra("seteo",1);
        recarga.putExtra("bandera",bandera);
        startActivity(recarga);
        finish();

    }
}