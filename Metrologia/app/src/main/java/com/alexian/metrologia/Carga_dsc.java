package com.alexian.metrologia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ing.Iván on 2017-06-09.
 */

public class Carga_dsc extends AppCompatActivity {
    private BDManager metrologia;
    private SQLiteDatabase db;
    private String idecombpr, carga, numero_lec,escogida,uni_recibida;
    private int codigo;
    private EditText etcarga, etlectura;
    private ImageButton btcargaCgr, btguardaLecturaCgr, btadicionalectura;
    private Button btguardaDsc;
    private TextView titulo, numero, adicion,formula,satisface,empcrg,uni_carga,uni_lectura;
    private int contador;
    private String origen = "PruebaCarga_dsc";
    private Double e, d, division_calculo, lacarga;
    private GridView gv;
    private RelativeLayout rl;
    private int cod_corrige=0;
    private List<String> li = new ArrayList<String>();

    //List<String> item=null;

    private String  Contador_actual()
    {

        String s_contador ="0";
        String[] args = new String[]{idecombpr};
        Cursor c = db.rawQuery(" select max(NumPca) from PCarga_Cab where idecombpr=? ", args);
        if (c.moveToFirst()) {
            do {
                s_contador=c.getString(0) ;

            } while (c.moveToNext());
        }
        c.close();

        return s_contador;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba_carga_dsc);
        {
            {
                //**************************************Cuador de dialogo**********************************************

                //*****************************************************************************************************
                idecombpr = getIntent().getExtras().getString("idecombpr");
                escogida = getIntent().getExtras().getString("carga_escogida");
                contador=getIntent().getExtras().getInt("conteo");
                uni_recibida=getIntent().getExtras().getString("unidad");



                metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
                db = metrologia.getWritableDatabase();

                etcarga = (EditText) findViewById(R.id.txtCargaCrg);
                etlectura = (EditText) findViewById(R.id.txtLecturaCrg);
                btcargaCgr = (ImageButton) findViewById(R.id.ibSelecCargaCrg);
                btguardaLecturaCgr = (ImageButton) findViewById(R.id.ibGuardaLectura);
                btguardaDsc = (Button) findViewById(R.id.btGuardarAsc);
                btadicionalectura = (ImageButton) findViewById(R.id.ibAdicionarLectura);
                titulo = (TextView) findViewById(R.id.lblTituloNro);
                numero = (TextView) findViewById(R.id.lblnro);
                adicion = (TextView) findViewById(R.id.lblAdicionar);
                gv = (GridView) findViewById(R.id.gvVistaAsc);
                formula=(TextView)findViewById(R.id.lblFormula_Pcarga);
                empcrg=(TextView)findViewById(R.id.lblEmpCrg);
                satisface=(TextView)findViewById(R.id.lblSatisfactorio);
                rl=(RelativeLayout)findViewById(R.id.LyCargaDsc);
                uni_carga=(TextView)findViewById(R.id.lbUniCarga_crg_dsc);
                uni_lectura=(TextView)findViewById(R.id.lbUniLec_crg_dsc);

                uni_carga.setText(uni_recibida);
                uni_lectura.setText(uni_recibida);

                btcargaCgr.setVisibility(View.INVISIBLE);
                etcarga.setEnabled(false);

                String leyenda = titulo.getText().toString();
                leyenda = leyenda + " " + contador;
                titulo.setText(leyenda);
                numero.setText(String.valueOf(contador));
                btguardaDsc.setVisibility(View.INVISIBLE);
                btadicionalectura.setVisibility(View.INVISIBLE);
                adicion.setVisibility(View.INVISIBLE);
                btguardaLecturaCgr.setEnabled(false);

                muestra_datos();
                String[] args = new String[]{idecombpr,String.valueOf(contador)};
                Cursor c = db.rawQuery(" select CodPca_C,CarPca from PCarga_Cab where idecombpr=? and NumPca=?", args);
                if (c.moveToFirst()) {
                    do {
                        codigo=c.getInt(0);
                        lacarga=c.getDouble(1);
                    } while (c.moveToNext());
                }
                c.close();

                if (contador>1) {

                    if (escogida == null) {
                        String lacrg=formateado(String.valueOf(lacarga));
                        etcarga.setText(lacrg);
                        btguardaLecturaCgr.setEnabled(true);
                    }

                    setea_formula();

                    etlectura.requestFocus();

                }else if (contador == 1){

                    etcarga.setText("0");
                    etlectura.setText("0");
                    btcargaCgr.setEnabled(false);
                    btguardaLecturaCgr.setEnabled(true);
                    etlectura.requestFocus();

                }else{
                    btguardaDsc.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Se ha terminado de ingresar los valores de cargas descendientes", Toast.LENGTH_SHORT).show();

                }

                etlectura.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //String edita=formateado(etp2.getText().toString());
                        //etp2.setText(edita);
                        //maximo();
                        //emp_excentricidad();
                        //resulta();
                        //errmp();
                    }
                });
                etlectura.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE){
                            btguardaLecturaCgr.requestFocus();
                        }
                        return false;
                    }
                });


                btcargaCgr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String es_carga=etcarga.getText().toString();
                        if (TextUtils.isEmpty(es_carga)){
                            Toast.makeText(getApplicationContext(),"Primero debe ingresar la carga propuesta.", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            carga=es_carga;
                        }
                        Intent acarga = new Intent(Carga_dsc.this, SeteoCarga.class);
                        acarga.putExtra("carga", carga);
                        acarga.putExtra("origen", origen);
                        acarga.putExtra("idecombpr", idecombpr);
                        acarga.putExtra("conteo", contador);
                        startActivity(acarga);
                    }
                });

                btguardaLecturaCgr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        guarda_lectura();

                        muestra_datos();
                    }
                });

                btguardaDsc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String resu_final="";
                        int verifica=0;
                        Cursor c = db.rawQuery("select SatPca_D from Pcarga_Det where CodPca_C=" + codigo + "", null);
                        if (c.moveToFirst()) {
                            do {

                                String res=c.getString(0);
                                if (res.equals("NO SATISFACTORIA")){
                                    verifica++;
                                }

                            } while (c.moveToNext());
                        }
                        c.close();

                        if (verifica>=1)
                            resu_final="SATISFACTORIA";
                        else
                            resu_final="NO SATISFACTORIA";

                        String act_final="update PCarga_Cab set SatPca_C='" + resu_final  +"' where CodPca_C=" + codigo + "";
                        db.execSQL(act_final);

                        AlertDialog.Builder dialogo1=new AlertDialog.Builder(Carga_dsc.this);
                        dialogo1.setTitle("Finalizar Lecturas Descendentes");
                        dialogo1.setMessage("¿Esta seguro de finalizar la prueba de Carga? ");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Finalizar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] args = new String[]{idecombpr};
                                String clase="";
                                Cursor c = db.rawQuery("Select ClaBpr from Balxpro where idecombpr=?", args);
                                if (c.moveToFirst()) {
                                    do {
                                        clase=c.getString(0);
                                    } while (c.moveToNext());
                                }
                                if (clase.equals("II")) {
                                    Intent repetibilidad = new Intent(Carga_dsc.this, Repetibilidad_ii.class);
                                    repetibilidad.putExtra("idecombpr", idecombpr);
                                    repetibilidad.putExtra("carga_escogida", "");
                                    repetibilidad.putExtra("unidad",uni_recibida);
                                    startActivity(repetibilidad);
                                    finish();
                                }else {

                                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Carga_dsc.this);
                                    dialogo1.setTitle("Cargas de Sustitucion");
                                    dialogo1.setMessage("¿ Desea Realizar Cargas de Sustitucion?");
                                    dialogo1.setCancelable(false);
                                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
