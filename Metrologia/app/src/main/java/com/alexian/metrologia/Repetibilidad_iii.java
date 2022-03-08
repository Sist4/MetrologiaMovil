package com.alexian.metrologia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Generado por Ing.Iván Villavicencio el 2017-06-12.
 */

public class Repetibilidad_iii extends AppCompatActivity {
    private BDManager metrologia;
    private SQLiteDatabase db;
    private String idecombpr,escogida,uni_recibida,uni_e,uni_d,div_uso,uni_uso;
    private EditText carga1,lec1,lec1_0,lec2,lec2_0,lec3,lec3_0,carga2,diferencia,emp_rep_ii;
    private Button guarda_rep_ii,btaplica;
    private ImageButton selec_carga;
    private String origen="repetibilidad_iii";
    private TextView resul,unidad;
    private Switch swCrgSust;
    private double div_e,div_d,div_calculo;
    private RelativeLayout rl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repetibilidad_iii);
        {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


            idecombpr = getIntent().getExtras().getString("idecombpr");
            escogida = getIntent().getExtras().getString("carga_escogida");
            uni_recibida = getIntent().getExtras().getString("unidad");

            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();

            carga1=(EditText)findViewById(R.id.txtCarga_rep_ii_1);
            lec1=(EditText)findViewById(R.id.txtLec1);
            lec1_0=(EditText)findViewById(R.id.txtLec1_0);
            lec2=(EditText)findViewById(R.id.txtLec2);
            lec2_0=(EditText)findViewById(R.id.txtLec2_0);
            lec3=(EditText)findViewById(R.id.txtLec3);
            lec3_0=(EditText)findViewById(R.id.txtLec3_0);
            carga2=(EditText)findViewById(R.id.txtCarga_rep_ii_2);
            diferencia=(EditText)findViewById(R.id.txtDifMax_rep_ii);
            emp_rep_ii=(EditText)findViewById(R.id.txtemp_rep_ii);
            selec_carga=(ImageButton)findViewById(R.id.ibSelecCarga_rep_ii);
            guarda_rep_ii=(Button)findViewById(R.id.btGuardar_rep_ii);
            resul=(TextView)findViewById(R.id.lblResulta_Repii);
            swCrgSust=(Switch)findViewById(R.id.swCrgSust_repiii);
            btaplica=(Button)findViewById(R.id.btAplicaCrgSust_repiiii);
            unidad=(TextView)findViewById(R.id.lbUniRepiii);
            unidad.setText(uni_recibida);
            rl=(RelativeLayout)findViewById(R.id.lyRep_iii);
            //Toast.makeText(getApplicationContext(),uni_recibida, Toast.LENGTH_SHORT).show();

            carga1.setEnabled(false);
            lec1.setEnabled(false);
            lec1_0.setEnabled(false);
            lec2.setEnabled(false);
            lec2_0.setEnabled(false);
            lec3.setEnabled(false);
            lec3_0.setEnabled(false);
            carga2.setEnabled(false);
            diferencia.setEnabled(false);
            emp_rep_ii.setEnabled(false);
            guarda_rep_ii.setEnabled(false);
            btaplica.setVisibility(View.INVISIBLE);

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(lec1.getWindowToken(), 0);
                }
            });

            if (escogida.equals("")){

                double capuso=0.0;
                double capmax=0;
                String capcal="";
                String[] args = new String[]{idecombpr};
                Cursor c = db.rawQuery(" SELECT CapMaxBpr,CapUsoBpr,CapCalBpr,DivEscBpr,UniDivEscBpr, " +
                        " DivEsc_dBpr,UniDivEsc_dBpr,DivEscCalBpr FROM Balxpro where idecombpr=?", args);
                if (c.moveToFirst()) {
                    do {
                        capmax = c.getDouble(0);
                        capuso = c.getDouble(1);
                        capcal = c.getString(2);
                        div_e=c.getDouble(3);
                        uni_e=c.getString(4);
                        div_d=c.getDouble(5);
                        uni_d=c.getString(6);
                        div_uso=c.getString(7);

                    } while (c.moveToNext());
                }
                c.close();
                double cap_a_usar=0.0;
                if (capcal.equals("uso")){
                    cap_a_usar=capuso;
                }else{
                    cap_a_usar=capmax;
                }

                if (div_uso.equals("e")) {
                    div_calculo = div_e;
                    uni_uso=uni_e;
                } else {
                    div_calculo = div_d;
                    uni_uso=uni_d;
                }

                double carga_propuesta=cap_a_usar*0.80;
                final String edita=formateado(String.valueOf(carga_propuesta));
                carga1.setText(edita);

                selec_carga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String carga = carga1.getText().toString();
                        Intent acarga = new Intent(Repetibilidad_iii.this, SeteoCarga.class);
                        acarga.putExtra("carga", carga);
                        acarga.putExtra("origen", origen);
                        acarga.putExtra("idecombpr", idecombpr);
                        acarga.putExtra("unidad",uni_recibida);
                        startActivity(acarga);
                    }
                });

                swCrgSust.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (swCrgSust.isChecked()){
                            selec_carga.setEnabled(false);
                            carga1.setEnabled(true);
                            btaplica.setVisibility(View.VISIBLE);
                            carga1.setText("");
                            carga1.setHint("0");
                        }else{
                            selec_carga.setEnabled(true);
                            carga1.setEnabled(false);
                            btaplica.setVisibility(View.INVISIBLE);
                            carga1.setText(edita);
                        }
                    }
                });

            }else{

                carga1.setText(escogida);
                carga1.setEnabled(false);

                lec1.setEnabled(true);

                //Formateo de los valores de los edit texts
                lec1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        lec1_0.setEnabled(true);
                        maximo();
                        Calcemp_Repii();
                        resulta();

                    }
                });
                lec1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            lec1_0.setEnabled(true);
                            lec1.setNextFocusForwardId(lec1_0.getId() );
                        }
                        return false;
                    }
                });
                lec1_0.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String edita=formateado(lec1.getText().toString());
                        lec1.setText(edita);
                        lec2.setEnabled(true);
                        maximo();
                        Calcemp_Repii();
                        resulta();
                    }
                });
                lec1_0.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            lec2.setEnabled(true);
                            lec1_0.setNextFocusForwardId(lec2.getId() );
                        }
                        return false;
                    }
                });
                lec2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String edita=formateado(lec1_0.getText().toString());
                        lec1_0.setText(edita);
                        lec2_0.setEnabled(true);
                        maximo();
                        Calcemp_Repii();
                        resulta();
                    }
                });
                lec2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            lec2_0.setEnabled(true);
                            lec2.setNextFocusForwardId(lec2_0.getId() );
                        }
                        return false;
                    }
                });
                lec2_0.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String edita=formateado(lec2.getText().toString());
                        lec2.setText(edita);
                        lec3.setEnabled(true);
                        maximo();
                        Calcemp_Repii();
                        resulta();
                    }
                });
                lec2_0.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            lec3.setEnabled(true);
                            lec2_0.setNextFocusForwardId(lec3.getId() );
                        }
                        return false;
                    }
                });
                lec3.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String edita=formateado(lec2_0.getText().toString());
                        lec2_0.setText(edita);
                        lec3_0.setEnabled(true);
                        maximo();
                        Calcemp_Repii();
                        resulta();
                    }
                });
                lec3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            lec3_0.setEnabled(true);
                            lec3.setNextFocusForwardId(lec3_0.getId() );
                        }
                        return false;
                    }
                });
                lec3_0.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String edita=formateado(lec3.getText().toString());
                        lec3.setText(edita);
                        guarda_rep_ii.setEnabled(true);
                        maximo();
                        Calcemp_Repii();
                        resulta();
                    }
                });
                lec3_0.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            guarda_rep_ii.setEnabled(true);
                            lec3_0.setNextFocusForwardId(guarda_rep_ii.getId() );
                        }
                        return false;
                    }
                });

                selec_carga.setEnabled(false);
                swCrgSust.setVisibility(View.INVISIBLE);
                btaplica.setVisibility(View.INVISIBLE);

                guarda_rep_ii.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String edita=formateado(lec3_0.getText().toString());
                        lec3_0.setText(edita);

                        maximo();
                        Calcemp_Repii();
                        resulta();

                        if ((lec1.equals(""))||
                                (lec1_0.equals(""))||
                                (lec2.equals(""))||
                                (lec2_0.equals(""))||
                                (lec3.equals(""))||
                                (lec3_0.equals(""))){
                            Toast.makeText(getApplicationContext(),"Debe ingresar todas las lecturas requeridas.", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            AlertDialog.Builder dialogo1=new AlertDialog.Builder(Repetibilidad_iii.this);
                            dialogo1.setTitle("GRABAR REPETIBILIDAD");
                            dialogo1.setMessage("¿Esta seguro de grabar la prueba de Repetibilidad?");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Grabar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Guardar();
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

            btaplica.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llena_Sustitucion();
                    String crg_tipeada=formateado(carga1.getText().toString());
                    if (crg_tipeada.equals((""))){
                        Toast.makeText(getApplicationContext(),"Debe ingresar " +
                                "un valor diferente de 0(cero)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Double.valueOf(crg_tipeada)<=0){
                        Toast.makeText(getApplicationContext(),"Ingrese " +
                                "un valor diferente de 0(cero)", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        String uni_envio = unidad.getText().toString();
                        Intent repiii = new Intent(Repetibilidad_iii.this, Repetibilidad_iii.class);
                        repiii.putExtra("idecombpr", idecombpr);
                        //excec_cam.putExtra("carga_escogida",real.getText().toString());
                        repiii.putExtra("carga_escogida", crg_tipeada);
                        repiii.putExtra("unidad", uni_envio);
                        startActivity(repiii);
                        finish();
                    }
                }
            });
        }
    }

    public void Guardar(){
        int icod = 0;
        String Str="";
        String Str1="";
        /*String[] args = new String[]{idecombpr};
        Cursor c = db.rawQuery(" SELECT CodRiii_C FROM RepetIII_Cab where idecombpr=?",args);
        if (c.moveToFirst()) {
            do {
                icod=c.getInt(0);
            } while (c.moveToNext());
        }
        c.close();

        if (icod != 0) {*/

            //Str = "delete from RepetIII_Cab where CodRiii_C=" + Str.valueOf(icod) + "";
            Str = "delete from RepetIII_Cab where IdeComBpr='" + idecombpr + "'";
            db.execSQL(Str);

            //Str1 = "delete from RepetIII_Det where CodRiii_C = " + Str.valueOf(icod) + "";
            Str1 = "delete from RepetIII_Det where CodRiii_C = '" + idecombpr + "'";
            db.execSQL(Str1);
        //}

        Str="Insert into RepetIII_Cab values (null, " +
                " " + Double.valueOf(carga1.getText().toString()) + ", " +
                " " + Double.valueOf(diferencia.getText().toString()) + ", " +
                " " + Double.valueOf(emp_rep_ii.getText().toString()) + ", " +
                " '" + resul.getText().toString() + "', " +
                " '" + idecombpr + "')";
        db.execSQL(Str);

        /*int cod=0;
        c = db.rawQuery(" SELECT max(CodRiii_C) FROM RepetIII_Cab",null);
        if (c.moveToFirst()) {
            do {
                cod=c.getInt(0);
            } while (c.moveToNext());
        }
        c.close();*/


        //Código reemplazado 09-11-2017 por Ivan Villavicencio. Se cambia el dato que se envía a la columna del código de cabecera.
        //en lugar de ello se envía el IdeComBpr + el número de prueba.
        String cod=idecombpr;


        Str1="Insert into RepetIII_Det values (null, " +
                " " + Double.valueOf(lec1.getText().toString()) + ", " +
                " " + Double.valueOf(lec1_0.getText().toString()) + ", " +
                " " + Double.valueOf(lec2.getText().toString()) + ", " +
                " " + Double.valueOf(lec2_0.getText().toString()) + ", " +
                " " + Double.valueOf(lec3.getText().toString()) + ", " +
                " " + Double.valueOf(lec3_0.getText().toString()) + ", " +
                " '" + cod +  "')";
                //" " + cod +  ")";
        db.execSQL(Str1);

        Toast.makeText(getApplicationContext(),"Información almacenada exitosamente.", Toast.LENGTH_SHORT).show();

        Intent ambientalesI = new Intent(Repetibilidad_iii.this,Ambientales_fin.class);
        ambientalesI.putExtra("idecombpr",idecombpr);
        startActivity(ambientalesI);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent repetibilidad = new Intent(Repetibilidad_iii.this, Repetibilidad_iii.class);
        repetibilidad.putExtra("idecombpr", idecombpr);
        repetibilidad.putExtra("carga_escogida", "");
        repetibilidad.putExtra("unidad",uni_recibida);
        startActivity(repetibilidad);
        finish();
    }


    public String formateado(String sin_forma) {
        Globals g = (Globals) getApplication();
        final String formato = g.getFormato();

        String numero=sin_forma;
        double num_d=Double.valueOf(numero);
        String editado = String.format(formato,num_d);

        return editado;
    }

    public void maximo(){
        double lecturas[]={0,0,0};
        String l1=lec1.getText().toString();
        String l2=lec2.getText().toString();
        String l3=lec3.getText().toString();

        if (l1.equals("")) {
            lecturas[0]=0;
        }else{
            lecturas[0] = Double.valueOf(l1);
        }

        if (l2.equals("")) {
            lecturas[1]=0;
        }else{
            lecturas[1] = Double.valueOf(l2);
        }
        if (l3.equals("")) {
            lecturas[2]=0;
        }else{
            lecturas[2] = Double.valueOf(l3);
        }

        double max,min;
        min=max=lecturas[0];
        for (int i=0; i < lecturas.length; i++){
            if(min > lecturas[i]){
                min=lecturas[i];
            }
            if (max < lecturas[i]){
                max=lecturas[i];
            }
        }
        double dif=max-min;
        String edita = formateado(String.valueOf(dif));
        diferencia.setText(edita);
    }

    public void Calcemp_Repii(){
        double e=0;
        String clase="";
        double uno_e=0;
        double dos_e=0;
        double emp=0;

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
        }

        if (Double.valueOf(escogida) <= uno_e){
            emp=(e*1);
        }else if (Double.valueOf(escogida) <= dos_e){
            emp=(e*2);
        }else{
            emp=(e*3);
        }

        String edita = formateado(String.valueOf(emp));
        emp_rep_ii.setText(edita);
    }

    public void resulta(){
        String resultado="";

        String edita=formateado(carga1.getText().toString());
        carga2.setText(edita);

        if (Double.valueOf(diferencia.getText().toString()) <= (Double.valueOf(emp_rep_ii.getText().toString())*1.001)){
            resultado="SATISFACTORIA";
        }else{
            resultado="NO SATISFACTORIA";
        }
        resul.setText(resultado);
    }

    public void llena_Sustitucion() {
        String borra = "delete from Sustxpro where idecombpr='" + idecombpr + "' and TipSxp='REP'";
        db.execSQL(borra);

        String Str = "Insert into Sustxpro values " +
                "(null, '" + idecombpr + "','REP'," + carga1.getText().toString() + ", 0,0,0,0,0,0,0,0,0,0)";
        db.execSQL(Str);
    }
}