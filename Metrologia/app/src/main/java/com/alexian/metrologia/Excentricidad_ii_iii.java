package com.alexian.metrologia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ing.Iván on 2017-05-15.
 */

public class Excentricidad_ii_iii extends AppCompatActivity {
    private BDManager metrologia;
    private SQLiteDatabase db;
    private EditText etcarga,etp1,etp2,etp3,etp4,etp5,et_ex_max,et_emp;
    private TextView resul,unidad;
    private Button btguarda;
    private ImageButton btcarga;
    private double capmax,capuso,e,d,division_calculo;
    private String capcal,Clase_eq,DivisionUso,CargaUso,uni_e,uni_d,uni_uso,Uni;
    private int maxima,uso,carga_calculo;
    private String origen="Excentricidad_II(1)";
    private double posiciones []={0,0,0,0,0};
    private String idecombpr,escogida;
    private RelativeLayout rl;
    private Selec_Proyecto cx1 = new Selec_Proyecto();
    private Verifica_cliente cx2 = new Verifica_cliente();
    private Selec_Equipo cx3 = new Selec_Equipo();
    private Verifica_equipo cx4 = new Verifica_equipo();
    private Certificados cx5 = new Certificados();
    private Ambientales_Inicio cx6 = new Ambientales_Inicio();
    private Patrones_peso cx7 = new Patrones_peso();
    private visuales cx8 = new visuales();
    private servidorftp cx9 = new servidorftp();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.excentricidad_ii_iii);
        {
            idecombpr = getIntent().getExtras().getString("idecombpr");
            escogida = getIntent().getExtras().getString("carga_escogida");
            uni_uso = getIntent().getExtras().getString("unidad");

            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();
            etcarga = (EditText) findViewById(R.id.txtCargaExc_ii);
            etp1 = (EditText) findViewById(R.id.txtpos1);
            etp2 = (EditText) findViewById(R.id.txtpos2);
            etp3 = (EditText) findViewById(R.id.txtpos3);
            etp4 = (EditText) findViewById(R.id.txtpos4);
            etp5 = (EditText) findViewById(R.id.txtpos5);
            et_ex_max = (EditText) findViewById(R.id.txtExct_max_iii);
            et_emp = (EditText) findViewById(R.id.txt_e_m_p_ex_iii);
            btcarga = (ImageButton) findViewById(R.id.ibSelecCarga);
            btguarda = (Button) findViewById(R.id.btGuarda);
            resul = (TextView)findViewById(R.id.lblResultado);
            unidad=(TextView)findViewById(R.id.lblunidad_exii);
            rl=(RelativeLayout)findViewById(R.id.lyExii);

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etp1.getWindowToken(), 0);
                }
            });


            if (escogida.equals("")) {
                etp1.setEnabled(false);
                etp2.setEnabled(false);
                etp3.setEnabled(false);
                etp4.setEnabled(false);
                etp5.setEnabled(false);
                et_ex_max.setEnabled(false);
                et_emp.setEnabled(false);
                btguarda.setEnabled(false);
                etcarga.setEnabled(false);

                String[] args = new String[]{idecombpr};
                Cursor c = db.rawQuery(" SELECT ClaBpr,DivEscBpr,DivEsc_dBpr,DivEscCalBpr,CapCalBpr," +
                        "CapMaxBpr,CapUsoBpr," +
                        "UniDivEscBpr,UniDivEsc_dBpr FROM Balxpro where idecombpr=?", args);
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


                //final double lacarga = (cap_a_usar / 3);
                final double lacarga = (carga_calculo / 3);
                String crg=formateado(String.valueOf(lacarga));
                etcarga.setText(crg);
                btcarga.requestFocus();
                //etcarga.setText(String.format("%.2f", lacarga));

            }else{
                maximo();
                emp_excentricidad();
                resulta();
                etp1.setEnabled(true);
                etp2.setEnabled(true);
                etp3.setEnabled(true);
                etp4.setEnabled(true);
                etp5.setEnabled(true);
                et_ex_max.setEnabled(false);
                et_emp.setEnabled(false);
                btguarda.setEnabled(true);
                etcarga.setText(escogida);
                etcarga.setEnabled(false);
                unidad.setText(uni_uso);
                etp1.requestFocus();

                etp1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (etp1.getText().toString().equals("")) {
                        }else{
                            String edita = formateado(etp1.getText().toString());
                            etp1.setText(edita);
                            maximo();
                            emp_excentricidad();
                            resulta();
                        }
                    }
                });
                etp2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (etp2.getText().toString().equals("")) {
                        }else{
                            String edita = formateado(etp2.getText().toString());
                            etp2.setText(edita);
                            maximo();
                            emp_excentricidad();
                            resulta();
                        }
                    }
                });
                etp3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (etp3.getText().toString().equals("")) {
                        }else{
                            String edita = formateado(etp3.getText().toString());
                            etp3.setText(edita);
                            maximo();
                            emp_excentricidad();
                            resulta();
                        }
                    }
                });
                etp4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (etp4.getText().toString().equals("")) {
                        }else{
                            String edita = formateado(etp4.getText().toString());
                            etp4.setText(edita);
                            maximo();
                            emp_excentricidad();
                            resulta();
                        }
                    }
                });
                etp5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (etp5.getText().toString().equals("")) {
                        }else{
                            String edita = formateado(etp5.getText().toString());
                            etp5.setText(edita);
                            maximo();
                            emp_excentricidad();
                            resulta();
                        }
                    }
                });

                btguarda.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String posi1=etp1.getText().toString();
                        String posi2=etp2.getText().toString();
                        String posi3=etp3.getText().toString();
                        String posi4=etp4.getText().toString();
                        String posi5=etp5.getText().toString();
                        if ((posi1.equals(""))||
                                (posi2.equals(""))||
                                (posi3.equals(""))||
                                (posi4.equals(""))||
                                (posi5.equals(""))){
                            Toast.makeText(getApplicationContext(),"Debe ingresar los cinco(5) valores requeridos.", Toast.LENGTH_SHORT).show();
                            return;
                        }else {
                            String edita=formateado(etp5.getText().toString());
                            etp5.setText(edita);
                            AlertDialog.Builder dialogo1=new AlertDialog.Builder(Excentricidad_ii_iii.this);
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


            }

            btcarga.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String carga = etcarga.getText().toString();
                    Intent acarga = new Intent(Excentricidad_ii_iii.this, SeteoCarga.class);
                    acarga.putExtra("carga", carga);
                    acarga.putExtra("origen", origen);
                    acarga.putExtra("idecombpr", idecombpr);
                    acarga.putExtra("unidad",unidad.getText().toString());
                    startActivity(acarga);
                }
            });

        }
    }
    public void maximo(){
        String posi1=etp1.getText().toString();
        String posi2=etp2.getText().toString();
        String posi3=etp3.getText().toString();
        String posi4=etp4.getText().toString();
        String posi5=etp5.getText().toString();
        if (posi1.equals(""))
            posiciones[0]=0;
        else
            posiciones[0]=Math.abs(Double.valueOf(posi1)-Double.valueOf(posi1));

        if (posi2.equals(""))
            posiciones[1]=0;
        else
            posiciones[1]=Math.abs(Double.valueOf(posi2)-Double.valueOf(posi1));

        if (posi3.equals(""))
            posiciones[2]=0;
        else
            posiciones[2]=Math.abs(Double.valueOf(posi3)-Double.valueOf(posi1));

        if (posi4.equals(""))
            posiciones[3]=0;
        else
            posiciones[3]=Math.abs(Double.valueOf(posi4)-Double.valueOf(posi1));

        if (posi5.equals(""))
            posiciones[4]=0;
        else
            posiciones[4]=Math.abs(Double.valueOf(posi5)-Double.valueOf(posi1));

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

        et_ex_max.setText(String.format("%.6f", max));
    }

    public void emp_excentricidad(){
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
                capmax = c.getInt(0);
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

        et_emp.setText(String.valueOf(emp));
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

        String[] args = new String[]{idecombpr,"1"};

        Cursor c = db.rawQuery(" SELECT CodEii_c FROM ExecII_Cab where idecombpr=? and PrbEii_c=?",args);
        if (c.moveToFirst()) {
            do {
                icod=c.getInt(0);
            } while (c.moveToNext());
        }
        c.close();

        if (icod != 0) {

            Str = "delete from ExecII_Cab where CodEii_c=" + Str.valueOf(icod) + "";
            db.execSQL(Str);

            //Str1 = "delete from ExecII_Det where CodEii_c = " + Str.valueOf(icod) + "";
            Str1 = "delete from ExecII_Det where CodEii_c = '" + idecombpr+"1" + "' or CodEii_c = '" + idecombpr+"2" + "'";
            db.execSQL(Str1);
        }

        Str="Insert into ExecII_Cab values (null," + Double.valueOf(escogida) + ",1,'" + idecombpr + "','" + resul.getText().toString() + "')";

        db.execSQL(Str);

        /*int cod=0;
        c = db.rawQuery(" SELECT max(CodEii_c) FROM ExecII_Cab",null);
        if (c.moveToFirst()) {
            do {
                cod=c.getInt(0);
            } while (c.moveToNext());
        }
        c.close();*/

        //Código reemplazado 09-11-2017 por Ivan Villavicencio. Se cambia el dato que se envía a la columna del código de cabecera.
        //en lugar de ello se envía el IdeComBpr + el número de prueba.
        String cod=idecombpr +"1";

        Str="Insert into ExecII_Det values (null," + Double.valueOf(etp1.getText().toString()) + "," +
                "" + Double.valueOf(etp2.getText().toString()) + "," +
                "" + Double.valueOf(etp3.getText().toString()) + "," +
                "" + Double.valueOf(etp4.getText().toString()) + "," +
                "" + Double.valueOf(etp5.getText().toString()) + "," +
                "" + Double.valueOf(et_ex_max.getText().toString()) + "," +
                "" + Double.valueOf(et_emp.getText().toString()) + "," +
                "'" + cod +"')";
                //"" + codigobpr +")";
                //"" + cod +")";
        //Toast.makeText(getApplicationContext(),Str, Toast.LENGTH_SHORT).show();
        db.execSQL(Str);
        Toast.makeText(getApplicationContext(),"Información almacenada exitosamente.", Toast.LENGTH_SHORT).show();
        Intent excec_ii_iii_2 = new Intent(Excentricidad_ii_iii.this,Excentricidad_ii_iii_2.class);
        excec_ii_iii_2.putExtra("idecombpr",idecombpr);
        excec_ii_iii_2.putExtra("unidad",uni_uso);
        excec_ii_iii_2.putExtra("carga_escogida",escogida);
        startActivity(excec_ii_iii_2);
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
}