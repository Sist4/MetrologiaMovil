package com.alexian.metrologia;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.RestrictionEntry;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ing.Iván on 2017-05-15.
 */

public class Excentricidad_cam extends AppCompatActivity {
    private BDManager metrologia;
    private SQLiteDatabase db;
    private EditText etcarga,etp1,etp1r,etp2,etp2r,etp3,etp3r,et_ex_max,et_emp,mas_menos_e;
    private TextView resul,unidad;
    private Button btguarda,btaplica;
    private ImageButton btcarga;
    private double d,e,division_calculo;
    private int maxima,uso,carga_calculo;
    private String CargaUso,Clase_eq,DivisionUso;
    private String origen="Excentricidad_cam(1)";
    private double posiciones []={0,0,0,0,0,0};
    private String idecombpr,escogida,uni_e,uni_d,uni_uso,uni_recibida;
    private Switch swCrgSust;
    private Selec_Proyecto cx1 = new Selec_Proyecto();
    private Verifica_cliente cx2 = new Verifica_cliente();
    private Selec_Equipo cx3 = new Selec_Equipo();
    private Verifica_equipo cx4 = new Verifica_equipo();
    private Certificados cx5 = new Certificados();
    private Ambientales_Inicio cx6 = new Ambientales_Inicio();
    private Patrones_peso cx7 = new Patrones_peso();
    private visuales cx8 = new visuales();
    private servidorftp cx9 = new servidorftp();