//*****************************************Nuevas cargas de sustitucion
                                            Intent repetibilidad = new Intent(Carga_dsc.this, Pcarga_Sustitucion.class);
                                            repetibilidad.putExtra("idecombpr", idecombpr);
                                            repetibilidad.putExtra("carga_escogida", "");
                                            repetibilidad.putExtra("unidad",uni_recibida);
                                            repetibilidad.putExtra("contadorN",1);
                                            repetibilidad.putExtra("estado_sw", "INICIO");

                                            //datos nuevos
                                            repetibilidad.putExtra("Contador_Actual",Contador_actual());
                                            //fin
                                            startActivity(repetibilidad);
                                            finish();
                                        }
                                    });
                                    dialogo1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
                                            Intent repetibilidad = new Intent(Carga_dsc.this, Repetibilidad_iii.class);
                                            repetibilidad.putExtra("idecombpr", idecombpr);
                                            repetibilidad.putExtra("carga_escogida", "");
                                            repetibilidad.putExtra("unidad",uni_recibida);
                                            startActivity(repetibilidad);
                                            finish();
                                        }
                                    });
                                    dialogo1.show();

                                }
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
                        //Toast.makeText(getApplicationContext(), String.valueOf(position) , Toast.LENGTH_SHORT).show();
                        if (position < 8)
                            cod_corrige=0;
                        else if ((position >= 9)&&(position <= 17))
                            cod_corrige=9;
                        else if ((position >= 18)&&(position <= 26))
                            cod_corrige=18;
                        else if ((position >= 27)&&(position <= 35))
                            cod_corrige=27;
                        else if ((position >= 36)&&(position <= 44))
                            cod_corrige=36;
                        else if ((position >= 45)&&(position <= 53))
                            cod_corrige=45;
                        else if ((position >= 54)&&(position <= 62))
                            cod_corrige=54;
                        else if ((position >= 63)&&(position <= 71))
                            cod_corrige=63;
                        else if ((position >= 72)&&(position <= 80))
                            cod_corrige=72;
                        else if ((position >= 81)&&(position <= 89))
                            cod_corrige=81;
                        else if ((position >= 90)&&(position <= 98))
                            cod_corrige=90;
                        else if ((position >= 99)&&(position <= 107))
                            cod_corrige=99;
                        else if ((position >= 108)&&(position <= 116))
                            cod_corrige=108;
                        else if ((position >= 117)&&(position <= 125))
                            cod_corrige=117;
                        else if ((position >= 126)&&(position <= 134))
                            cod_corrige=126;
                        else if ((position >= 135)&&(position <= 143))
                            cod_corrige=135;
                        else if ((position >= 144)&&(position <= 152))
                            cod_corrige=144;
                        else if ((position >= 153)&&(position <= 161))
                            cod_corrige=153;
                        else if ((position >= 162)&&(position <= 170))
                            cod_corrige=162;
                        else if ((position >= 171)&&(position <= 179))
                            cod_corrige=171;
                        else if ((position >= 180)&&(position <= 188))
                            cod_corrige=180;
                        else if ((position >= 189)&&(position <= 197))
                            cod_corrige=189;
                        else if ((position >= 198)&&(position <= 206))
                            cod_corrige=198;
                        else if ((position >= 207)&&(position <= 215))
                            cod_corrige=207;
                        else if ((position >= 216)&&(position <= 224))
                            cod_corrige=216;
                        else if ((position >= 225)&&(position <= 233))
                            cod_corrige=225;
                        else if ((position >= 234)&&(position <= 242))
                            cod_corrige=234;
                        else if ((position >= 243)&&(position <= 251))
                            cod_corrige=243;
                        else if ((position >= 252)&&(position <= 260))
                            cod_corrige=252;
                        else if ((position >= 261)&&(position <= 269))
                            cod_corrige=261;
                        if (position >= 9) {
                            //Toast.makeText(getApplicationContext(), li.get(cod_corrige) , Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Carga_dsc.this);
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

                                    Cursor c = db.rawQuery("SELECT CodPca_C,NumPca FROM PCarga_Cab where IdeComBpr=? and NumPca<=?", args);
                                    if (c.moveToFirst()) {
                                        do {
                                            //cod_cabecera=c.getString(0);
                                            cod_cabecera=idecombpr + c.getString(1);
                                            String Str="update Pcarga_det set LecDscPca=null,ErrDscPca=null,EmpPca=null where codpca_c ='" + cod_cabecera + "'";
                                            db.execSQL(Str);
                                        } while (c.moveToNext());
                                    }
                                    c.close();
                                    muestra_datos();

                                    etcarga.setText("");
                                    etlectura.setText("");
                                    Intent recarga = new Intent(Carga_dsc.this, Carga_dsc.class);
                                    recarga.putExtra("carga", carga);
                                    recarga.putExtra("origen", origen);
                                    recarga.putExtra("idecombpr", idecombpr);
                                    recarga.putExtra("conteo", Integer.valueOf(li.get(cod_corrige)));
                                    recarga.putExtra("unidad",uni_recibida);
                                    startActivity(recarga);
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
                    }
                });

            }
        }
    }

    public void errmp(){
        double carga = Double.valueOf(etcarga.getText().toString());
        String clase="";
        double uno_e=0;
        double dos_e=0;
        double emp=0;
        double errasc=0;
        double errdsc=0;
        String resulta="";

        String[] args = new String[]{idecombpr};
        Cursor c = db.rawQuery(" SELECT DivEscBpr,ClaBpr FROM Balxpro where idecombpr=?", args);
        if (c.moveToFirst()) {
            do {
                e=c.getDouble(0);
                clase=c.getString(1);
                //capmax = c.getInt(0);
            } while (c.moveToNext());
        }
        c.close();

        if (clase.equals("II")){
            uno_e=(5000*e);
            dos_e=(20000*e);
        }else if (clase.equals("III")){
            uno_e=(500*e);
            dos_e=(2000*e);
        }else if (clase.equals("IIII")){
            uno_e=(50*e);
            dos_e=(200*e);
        }else if (clase.equals("Camionera")) {
            uno_e = (500 * e);
            dos_e = (2000 * e);
        }

        if (Double.valueOf(carga) <= uno_e){
            emp=(e*1);
        }else if (Double.valueOf(carga) <= dos_e){
            emp=(e*2);
        }else{
            emp=(e*3);
        }

        String edita = "e.m.p.: " + formateado(String.valueOf(emp));
        empcrg.setText(edita);

        c = db.rawQuery("select ErrAscPca,ErrDscPca from Pcarga_Det where CodPca_C='" + idecombpr+contador + "'", null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {

                    errasc=c.getDouble(0);
                    errdsc=c.getDouble(1);

                } while (c.moveToNext());
            }
        }
        c.close();

        if ((Math.abs(errasc)<=(emp*1.001)) && (Math.abs(errdsc)<=(emp*1.001))){
            resulta="SATISFACTORIA";
        }else{
            resulta="NO SATISFACTORIA";
        }

        String Str1 = "Update PCarga_Det set EmpPca=" + emp + ",SatPca_D='" + resulta + "' where CodPca_C='" + idecombpr+contador + "'";
        db.execSQL(Str1);

        String Str2 ="Update Pcarga_Cab set SatPca_C='"+ resulta + "' where codPca_C =" + codigo + "";
        db.execSQL(Str2);

    }



    public void guarda_lectura() {
        double carga = Double.valueOf(etcarga.getText().toString());
        double lectura = 0;
        double diferencia = 0;

        String SCarga = etcarga.getText().toString();
        String SLectura = etlectura.getText().toString();
        if ((SLectura.equals("")) || (SCarga.equals(""))) {
            Toast.makeText(getApplicationContext(), "Debe Ingresar la " +
                    "lectura Descendente N° " + String.valueOf(contador), Toast.LENGTH_SHORT).show();
            return;
        } else {
            lectura = Double.valueOf(etlectura.getText().toString());
            diferencia = Math.abs(carga - lectura);
        }

        String Str1 = "Update PCarga_Det set LecDscPca=" + lectura + ", " +
                "ErrDscPca=" + diferencia + " where CodPca_C='" + idecombpr+contador + "'";
        //"ErrDscPca=" + diferencia + " where CodPca_C=" + codigo + "";
        //"ErrDscPca=" + diferencia + " where CodPca_C=" + codigo + "";
        db.execSQL(Str1);

        errmp();

        contador--;
        etcarga.setText("");
        etlectura.setText("");
        Intent recarga = new Intent(Carga_dsc.this, Carga_dsc.class);
        recarga.putExtra("carga", carga);
        recarga.putExtra("origen", origen);
        recarga.putExtra("idecombpr", idecombpr);
        recarga.putExtra("conteo", contador);
        recarga.putExtra("unidad",uni_recibida);
        startActivity(recarga);
        finish();
    }

    public void muestra_datos() {
        //List<String> li = new ArrayList<String>();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, li);
        String Carga_tomada = "", num_toma = "", lectura_tomada = "", error_tomado = "";
        String desc_tomada = "",errorDsc = "",emp = "",satis="";

        li.add("N°");
        li.add("Crg");
        li.add("L+");
        li.add("E+");
        li.add("L-");
        li.add("E-");
        li.add("emp");
        li.add("Sat");
        li.add("Opc");


        try {
            String[] args = new String[]{idecombpr};
            Cursor c = db.rawQuery("select cab.CarPca as Carga,cab.NumPca as numero, " +
                    " det.LecAscPca as LecturaAsc,det.ErrAscPca as Error_Asc, " +
                    " det.LecDscPca as LecturaDsc,det.ErrDscPca as Error_Dsc, " +
                    " det.EmpPca as emp, " +
                    " det.SatPca_D as satisface " +
                    " from Pcarga_cab as cab,Pcarga_det as det" +
                    " where cab.idecombpr=? and cab.CodPca_C = det.CodPca_D", args);
            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        Carga_tomada = String.valueOf(c.getDouble(0));
                        num_toma = String.valueOf(c.getInt(1));
                        lectura_tomada = String.valueOf(c.getDouble(2));
                        error_tomado = String.valueOf(c.getDouble(3));
                        desc_tomada = String.valueOf(c.getDouble(4));
                        errorDsc = String.valueOf(c.getDouble(5));
                        emp = String.valueOf(c.getDouble(6));
                        satis=c.getString(7);



                        li.add(String.valueOf(num_toma));
                        String crgtmd=formateado(Carga_tomada);
                        li.add(String.valueOf(crgtmd));
                        String lectmd=formateado(lectura_tomada);
                        li.add(String.valueOf(lectmd));
                        String errtmd=formateado(error_tomado);
                        li.add(String.valueOf(errtmd));
                        String desctmd=formateado(desc_tomada);
                        li.add(String.valueOf(desctmd));
                        String errdsctmd=formateado(errorDsc);
                        li.add(String.valueOf(errdsctmd));
                        String emptmd = formateado(emp);
                        li.add(String.valueOf(emptmd));
                        String iniciales;
                        if (satis.equals("SATISFACTORIA"))
                            iniciales="S";
                        else
                            iniciales="NS";
                        li.add(iniciales);
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

    private void setea_formula(){
        int n1,n2,n2a,n5,n10,n20,n20a,n50,n100,n200,n200a,n500,n1000,n2000,n2000a,n5000,n10000,n20000,n500000,n1000000;
        double Crp1,Crp2,Crp3,Crp4,Crp5,Crp6,Crp7,Crp8,Crp9,Crp10,Crp11,Crp12,AjCrp;
        String nombre,forma_formula;
        forma_formula="Pesas: ";
        String tipo_armado="C" + String.valueOf(contador) + "+";
        String[] args = new String[]{idecombpr,tipo_armado};
        Cursor c = db.rawQuery(" SELECT * FROM Pesxpro where idecombpr=? and TipPxp=?",args);
        if (c.moveToFirst()) {
            do {

                nombre=c.getString(3);
                n1=c.getInt(4);
                n2=c.getInt(5);
                n2a=c.getInt(6);
                n5=c.getInt(7);
                n10=c.getInt(8);
                n20=c.getInt(9);
                n20a=c.getInt(10);
                n50=c.getInt(11);
                n100=c.getInt(12);
                n200=c.getInt(13);
                n200a=c.getInt(14);
                n500=c.getInt(15);
                n1000=c.getInt(16);
                n2000=c.getInt(17);
                n2000a=c.getInt(18);
                n5000=c.getInt(19);
                n10000=c.getInt(20);
                n20000=c.getInt(21);
                n500000=c.getInt(22);
                n1000000=c.getInt(23);
                Crp1=c.getDouble(24);
                Crp2=c.getDouble(25);
                Crp3=c.getDouble(26);
                Crp4=c.getDouble(27);
                Crp5=c.getDouble(28);
                Crp6=c.getDouble(29);
                Crp7=c.getDouble(30);
                Crp8=c.getDouble(31);
                Crp9=c.getDouble(32);
                Crp10=c.getDouble(33);
                Crp11=c.getDouble(34);
                Crp12=c.getDouble(35);
                AjCrp=c.getDouble(36);
                String palabra="";


                if (n1 > 0){
                    if (n1 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula=forma_formula + String.valueOf(n1) + " " + palabra + " de 1g. ";
                }
                if (n2 > 0){
                    if (n2 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n2) + " " + palabra + " de 2g. ";
                }
                if (n2a > 0){
                    if (n2a > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n2a) + " " + palabra + " de 2g(*). ";
                }
                if (n5 > 0){
                    if (n5 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " +  String.valueOf(n5) + " " + palabra + " de 5g. ";
                }
                if (n10 > 0){
                    if (n10 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n10) + " " + palabra + " de 10g. ";
                }
                if (n20 > 0){
                    if (n20 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n20) + " " + palabra + " de 20g. ";
                }
                if (n20a > 0){
                    if (n20a > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n20a) + " " + palabra + " de 20g(*). ";
                }
                if (n50 > 0){
                    if (n50 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n50) + " " + palabra + " de 50g. ";
                }
                if (n100 > 0){
                    if (n100 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n100) + " " + palabra + " de 100g. ";
                }
                if (n200 > 0){
                    if (n200 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n200) + " " + palabra + " de 200g. ";
                }
                if (n200a > 0){
                    if (n200a > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n200a) + " " + palabra + " de 200g(*). ";
                }
                if (n500 > 0){
                    if (n500 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n500) + " " + palabra + " de 500g. ";
                }
                if (n1000 > 0){
                    if (n1000 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n1000) + " " + palabra + " de 1000g. ";
                }
                if (n2000 > 0){
                    if (n2000 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n2000) + " " + palabra + " de 2000g. ";
                }
                if (n2000a > 0){
                    if (n2000a > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n2000a) + " " + palabra + " de 2000g(*). ";
                }
                if (n5000 > 0){
                    if (n5000 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n5000) + " " + palabra + " de 5000g. ";
                }
                if (n10000 > 0){
                    if (n10000 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n10000) + " " + palabra + " de 10000g. ";
                }
                if (n20000 > 0){
                    if (n20000 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n20000) + " " + palabra + " de 20000g. ";
                }
                if (n500000 > 0){
                    if (n500000 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n500000) + " " + palabra + " de 500000g. ";
                }
                //* Adición 02-04-2019 para pesas de 1 tn.
                if (n1000000 > 0){
                    if (n1000000 > 1){
                        palabra="pesas";
                    }else{
                        palabra="pesa";
                    }
                    forma_formula= forma_formula + " | " + String.valueOf(n1000000) + " " + palabra + " de 1000000g. ";
                }
                // *
                if (Crp1 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp1) +" como 1° Carga de Sustitución ";
                }
                if (Crp2 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp2) +" como 2° Carga de Sustitución ";
                }
                if (Crp3 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp3) +" como 3° Carga de Sustitución ";
                }
                if (Crp4 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp4) +" como 4° Carga de Sustitución ";
                }
                if (Crp5 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp5) +" como 5° Carga de Sustitución ";
                }
                if (Crp6 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp6) +" como 6° Carga de Sustitución ";
                }
                if (Crp7 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp7) +" como 7° Carga de Sustitución ";
                }
                if (Crp8 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp8) +" como 8° Carga de Sustitución ";
                }
                if (Crp9 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp9) +" como 9° Carga de Sustitución ";
                }
                if (Crp10 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp10) +" como 10° Carga de Sustitución ";
                }
                if (Crp11 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp11) +" como 11° Carga de Sustitución ";
                }
                if (Crp12 > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(Crp12) +" como 12° Carga de Sustitución ";
                }
                if (AjCrp > 0){
                    forma_formula= forma_formula + " | " + String.valueOf(AjCrp) +" como valor de ajuste ";
                }

                String str = "delete from Pesxpro where IdeComBpr='" + idecombpr + "'  and TipPxp='C" + String.valueOf(contador) + "-'";
                //db.execSQL(str);

                String str1="Insert into Pesxpro values (null, " +
                        "'" + idecombpr + "', " +
                        "'C" + String.valueOf(contador) + "-', " +
                        "'" + nombre + "', " +
                        "" + n1 + ", " +
                        "" + n2 + ", " +
                        "" + n2a + ", " +
                        "" + n5 + ", " +
                        "" + n10 + ", " +
                        "" + n20 + ", " +
                        "" + n20a + ", " +
                        "" + n50 + ", " +
                        "" + n100 + ", " +
                        "" + n200 + ", " +
                        "" + n200a + ", " +
                        "" + n500 + ", " +
                        "" + n1000 + ", " +
                        "" + n2000 + ", " +
                        "" + n2000a + ", " +
                        "" + n5000 + ", " +
                        "" + n10000 + ", " +
                        "" + n20000 + ", " +
                        "" + n500000 + ", " +
                        "" + n1000000 + " " +
                        ")";
                //Modificación para adicionar pesa de 1 tn. 02-04-2019
                // db.execSQL(str1);

            } while (c.moveToNext());
        }
        c.close();
        forma_formula=forma_formula + " | ";
        formula.setText(forma_formula);

    }

    public String formateado(String sin_forma) {
        Globals g = (Globals) getApplication();
        final String formato = g.getFormato();

        String numero=sin_forma;
        double num_d=Double.valueOf(numero);
        String editado = String.format(formato,num_d);

        return editado;
    }

    @Override
    public void onBackPressed() {



        Intent descendente = new Intent(Carga_dsc.this, Carga_dsc.class);
        descendente.putExtra("idecombpr", idecombpr);
        descendente.putExtra("conteo", contador);
        descendente.putExtra("unidad",uni_recibida);
        startActivity(descendente);
        finish();
    }

}