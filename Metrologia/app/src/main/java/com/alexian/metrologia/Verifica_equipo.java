package com.alexian.metrologia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ing.Iván on 2017-05-05.
 */

public class Verifica_equipo extends AppCompatActivity {

    private BDManager metrologia;
    private SQLiteDatabase db;
    private EditText txtlugcal,txdesc,txindenti,txmarca,txmodelo,txserie,txcapmax,txubicacion,txcapuso,txrango,txdiv_e,txdiv_d,txclase;
    private RadioButton radio_kg_e,radio_g_e,radio_kg_d,radio_g_d,radioe,radiod,radiocapm,radiocapu;
    private String uni_e,uni_d,div_para_calculo,cap_para_calculo;
    private String clasebal="";
    private equipos_x_proyectos obj;
    private double n,e,d,max;
    private CheckBox chkCam;
    private String proyecto;
    private RelativeLayout rl;
    private TextView txtinfo;
    public static Activity cl_verifica_equipo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verifica_equipo);
        {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            cl_verifica_equipo=this;

            final String idecombpr = getIntent().getExtras().getString("idecombpr");
            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();
            txtinfo=(TextView)findViewById(R.id.txtPr1);
            txtinfo.setText(idecombpr);

            //Consulta de los datos del cliente
            String[] args = new String[]{idecombpr};//Argumentos de la consulta
            //Cursor c = db.rawQuery(" SELECT * FROM balxpro where Codbpr=?", args);//carga del cursor
            Cursor c = db.rawQuery(" SELECT * FROM balxpro where IdeComBpr=?", args);//carga del cursor
            if (c.moveToFirst()) {
                do {
                    //recolección de datos
                    obj = new equipos_x_proyectos();
                    obj.setDesbpr(c.getString(2));
                    obj.setIdentbpr(c.getString(3));
                    obj.setMabpr(c.getString(4));
                    obj.setModbpr(c.getString(5));
                    obj.setSerbpr(c.getString(6));
                    obj.setCapmaxbpr(c.getInt(7));
                    obj.setUbibpr(c.getString(8));
                    obj.setCapusobpr(c.getInt(9));
                    obj.setDivescbpr(c.getDouble(10));
                    obj.setUnidivescbpr(c.getString(11));
                    obj.setDivesc_dbpr(c.getDouble(12));
                    obj.setUnidivesc_dbpr(c.getString(13));
                    obj.setRanbpr(c.getInt(14));
                    obj.setClabpr(c.getString(15));
                    obj.setDivesccalbpr(c.getString(45));
                    obj.setCapcalbpr(c.getString(46));
                    obj.setlugcalbpr(c.getString(52));
                } while (c.moveToNext());
            }
            c.close();

            // declaración de objetos
            txtlugcal=(EditText)findViewById(R.id.txtLugarCalibracion_ve);
            txdesc = (EditText) findViewById(R.id.txtDescripcionEquipo);
            txindenti = (EditText) findViewById(R.id.txtIdentificacionEquipo);
            txmarca = (EditText) findViewById(R.id.txtMarcaEquipo);
            txmodelo = (EditText) findViewById(R.id.txtModeloEquipo);
            txserie = (EditText) findViewById(R.id.txtSerieEquipo);
            txcapmax = (EditText) findViewById(R.id.txtCapMaxEquipo);
            txubicacion = (EditText) findViewById(R.id.txtUbicacionEquipo);
            txcapuso = (EditText) findViewById(R.id.txtCapUsoEquipo);
            txrango=(EditText)findViewById( R.id.txtRangoEquipo);
            txdiv_e = (EditText) findViewById(R.id.txtDivision_eEquipo);
            radio_kg_e = (RadioButton) findViewById(R.id.radio_kg_e);
            radio_g_e = (RadioButton) findViewById(R.id.radio_g_e);
            txdiv_d = (EditText) findViewById(R.id.txtDivision_dEquipo);
            radio_kg_d = (RadioButton) findViewById(R.id.radio_kg_d);
            radio_g_d = (RadioButton) findViewById(R.id.radio_g_d);
            chkCam = (CheckBox) findViewById(R.id.chkClase);
            txclase = (EditText) findViewById(R.id.txtClaseEquipo);
            radioe=(RadioButton)findViewById(R.id.radio_e);
            radiod=(RadioButton)findViewById(R.id.radio_d);
            //Se oculta el radio button de div. escala de visualización (d), pues no se utilizará en ningún caso
            //solicitado por Wilson Díaz, reunión del 21/07/2017
            radiod.setVisibility(View.INVISIBLE);
            radiocapm=(RadioButton)findViewById(R.id.rdCapMAx);
            radiocapu=(RadioButton)findViewById(R.id.rdCapUso);
            rl=(RelativeLayout)findViewById(R.id.LyVerificaEquipo);




            //Seteo de la información dentro de los cuadros de texto y radios
            txtlugcal.setText(obj.getlugcalbpr());
            txdesc.setText(obj.getDesbpr());
            txindenti.setText(obj.getIdentbpr());
            txmarca.setText(obj.getMabpr());
            txmodelo.setText(obj.getModbpr());
            txserie.setText(obj.getSerbpr());
            txcapmax.setText(String.valueOf(obj.getCapmaxbpr()));
            max = obj.getCapmaxbpr();
            txubicacion.setText(obj.getUbibpr());
            txcapuso.setText(String.valueOf(obj.getCapusobpr()));
            String rng = String.valueOf(obj.getRanbpr());
            if (rng.equals("0")){
                txrango.setText(String.valueOf(obj.getCapusobpr()));
            }else{
                txrango.setText(String.valueOf(obj.getRanbpr()));
            }
            txdiv_e.setText(String.valueOf((obj.getDivescbpr())));
            e = obj.getDivescbpr();
            uni_e = obj.getUnidivescbpr();
            if (uni_e == null) {
                radio_kg_e.setChecked(true);
            }else{
                if (uni_e.equals("k")) {
                    radio_kg_e.setChecked(true);
                } else if (uni_e.equals("g")) {
                    radio_g_e.setChecked(true);
                }
            }
            txdiv_d.setText((String.valueOf(obj.getDivesc_dbpr())));
            d=obj.getDivesc_dbpr();
            uni_d = obj.getUnidivesc_dbpr();
            if (uni_d == null) {
                radio_kg_d.setChecked(true);
            }else{
                if (uni_d.equals("k")) {
                    radio_kg_d.setChecked(true);
                } else if (uni_d.equals("g")) {
                    radio_g_d.setChecked(true);
                }
            }

            div_para_calculo=obj.getDivesccalbpr();
            if (div_para_calculo == null) {
                radioe.setChecked(true);
            }else{
                if (div_para_calculo.equals("e")) {
                    radioe.setChecked(true);
                } else if (div_para_calculo.equals("d")) {
                    radiod.setChecked(true);
                }
            }

            cap_para_calculo=obj.getCapcalbpr();
            if (cap_para_calculo==null){
                radiocapm.setChecked(true);
            }else{
                if (cap_para_calculo.equals("max")){
                    radiocapm.setChecked(true);
                }else if (cap_para_calculo.equals("uso")){
                    radiocapu.setChecked(true);
                }
            }

            String cls= obj.getClabpr();
            if (cls.equals("Camionera")){
                txclase.setText("Camionera");
                chkCam.setChecked(true);
            }else{
                calc_clase();
            }

            //Control de eventos de los Edittext tanto de capacidades como de divisiones de escala.
            txcapmax.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txcapmax.getText().toString().equals("")) {
                    }else{
                        calc_clase();
                    }
                }
            });


            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(txdesc.getWindowToken(), 0);
                }
            });

            txcapuso.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txcapuso.getText().toString().equals("")) {
                    }else{
                        calc_clase();
                    }
                }
            });
            txdiv_e.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txdiv_e.getText().toString().equals("")) {
                    }else{
                        calc_clase();
                    }
                }
            });
            txdiv_d.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txdiv_d.getText().toString().equals("")) {
                    }else{
                        calc_clase();
                    }
                }
            });

            //Eventos on click de los Radio buttons para cálculo
            radioe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        calc_clase();
                }
            });
            radiod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        calc_clase();
                }
            });
            radiocapm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        calc_clase();
                }
            });
            radiocapu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        calc_clase();
                }
            });


            // Evento onclick del check que permite cambiar a clase Camionera
            chkCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pivote = txclase.getText().toString();
                    if (chkCam.isChecked()) {
                        txclase.setText("Camionera");
                    } else {
                            calc_clase();
                    }
                }
            });

            Button btnactualizarequipo = (Button) findViewById(R.id.btnActualizarEquipo);
            btnactualizarequipo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Valida los campos y actualiza la tabla Balxpro con la información completa.
                    String clase= txclase.getText().toString();
                    String unie,unid;
                    String div_en_calculo;
                    String cap_en_calculo;

                    if (radio_kg_e.isChecked()){
                        unie="k";
                    }else {
                        unie="g";
                    }

                    if (radio_kg_d.isChecked()){
                        unid="k";
                    }else{
                        unid="g";
                    }

                    if (radioe.isChecked()){
                        div_en_calculo="e";
                    }else {
                        div_en_calculo="d";
                    }

                    Double nume=0.0;
                    String Snume="";
                    if (div_en_calculo.equals("e")) {
                        nume = Double.valueOf(txdiv_e.getText().toString());
                        Snume=txdiv_e.getText().toString();
                    }else{
                        nume = Double.valueOf(txdiv_d.getText().toString());
                        Snume=txdiv_d.getText().toString();
                    }
                    if ((nume < 1) && (nume > 0)){
                        String a = Snume.substring(Snume.indexOf(".") + 1 );
                        int largo= a.length();
                            String formato="%." + largo + "f";
                            Globals g = (Globals)getApplication();
                            g.setFormato(formato);
                        }else{
                            String formato="%.0f";
                            Globals g = (Globals)getApplication();
                            g.setFormato(formato);
                    }

                    if (radiocapm.isChecked()){
                        cap_en_calculo="max";
                    }else {
                        cap_en_calculo="uso";
                    }

                    //validación de campos vacíos
                    if ((TextUtils.isEmpty(txdesc.getText().toString())) ||
                         TextUtils.isEmpty(txtlugcal.getText().toString()) ||
                         TextUtils.isEmpty(txindenti.getText().toString()) ||
                         TextUtils.isEmpty(txmarca.getText().toString()) ||
                         TextUtils.isEmpty(txmodelo.getText().toString()) ||
                         TextUtils.isEmpty(txserie.getText().toString()) ||
                         TextUtils.isEmpty(txcapmax.getText().toString()) ||
                         TextUtils.isEmpty(txubicacion.getText().toString()) ||
                         TextUtils.isEmpty(txcapuso.getText().toString()) ||
                         TextUtils.isEmpty(txrango.getText().toString()) ||
                         TextUtils.isEmpty(txdiv_e.getText().toString()) ||
                         TextUtils.isEmpty(txdiv_d.getText().toString()) ||
                         TextUtils.isEmpty(txclase.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Todos la información debe ser completada.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Validación de la clase de balanza
                    else if (txclase.getText().toString().equals("Valores fuera de rangos permitidos") ){
                        Toast.makeText(getApplicationContext(),"Clase de balanza no admitida. Por favor revise la DIVISIÓN DE ESCALA y la CAPACIDAD MÁXIMA.", Toast.LENGTH_LONG).show();
                        return;
                    }else if (unie != unid){//Validación que las unidades (kg-g) de cada una de las divisiones de escala (e-d)  sean iguales
                        Toast.makeText(getApplicationContext(),"No se admite diferencia entre las unidades de la DIVISIÓN DE ESCALA DE VERIFICACIÓN (e) y " +
                                "la DIVISIÓN DE ESCALA DE VISUALIZACIÓN (d). Por favor Iguale las unidades (kg. o g.). ", Toast.LENGTH_LONG).show();
                        return;
                    }else {
                        //Apertura de base y actualización de datos en la tabla
                        db = metrologia.getWritableDatabase();
                        String des=txdesc.getText().toString();
                        String Str ="update balxpro set desbpr = '"+ txdesc.getText().toString() +"', " +
                                "identbpr = '" + txindenti.getText().toString() + "', " +
                                "marbpr = '" + txmarca.getText().toString() + "', " +
                                "modbpr = '" + txmodelo.getText().toString() + "', " +
                                "serbpr = '" + txserie.getText().toString() + "', " +
                                "capmaxbpr = " + Integer.valueOf(txcapmax.getText().toString()) + ", " +
                                "ubibpr = '" + txubicacion.getText().toString() + "', " +
                                "capusobpr = " + Integer.valueOf(txcapuso.getText().toString()) + ", " +
                                "divescbpr = " + Double.valueOf(txdiv_e.getText().toString()) + ", " +
                                "unidivescbpr = '" + unie + "', " +
                                "divesc_dbpr = " + Double.valueOf(txdiv_d.getText().toString()) + ", " +
                                "unidivesc_dbpr = '" + unid + "' " + "where IdeComBpr = '" + idecombpr + "';";
                                //"unidivesc_dbpr = '" + unid + "' " + "where CodBpr = " + codigobpr + ";";
                        String Str2 ="update balxpro set ranbpr = " + Integer.valueOf(txrango.getText().toString()) + ", " +
                                "clabpr = '" + txclase.getText().toString() + "', " +
                                "DivEscCalBpr = '" + div_en_calculo + "', " +
                                "CapCalBpr= '" + cap_en_calculo + "', " +
                                "lugcalBpr= '" + txtlugcal.getText().toString() + "' " + "where IdeComBpr = '" + idecombpr + "';";
                                //"CapCalBpr= '" + cap_en_calculo + "' " + "where CodBpr = " + codigobpr + ";";
                        db.execSQL(Str);
                        db.execSQL(Str2);
                        Toast.makeText(getApplicationContext(),"Información actualizada exitosamente.", Toast.LENGTH_SHORT).show();
                        //pasamos a la siguiente activity
                        String[] args = new String[]{idecombpr};//Argumentos de la consulta
                        //Cursor c = db.rawQuery(" SELECT codpro FROM balxpro where Codbpr=?", args);//carga del cursor
                        Cursor c = db.rawQuery(" SELECT codpro FROM balxpro where IdeComBpr=?", args);//carga del cursor
                        if (c.moveToFirst()) {
                            do {
                                 proyecto = String.valueOf(c.getInt(0));
                            } while (c.moveToNext());
                        }
                    /*Intent certificado = new Intent(Verifica_equipo.this,Certificados.class);
                    certificado.putExtra("proyecto",proyecto);
                    certificado.putExtra("idecombpr",idecombpr);
                    startActivity(certificado);*/
                        Intent descarta = new Intent(Verifica_equipo.this,Descarte_equipo.class);
                        descarta.putExtra("proyecto",proyecto);
                        descarta.putExtra("idecombpr",idecombpr);
                        startActivity(descarta);
                    }

                }
            });

            db.close();
        }
    }

    public void calc_clase(){
        Double escala=0.0;
        Double capacidad=0.0;

            /*if (radioe.isChecked()) {
                escala=Double.valueOf(txdiv_e.getText().toString());
            }else{
                escala=Double.valueOf(txdiv_d.getText().toString());
            }

            if (radiocapm.isChecked()){
                capacidad=Double.valueOf(txcapmax.getText().toString());
            }else{
                capacidad=Double.valueOf(txcapuso.getText().toString());
            }*/

            escala=Double.valueOf(txdiv_e.getText().toString());
            capacidad=Double.valueOf(txcapmax.getText().toString());

            n=capacidad/escala;

            if (n >= 10001) {
                clasebal = "II";
            } else if ((n<=10000) && (n>=1001)){
                clasebal="III";
            } else if (n<=1000){
                clasebal="IIII";
            }
            txclase.setText(clasebal);
    }


}