    private void messageBox(String method, String message)
    {
        Log.d("EXCEPTION: " + method, message);
        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(method); messageBox.setMessage(message); messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.excentricidad_cam);
        {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            idecombpr = getIntent().getExtras().getString("idecombpr");
            escogida = getIntent().getExtras().getString("carga_escogida");
            uni_recibida = getIntent().getExtras().getString("unidad");

            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();
            etcarga = (EditText) findViewById(R.id.txtCargaExc_ii);
            etp1 = (EditText) findViewById(R.id.txtpos1);
            etp1r=(EditText)findViewById(R.id.txtpos4);
            etp2 = (EditText) findViewById(R.id.txtpos2);
            etp2r=(EditText)findViewById(R.id.txtpos5);
            etp3 = (EditText) findViewById(R.id.txtpos3);
            etp3r=(EditText)findViewById(R.id.txtpos6);
            et_ex_max = (EditText) findViewById(R.id.txtExct_max_iii);
            et_emp = (EditText) findViewById(R.id.txt_e_m_p_ex_iii);
            btcarga = (ImageButton) findViewById(R.id.ibSelecCarga);
            btguarda = (Button) findViewById(R.id.btGuarda);
            resul = (TextView)findViewById(R.id.lblResultado);
            unidad=(TextView)findViewById(R.id.txtUnid);
            swCrgSust=(Switch)findViewById(R.id.swCrgSustitucion);
            swCrgSust.setEnabled(true);
            btaplica=(Button)findViewById(R.id.btAplicarCargaSust);
            btaplica.setVisibility(View.INVISIBLE);
            mas_menos_e=(EditText)findViewById(R.id.tx_mas_menos_e_cam1);

            etp1.setEnabled(false);
            etp1r.setEnabled(false);
            etp2.setEnabled(false);
            etp2r.setEnabled(false);
            etp3.setEnabled(false);
            etp3r.setEnabled(false);
            et_ex_max.setEnabled(false);
            et_emp.setEnabled(false);
            mas_menos_e.setEnabled(false);
            btguarda.setEnabled(false);
            etcarga.setEnabled(false);


            if (escogida.equals("")) {

                String[] args = new String[]{idecombpr};
                Cursor c = db.rawQuery(" SELECT ClaBpr,DivEscBpr,DivEsc_dBpr,DivEscCalBpr,CapCalBpr," +
                        "CapMaxBpr,CapUsoBpr," +
                        "UniDivEscBpr,UniDivEsc_dBpr FROM Balxpro where IdeComBpr=?", args);
                if (c.moveToFirst()) {
                    do {
                        Clase_eq = c.getString(0);
                        e = c.getDouble(1);
                        d = c.getDouble(2);
                        DivisionUso = c.getString(3);
                        CargaUso = c.getString(4);
                        maxima = c.getInt(5);
                        uso = c.getInt(6);
                        uni_e=c.getString(7);
                        uni_d= c.getString(8);
                    } while (c.moveToNext());
                }
                c.close();

                if (DivisionUso.equals("e")) {
                    division_calculo = e;
                    uni_uso=uni_e;
                } else {
                    division_calculo = d;
                    uni_uso=uni_d;
                }

                if (CargaUso.equals("uso")) {
                    carga_calculo = uso;
                } else {
                    carga_calculo = maxima;
                }

                if (uni_uso.equals("k")) {
                    unidad.setText("kg.");
                }else{
                    unidad.setText("g.");
                }

                final double lacarga = (carga_calculo / 3);
                final String crg=formateado(String.valueOf(lacarga));
                etcarga.setText(crg);
                //etcarga.setText(String.format("%.2f", lacarga));

                swCrgSust.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (swCrgSust.isChecked()){
                            btcarga.setEnabled(false);
                            etcarga.setEnabled(true);
                            btaplica.setVisibility(View.VISIBLE);
                            etcarga.setText("");
                            etcarga.setHint("0");
                        }else{
                            btcarga.setEnabled(true);
                            etcarga.setEnabled(false);
                            btaplica.setVisibility(View.INVISIBLE);
                            etcarga.setText(crg);
                        }
                    }
                });


                btaplica.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String crg_tipeada=etcarga.getText().toString();
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
                            try {
                                llena_Sustitucion();
                                String uni_envio = unidad.getText().toString();
                                Intent excec_cam = new Intent(Excentricidad_cam.this, Excentricidad_cam.class);
                                excec_cam.putExtra("idecombpr", idecombpr);
                                excec_cam.putExtra("carga_escogida", crg_tipeada);
                                excec_cam.putExtra("unidad", uni_envio);
                                startActivity(excec_cam);
                                finish();
                            }catch(Exception e)
                            {
                                messageBox("ERROR GUARDAR", e.getMessage());
                            }
                        }
                    }
                });

                btcarga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String carga = etcarga.getText().toString();
                            String uni_envio = unidad.getText().toString();
                            Intent acarga = new Intent(Excentricidad_cam.this, SeteoCarga.class);
                            acarga.putExtra("carga", carga);
                            acarga.putExtra("origen", origen);
                            acarga.putExtra("idecombpr", idecombpr);
                            acarga.putExtra("unidad", uni_envio);
                            startActivity(acarga);
                        }catch(Exception e)
                        {
                            messageBox("ERROR GUARDAR", e.getMessage());
                        }

                    }
                });
            }else{
                try {
                    maximo();
                    emp_excentricidad();
                    resulta();
                    etp1.setEnabled(true);
                    etp1.requestFocus();
                    swCrgSust.setEnabled(false);
                    et_ex_max.setEnabled(false);
                    et_emp.setEnabled(false);
                    mas_menos_e.setEnabled(false);
                    btguarda.setEnabled(true);
                    etcarga.setText(escogida);
                    etcarga.setEnabled(false);
                    unidad.setText(uni_recibida);
                    swCrgSust.setVisibility(View.INVISIBLE);
                    btaplica.setVisibility(View.INVISIBLE);
                    btcarga.setEnabled(false);
                } catch(Exception e)
                    {
                        messageBox("ERROR GUARDAR", e.getMessage());
                    }
                etp1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        maximo();
                        emp_excentricidad();
                        resulta();

                    }
                });
                etp1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            etp2.setEnabled(true);
                            etp2.requestFocus();
                        }
                        return false;
                    }
                });

                etp2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        maximo();
                        emp_excentricidad();
                        resulta();
                    }
                });
                etp2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            etp3.setEnabled(true);
                            etp3.requestFocus();
                        }
                        return false;
                    }
                });

                etp3.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        maximo();
                        emp_excentricidad();
                        resulta();
                    }
                });
                etp3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            etp3r.setEnabled(true);
                            //etp3r.requestFocus();
                            etp3.setNextFocusForwardId(etp3r.getId() );
                        }
                        return false;
                    }
                });

                etp1r.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        maximo();
                        emp_excentricidad();
                        resulta();
                    }
                });
                etp1r.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            btaplica.setEnabled(true);
                            //btaplica.requestFocus();
                            etp1r.setNextFocusForwardId(btaplica.getId() );
                        }
                        return false;
                    }
                });

                etp2r.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        maximo();
                        emp_excentricidad();
                        resulta();
                    }
                });
                etp2r.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            etp1r.setEnabled(true);
                            //etp1r.requestFocus();
                            etp2r.setNextFocusForwardId(etp1r.getId() );
                        }
                        return false;
                    }
                });

                etp3r.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        maximo();
                        emp_excentricidad();
                        resulta();
                    }
                });
                etp3r.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_NEXT){
                            etp2r.setEnabled(true);
                            //etp2r.requestFocus();
                            etp3r.setNextFocusForwardId(etp2r.getId() );
                        }
                        return false;
                    }
                });


                btguarda.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String posi1=etp1.getText().toString();
                        String posi1r=etp1r.getText().toString();
                        String posi2=etp2.getText().toString();
                        String posi2r=etp2r.getText().toString();
                        String posi3=etp3.getText().toString();
                        String posi3r=etp3r.getText().toString();
                        if ((posi1.equals(""))||
                                (posi1r.equals(""))||
                                (posi2.equals(""))||
                                (posi2r.equals(""))||
                                (posi3.equals(""))||
                                (posi3r.equals(""))) {
                            Toast.makeText(getApplicationContext(), "Debe ingresar los seis(6) " +
                                    "valores requeridos.", Toast.LENGTH_SHORT).show();

                            return;

                        }else {
                            AlertDialog.Builder dialogo1=new AlertDialog.Builder(Excentricidad_cam.this);
                            dialogo1.setTitle("GRABAR EXCENTRICIDAD 1");
                            dialogo1.setMessage("¿Esta seguro de grabar la prueba de Excentricidad 1? Una vez " +
                                    "grabados estos datos se acepta toda la información almacenada con anterioridad " +
                                    "y únicamente se podrá continuar con el resto de pruebas.");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Grabar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cx1.cl_selec_proyecto.finish();
                                    cx2.cl_verifica_cliente.finish();
                                    cx3.cl_select_equipo.finish();
                                    cx4.cl_verifica_equipo.finish();
                                    cx5.cl_certificados.finish();
                                    cx6.cl_ambientales_inicio.finish();
                                    cx7.cl_patrones_peso.finish();
                                    cx8.cl_visuales.finish();
                                    cx9.cl_servidor_ftp.finish();
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

                btcarga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            String carga = etcarga.getText().toString();
                            String uni_envio = unidad.getText().toString();
                            Intent acarga = new Intent(Excentricidad_cam.this, SeteoCarga.class);
                            acarga.putExtra("carga", carga);
                            acarga.putExtra("origen", origen);
                            acarga.putExtra("idecombpr", idecombpr);
                            acarga.putExtra("unidad", uni_envio);
                            startActivity(acarga);
                        }

        catch(Exception e)

                    {
                        messageBox("ERROR GUARDAR", e.getMessage());
                    }
                    }
                });
            }

        }
    }
    public void maximo(){
        try {
            String posi1 = etp1.getText().toString();
            String posi2 = etp2.getText().toString();
            String posi3 = etp3.getText().toString();
            String posi1r = etp1r.getText().toString();
            String posi2r = etp2r.getText().toString();
            String posi3r = etp3r.getText().toString();
            if (posi1.equals(""))
                posiciones[0] = 0;
            else
                posiciones[0] = Double.valueOf(posi1);

            if (posi1r.equals(""))
                posiciones[1] = 0;
            else
                posiciones[1] = Double.valueOf(posi1r);

            if (posi2.equals(""))
                posiciones[2] = 0;
            else
                posiciones[2] = Double.valueOf(posi2);

            if (posi2r.equals(""))
                posiciones[3] = 0;
            else
                posiciones[3] = Double.valueOf(posi2r);

            if (posi3.equals(""))
                posiciones[4] = 0;
            else
                posiciones[4] = Double.valueOf(posi3);

            if (posi3r.equals(""))
                posiciones[5] = 0;
            else
                posiciones[5] = Double.valueOf(posi3r);


            double max, min;
            min = max = posiciones[0];
            for (int i = 0; i < posiciones.length; i++) {
                if (min > posiciones[i]) {
                    min = posiciones[i];
                }
                if (max < posiciones[i]) {
                    max = posiciones[i];
                }
            }

            double ex_max = max - min;
            String edt = formateado(String.valueOf(ex_max));
            //et_ex_max.setText(String.format("%.6f", max));
            et_ex_max.setText(edt);
        }
        catch(Exception e)
        {
            messageBox("ERROR GUARDAR", e.getMessage());
        }
    }

    public void emp_excentricidad(){
        try
        {
        String mas_menos;
        double e=0;
        String clase="";
        double uno_e=0;
        double dos_e=0;
        double emp=0;
        String[] args = new String[]{idecombpr};
        Cursor c = db.rawQuery(" SELECT DivEscBpr,ClaBpr FROM Balxpro where IdeComBpr=?", args);
        if (c.moveToFirst()) {
            do {
                e=c.getDouble(0);
                clase=c.getString(1);
                maxima = c.getInt(0);
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

        if (Double.valueOf(escogida) <= uno_e){
            emp=(e*1);
            mas_menos="1e";
        }else if (Double.valueOf(escogida) <= dos_e){
            emp=(e*2);
            mas_menos="2e";
        }else{
            emp=(e*3);
            mas_menos="3e";
        }

        et_emp.setText(String.valueOf(emp));
        mas_menos_e.setText("±" + mas_menos);
        }
        catch(Exception e)
        {
            messageBox("ERROR", e.getMessage());
        }
    }

    public void resulta(){

        try {
            String resultado = "";
            if (Double.valueOf(et_ex_max.getText().toString()) <= (Double.valueOf(et_emp.getText().toString()) * 1.001)) {
                resultado = "SATISFACTORIA";
            } else {
                resultado = "NO SATISFACTORIA";
            }
            resul.setText(resultado);
        }
        catch(Exception e)
        {
            messageBox("ERROR GUARDAR", e.getMessage());
        }
    }

    public void Guardar(){

        try {
            int icod = 0;
            String Str = "";
            String Str1 = "";

            String[] args = new String[]{idecombpr, "1"};
            Cursor c = db.rawQuery(" SELECT CodCam_c FROM ExecCam_Cab where IdeComBpr=? and PrbCam_c=?", args);
            if (c.moveToFirst()) {
                do {
                    icod = c.getInt(0);
                } while (c.moveToNext());
            }
            c.close();

            if (icod != 0) {

                Str = "delete from ExecCam_Cab where CodCam_c=" + Str.valueOf(icod) + "";
                db.execSQL(Str);

                //Str1 = "delete from ExecCam_Det where CodCam_c = " + Str.valueOf(icod) + "";
                Str1 = "delete from ExecCam_Det where CodCam_c = '" + idecombpr + "1" + "' or CodCam_c = '" + idecombpr + "2" + "'";
                db.execSQL(Str1);
            }

            Str = "Insert into ExecCam_Cab values (null," + Double.valueOf(escogida) + ",1,'" +
                    resul.getText().toString() + "','" + idecombpr + "')";
            db.execSQL(Str);

        /*int cod=0;
        c = db.rawQuery(" SELECT max(CodCam_c) FROM ExecCam_Cab",null);
        if (c.moveToFirst()) {
            do {
                cod=c.getInt(0);
            } while (c.moveToNext());
        }
        c.close();*/
            //Código reemplazado 09-11-2017 por Ivan Villavicencio. Se cambia el dato que se envía a la columna del código de cabecera.
            //en lugar de ello se envía el IdeComBpr + el número de prueba.
            String cod = idecombpr + "1";

            Str = "Insert into ExecCam_Det values (null," + Double.valueOf(etp1.getText().toString()) + "," +
                    "" + Double.valueOf(etp1r.getText().toString()) + "," +
                    "" + Double.valueOf(etp2.getText().toString()) + "," +
                    "" + Double.valueOf(etp2r.getText().toString()) + "," +
                    "" + Double.valueOf(etp3.getText().toString()) + "," +
                    "" + Double.valueOf(etp3r.getText().toString()) + "," +
                    "" + Double.valueOf(et_ex_max.getText().toString()) + "," +
                    "" + Double.valueOf(et_emp.getText().toString()) + "," +
                    "'" + cod + "')";
            //"" + cod +")";
            db.execSQL(Str);
            Toast.makeText(getApplicationContext(), "Información almacenada exitosamente.", Toast.LENGTH_SHORT).show();
            Intent excec_cam = new Intent(Excentricidad_cam.this, Excentricidad_cam_2.class);
            excec_cam.putExtra("idecombpr", idecombpr);
            excec_cam.putExtra("carga_escogida", escogida);
            excec_cam.putExtra("unidad", uni_recibida);
            startActivity(excec_cam);
            finish();
        }
        catch(Exception e)
        {
            messageBox("ERROR GUARDAR", e.getMessage());
        }
    }

    public String formateado(String sin_forma)
    {
        String editado="";
        try {
            Globals g = (Globals) getApplication();
            final String formato = g.getFormato();

            String numero = sin_forma;
            double num_d = Double.valueOf(numero);
             editado = String.format(formato, num_d);


        }
        catch(Exception e)
            {
                messageBox("ERROR", e.getMessage());
                editado="1";
            }

        return editado;
    }




    public void llena_Sustitucion(){
        try
        {
            String borra = "delete from Sustxpro where IdeComBpr='" + idecombpr + "' and TipSxp='ECA1'";
            db.execSQL(borra);

            String Str = "Insert into Sustxpro values " +
                    "(null, '" + idecombpr + "','ECA1'," + etcarga.getText().toString() + ", 0,0,0,0,0,0,0,0,0,0)";
            db.execSQL(Str);
        }
        catch(Exception e)
            {
                messageBox("ERROR", e.getMessage());
            }
    }



}