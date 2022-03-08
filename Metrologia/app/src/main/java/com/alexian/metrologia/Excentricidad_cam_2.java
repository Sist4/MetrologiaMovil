package com.alexian.metrologia;

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

public class Excentricidad_cam_2 extends AppCompatActivity {
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
        setContentView(R.layout.excentricidad_cam_2);
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
            swCrgSust.setVisibility(View.INVISIBLE);
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
            btcarga.setEnabled(false);


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
                        llena_Sustitucion();
                        String crg_tipeada=formateado(etcarga.getText().toString());
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
                            Intent excec_cam = new Intent(Excentricidad_cam_2.this, Excentricidad_cam.class);
                            excec_cam.putExtra("idecombpr", idecombpr);
                            //excec_cam.putExtra("carga_escogida",real.getText().toString());
                            excec_cam.putExtra("carga_escogida", crg_tipeada);
                            excec_cam.putExtra("unidad", uni_envio);
                            startActivity(excec_cam);
                            finish();
                        }
                    }
                });

                btcarga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String carga = etcarga.getText().toString();
                        String uni_envio=unidad.getText().toString();
                        Intent acarga = new Intent(Excentricidad_cam_2.this, SeteoCarga.class);
                        acarga.putExtra("carga", carga);
                        acarga.putExtra("origen", origen);
                        acarga.putExtra("idecombpr", idecombpr);
                        acarga.putExtra("unidad",uni_envio);
                        startActivity(acarga);
                    }
                });
            }else{
                maximo();
                emp_excentricidad();
                resulta();
                etp1.setEnabled(true);
                //etp2.setEnabled(true);
                //etp3.setEnabled(true);
                swCrgSust.setEnabled(false);
                et_ex_max.setEnabled(false);
                et_emp.setEnabled(false);
                mas_menos_e.setEnabled(false);
                btguarda.setEnabled(true);
                etcarga.setText(escogida);
                etcarga.setEnabled(false);
                unidad.setText(uni_recibida);
                btcarga.setEnabled(false);
                llena_Sustitucion();
                etp1.requestFocus();

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
                            //etp3r.requestFocus(View.FOCUS_DOWN);
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
              /*  etp1r.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        try
                        {
                            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                                btaplica.setEnabled(true);
                                //btaplica.requestFocus();
                                etp1r.setNextFocusForwardId(btaplica.getId());
                            }
                        }
                        catch(Exception e)
                        {
                            messageBox("ERROR", e.getMessage());
                        }
                        return false;
                    }
                });
*/
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
                            //etp1r.requestFocus(View.FOCUS_LEFT);
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
                                (posi3r.equals(""))){
                            Toast.makeText(getApplicationContext(),"Debe ingresar los seis(6) " +
                                    "valores requeridos.", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            AlertDialog.Builder dialogo1=new AlertDialog.Builder(Excentricidad_cam_2.this);
                            dialogo1.setTitle("GRABAR EXCENTRICIDAD 2");
                            dialogo1.setMessage("¿Esta seguro de grabar la prueba de Excentricidad 2?");
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

                btcarga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String carga = etcarga.getText().toString();
                        String uni_envio=unidad.getText().toString();
                        Intent acarga = new Intent(Excentricidad_cam_2.this, SeteoCarga.class);
                        acarga.putExtra("carga", carga);
                        acarga.putExtra("origen", origen);
                        acarga.putExtra("idecombpr", idecombpr);
                        acarga.putExtra("unidad",uni_envio);
                        startActivity(acarga);
                    }
                });
            }

        }
    }
    public void maximo(){
        String posi1=etp1.getText().toString();
        String posi2=etp2.getText().toString();
        String posi3=etp3.getText().toString();
        String posi1r=etp1r.getText().toString();
        String posi2r=etp2r.getText().toString();
        String posi3r=etp3r.getText().toString();
        if (posi1.equals(""))
            posiciones[0]=0;
        else
            posiciones[0]=Double.valueOf(posi1);

        if (posi1r.equals(""))
            posiciones[1]=0;
        else
            posiciones[1]=Double.valueOf(posi1r);

        if (posi2.equals(""))
            posiciones[2]=0;
        else
            posiciones[2]=Double.valueOf(posi2);

        if (posi2r.equals(""))
            posiciones[3]=0;
        else
            posiciones[3]=Double.valueOf(posi2r);

        if (posi3.equals(""))
            posiciones[4]=0;
        else
            posiciones[4]=Double.valueOf(posi3);

        if (posi3r.equals(""))
            posiciones[5]=0;
        else
            posiciones[5]=Double.valueOf(posi3r);



        double max,min;
        min=max=posiciones[0];
        for (int i=0; i < posiciones.length; i++){
            if(min > posiciones[i]){
                min=posiciones[i];
            }
            if (max < posiciones[i]){
                max=posiciones[i];
            }
        }

        double ex_max=max-min;
        String edt = formateado(String.valueOf(ex_max));
        //et_ex_max.setText(String.format("%.6f", max));
        et_ex_max.setText(edt);
    }

    public void emp_excentricidad(){
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

    public void resulta(){
        String resultado="";
        if (Double.valueOf(et_ex_max.getText().toString()) <= (Double.valueOf(et_emp.getText().toString())*1.001)){
            resultado="SATISFACTORIA";
        }else{
            resultado="NO SATISFACTORIA";
        }
        resul.setText(resultado);
    }

    public void Guardar(){
        int icod=0;
        String Str="";
        String Str1="";

        String[] args = new String[]{idecombpr,"2"};
        Cursor c = db.rawQuery(" SELECT CodCam_c FROM ExecCam_Cab where IdeComBpr=? and PrbCam_c=?",args);
        if (c.moveToFirst()) {
            do {
                icod=c.getInt(0);
            } while (c.moveToNext());
        }
        c.close();

        if (icod != 0) {

            Str = "delete from ExecCam_Cab where CodCam_c=" + Str.valueOf(icod) + "";
            db.execSQL(Str);

            //Str1 = "delete from ExecCam_Det where CodCam_c = " + Str.valueOf(icod) + "";
            Str1 = "delete from ExecCam_Det where CodCam_c = '" + idecombpr+"2" + "'";
            db.execSQL(Str1);
        }

        Str="Insert into ExecCam_Cab values (null," + Double.valueOf(escogida) + ",2,'" +
                //Str="Insert into ExecCam_Cab values (null," + etcarga.getText().toString() + ",1,'" +
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
        String cod=idecombpr +"2";


        Str="Insert into ExecCam_Det values (null," + Double.valueOf(etp1.getText().toString()) + "," +
                "" + Double.valueOf(etp1r.getText().toString()) + "," +
                "" + Double.valueOf(etp2.getText().toString()) + "," +
                "" + Double.valueOf(etp2r.getText().toString()) + "," +
                "" + Double.valueOf(etp3.getText().toString()) + "," +
                "" + Double.valueOf(etp3r.getText().toString()) + "," +
                "" + Double.valueOf(et_ex_max.getText().toString()) + "," +
                "" + Double.valueOf(et_emp.getText().toString()) + "," +
                "'" + cod +"')";
                //"" + cod +")";
        db.execSQL(Str);
        Toast.makeText(getApplicationContext(),"Información almacenada exitosamente.", Toast.LENGTH_SHORT).show();
        Intent carga = new Intent(Excentricidad_cam_2.this,Carga.class);
        carga.putExtra("idecombpr",idecombpr);
        carga.putExtra("carga_escogida","");
        carga.putExtra("unidad",uni_recibida);
        carga.putExtra("conteo",1);
        carga.putExtra("estado_sw", "I");
        carga.putExtra("cta_susti", 0);
        carga.putExtra("masa","");
        carga.putExtra("cgr_sust","");
        carga.putExtra("ajuste","");
        carga.putExtra("maspeso","");
        carga.putExtra("etapa","");
        carga.putExtra("seteo",1);
        carga.putExtra("bandera",0);
        startActivity(carga);
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

    public void llena_Sustitucion(){
        String borra="delete from Sustxpro where IdeComBpr='" + idecombpr + "' and TipSxp='ECA2'";
        db.execSQL(borra);

        String Str="Insert into Sustxpro values " +
                "(null, '" + idecombpr + "','ECA2'," + etcarga.getText().toString() +", 0,0,0,0,0,0,0,0,0,0)";
        db.execSQL(Str);
    }

    @Override
    public void onBackPressed() {

        Intent excec_cam = new Intent(Excentricidad_cam_2.this,Excentricidad_cam_2.class);
        excec_cam.putExtra("idecombpr",idecombpr);
        excec_cam.putExtra("carga_escogida",escogida);
        excec_cam.putExtra("unidad",uni_recibida);
        startActivity(excec_cam);
        finish();
    }

}