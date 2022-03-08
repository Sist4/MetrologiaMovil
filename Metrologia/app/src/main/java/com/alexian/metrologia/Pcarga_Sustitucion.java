package com.alexian.metrologia;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Pcarga_Sustitucion  extends AppCompatActivity
{
    private BDManager metrologia;
    private SQLiteDatabase db;
    //REVISAR

    private String alterno="Pcarga_Sustitucion",viene_maspeso,viene_ajuste,viene_masa,Clase_eq;
   // private Button BtSI,BtNo;
    private ImageButton ibvisto,ibseteo2,ibmas,ibvisto2,ibborra,Btn_Guardar;
    private EditText txtAjuste, txtMaspeso,txtCrg_mas_peso,txtLecturaCombinada,carga_sust,Txt_CargaFinal;
    private int contador,maxima,uso,cuenta_sustitucion,bandera;
    private String idecombpr,contador_A, escogida, DivisionUso, CargaUso,uni_recibida,est_sw,mantiene,uni_rec;
    private ImageButton btcargaCgr, btguardaLecturaCgr;
    private TextView Lbl_ContadorActual;
    private TextView lbEtiq_crg,lbAjuste,lbUni_masa_crg,Txt_Contador,Lbl_CargaDesc;
    private Double e, d, division_calculo, lacarga, ultima_carga;
    private Button btAplica;

    //private EditText  etlectura;
    private String origen = "Pcarga_Sustitucion";
    private int etapa_sust,carga_calculo,contadorN;
    private List<String> li = new ArrayList<String>();
    private GridView gv;
    private  int codigo;



    private void mensaje(String method, String message)
    {
        Log.d("EXCEPTION: " + method, message);
        AlertDialog.Builder messageBox = new AlertDialog.Builder(this);
        messageBox.setTitle(method); messageBox.setMessage(message); messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
    }

    /*
    public void muestra_datos( String Codigo_Proyectos, int contador) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, li);
        String Carga_tomada = "", num_toma = "", lectura_tomada = "", error_tomado = "";

        li.add("N°");
        li.add("Carga");
        li.add("Lectura Asc");
        li.add("Err Asc");
        li.add("Opción");

        try {
            String[] args = new String[]{Codigo_Proyectos,"" + contador};
            Cursor c = db.rawQuery("select cab.CarPca as Carga,cab.NumPca as numero,LecAscPca as LecturaAsc,ErrAscPca as Error_Asc" +
                    " from Pcarga_cab as cab,Pcarga_det as det" +
                    " where cab.idecombpr=? and cab.NumPca >? and cab.CodPca_C = det.CodPca_D", args);
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
    */

    public void muestra_datos(String Codigo_Proyectos, int contador) {
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
            String[] args = new String[]{Codigo_Proyectos,"" + contador};
            Cursor c = db.rawQuery("select cab.CarPca as Carga,cab.NumPca as numero, " +
                    " det.LecAscPca as LecturaAsc,det.ErrAscPca as Error_Asc, " +
                    " det.LecDscPca as LecturaDsc,det.ErrDscPca as Error_Dsc, " +
                    " det.EmpPca as emp, " +
                    " det.SatPca_D as satisface " +
                    " from Pcarga_cab as cab,Pcarga_det as det" +
                    " where cab.idecombpr=? and cab.NumPca >? and cab.CodPca_C = det.CodPca_D", args);



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







    public String formateado(String sin_forma)
    {
        Globals g = (Globals) getApplication();
        final String formato = g.getFormato();
        String numero=sin_forma;
        double num_d=Double.valueOf(numero);
        String editado = String.format(formato,num_d);
        return editado;
    }
    private void Desactiva_Cajas()
    {
        try
        {

            carga_sust.setVisibility(View.VISIBLE);
            //carga_sust.setEnabled(false);
            txtAjuste.setVisibility(View.VISIBLE);
            txtAjuste.setEnabled(false);
            txtMaspeso.setVisibility(View.VISIBLE);
            txtMaspeso.setEnabled(false);
            txtLecturaCombinada.setVisibility(View.VISIBLE);
            txtLecturaCombinada.setEnabled(false);
            txtCrg_mas_peso.setVisibility(View.VISIBLE);
            txtCrg_mas_peso.setEnabled(false);
            //ETIQUETAS
            lbAjuste.setVisibility(View.VISIBLE);
            lbUni_masa_crg.setVisibility(View.VISIBLE);
            lbEtiq_crg.setVisibility(View.VISIBLE);
            lbUni_masa_crg.setVisibility(View.VISIBLE);
            //fin Etiwuetas
            // botones
            ibvisto.setVisibility(View.INVISIBLE);
            ibvisto.setEnabled(false);
            ibseteo2.setVisibility(View.INVISIBLE);
            ibseteo2.setEnabled(false);
            ibmas.setVisibility(View.INVISIBLE);
            ibmas.setEnabled(false);
            ibborra.setVisibility(View.INVISIBLE);
            ibvisto2.setVisibility(View.INVISIBLE);
            // fin de los botones
        }
        catch (Exception e)
        {
            mensaje("error",e.getMessage() );

        }


    }
    private void Cargar_Datos()
    {
        try {
            final String CSustitucion = getIntent().getExtras().getString("CSustitucion");
            final String ARequerido = getIntent().getExtras().getString("ARequerido");
            final String estado_sw = getIntent().getExtras().getString("estado_sw");
            idecombpr = getIntent().getExtras().getString("idecombpr");//Codigo del proyecto
            escogida = getIntent().getExtras().getString("carga_escogida");
            uni_recibida = getIntent().getExtras().getString("unidad");
            contador_A = getIntent().getExtras().getString("Contador_Actual");
            contadorN=getIntent().getExtras().getInt ("contadorN");
           // cuenta_sustitucion = getIntent().getExtras().getInt("cta_susti");
            viene_ajuste= getIntent().getExtras().getString("ajuste");
            viene_masa= getIntent().getExtras().getString("masa");
            viene_maspeso= getIntent().getExtras().getString("maspeso");
            etapa_sust=getIntent().getExtras().getInt("etapa");
            cuenta_sustitucion = getIntent().getExtras().getInt("cta_susti");
            bandera=getIntent().getExtras().getInt("bandera");
                //Cuando inicia la primera caraga de sustitucion
                 if((estado_sw.equals("INICIO"))){
                     Desactiva_Cajas();
                     carga_sust.setEnabled(true);
                     carga_sust.requestFocus();
             //cuando guardo
            }else if (estado_sw.equals("Selcciono"))  {
                carga_sust.setText("" + CSustitucion);
                txtAjuste.setText("" + ARequerido);
                txtMaspeso.setText(viene_masa);
                //
                     carga_sust.setEnabled(false);
                     txtAjuste.setEnabled(false);
                     ibseteo2.setEnabled(true);
                     ibseteo2.setVisibility(View.VISIBLE);
                     ibvisto2.setEnabled(true);
                     ibvisto2.setVisibility(View.VISIBLE);


            }else if (estado_sw.equals("NuevaCarga"))  {
                     carga_sust.setText("" + CSustitucion);
                     txtAjuste.setText("" + ARequerido);
                     txtMaspeso.setText(viene_masa);


                 }
                Txt_Contador.setText("" + contadorN);
                Lbl_ContadorActual.setText(contador_A);
                //carga_sust.setEnabled(false);

//            muestra_datos(idecombpr,(Integer.parseInt(contador_A )+ contadorN));
            muestra_datos(idecombpr,(Integer.parseInt(contador_A )));

        }catch (Exception e)
        {
            mensaje("Error",e.getMessage());
        }

    }
    private void guarda_lectura(int vienede) {

 try {

     int codcab = 0;
     double carga = 0;
     double Ajuste = 0;
     double lectura = 0;
     double diferencia = 0;
     contador = (contadorN + Integer.parseInt(contador_A.replace(" ","")));
     //carga = Double.valueOf(txtLecturaCombinada.getText().toString());
     carga = Double.valueOf(txtCrg_mas_peso.getText().toString());
     Ajuste = Double.valueOf(txtAjuste.getText().toString());
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
     if ((contador > 1) && (carga <= 0)) {
         Toast.makeText(getApplicationContext(), "Para esta iteración, " +
                 "la carga no puede ser igual a 0(cero)" + String.valueOf(contador), Toast.LENGTH_SHORT).show();
         return;
     }

     String Str = "Insert into PCarga_Cab values (null," + carga + "," +
             "" + contador + ",'" + idecombpr + "','SATISFACTORIA')";
     db.execSQL(Str);
     //Código reemplazado 09-11-2017 por Ivan Villavicencio. Se cambia el dato que se envía a la columna del código de cabecera.
     //en lugar de ello se envía el IdeComBpr + el número de prueba.
     String Scodcab = idecombpr + contador;
     String Str1 = "Insert into PCarga_Det (CodPca_D,LecAscPca,ErrAscPca,CodPca_C,SatPca_D) values (null," + lectura + "," +
             "" + diferencia + ",'" + Scodcab + "','SATISFACTORIA')";
     db.execSQL(Str1);

    ibmas.setVisibility(View.INVISIBLE);
    ibborra.setVisibility(View.INVISIBLE);
    txtLecturaCombinada.setEnabled(false);
     Lbl_CargaDesc.setVisibility(View.VISIBLE);
     Txt_CargaFinal.setVisibility(View.VISIBLE);
     Btn_Guardar.setVisibility(View.VISIBLE);
     Txt_CargaFinal.setEnabled(true);
     Txt_CargaFinal.requestFocus();


     /*
     cuenta_sustitucion++;
     contador++;
     String cgr_sust = carga_sust.getText().toString();
     String aju = txtAjuste.getText().toString();
     String msa = txtMaspeso.getText().toString();
     String crg_mas_peso = txtCrg_mas_peso.getText().toString();
     Intent PCargaS = new Intent(Pcarga_Sustitucion.this, Pcarga_Sustitucion.class);
     PCargaS.putExtra("idecombpr", idecombpr);
     PCargaS.putExtra("estado_sw", "Nueva");
     //PCargaS.putExtra("cta_susti", cuenta_sust);
     PCargaS.putExtra("cgr_sust",cgr_sust);
     PCargaS.putExtra("bandera",bandera);
     PCargaS.putExtra("Contador_Actual",contador_A);
     PCargaS.putExtra("contadorN",contadorN + 1);
     PCargaS.putExtra("unidad",uni_recibida);
     startActivity(PCargaS);
     finish();
     */

 } catch (Exception e){
     mensaje("Boton Guaradar",e.getMessage());
 }



}

    public void errmp(){
        try{
            double carga = Double.valueOf(txtCrg_mas_peso.getText().toString());
            String clase="";
            double uno_e=0;
            double dos_e=0;
            double emp=0;
            double errasc=0;
            double errdsc=0;
            String resulta="";
            contador=(contadorN + Integer.parseInt(contador_A.replace(" ","")));
            String[] args = new String[]{idecombpr,String.valueOf(contador)};
            Cursor c = db.rawQuery(" select CodPca_C,CarPca from PCarga_Cab where idecombpr=? and NumPca=?", args);
            if (c.moveToFirst()) {
                do {
                    codigo=c.getInt(0);
                    lacarga=c.getDouble(1);
                } while (c.moveToNext());
            }
            c.close();



            args = new String[]{idecombpr};
            c = db.rawQuery(" SELECT DivEscBpr,ClaBpr FROM Balxpro where idecombpr=?", args);
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

            //String edita = "e.m.p.: " + formateado(String.valueOf(emp));
            //empcrg.setText(edita);

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


        }catch (Exception e){
            mensaje("Editar",e.getMessage());
        }

    }




    public void guarda_lecturadesc() {
        double carga = Double.valueOf(txtCrg_mas_peso.getText().toString());
        double lectura = 0;
        double diferencia = 0;
        contador=(contadorN + Integer.parseInt(contador_A.replace(" ","")));
        String SCarga = txtCrg_mas_peso.getText().toString();
        String SLectura = Txt_CargaFinal.getText().toString();
        if ((SLectura.equals("")) || (SCarga.equals(""))) {
            Toast.makeText(getApplicationContext(), "Debe Ingresar la " +
                    "lectura Descendente N° " + String.valueOf(contador), Toast.LENGTH_SHORT).show();
            return;
        } else {
            lectura = Double.valueOf(Txt_CargaFinal.getText().toString());
            diferencia = Math.abs(carga - lectura);
        }

        String Str1 = "Update PCarga_Det set LecDscPca=" + lectura + ", " +
                "ErrDscPca=" + diferencia + " where CodPca_C='" + idecombpr+contador + "'";
        //"ErrDscPca=" + diferencia + " where CodPca_C=" + codigo + "";
        //"ErrDscPca=" + diferencia + " where CodPca_C=" + codigo + "";
        db.execSQL(Str1);

        errmp();

    }














    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcarga_sustitucion);{
        metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
        db = metrologia.getWritableDatabase();
       // BtSI = (Button) findViewById(R.id.Btn_SI);
        //BtNo = (Button) findViewById(R.id.Btn_NO);
        gv = (GridView) findViewById(R.id.gvVistaAsc);
        btAplica  = (Button) findViewById(R.id.btAplicar_Crg_asc);
        carga_sust = (EditText) findViewById(R.id.txtCrgSust_crg_asc);
        txtAjuste = (EditText) findViewById(R.id.txtAjuste);
        txtMaspeso = (EditText) findViewById(R.id.txtMasPeso);
        txtCrg_mas_peso = (EditText) findViewById(R.id.txtCrg_mas_peso);
        txtLecturaCombinada = (EditText) findViewById(R.id.txtLecturaCombinada);
        Txt_CargaFinal = (EditText) findViewById(R.id.Txt_CargaFinal);

        //***Fin de cajas de Texto
        //Botones
        ibvisto = (ImageButton) findViewById(R.id.ibvisto);
        ibvisto2 = (ImageButton) findViewById(R.id.ibvisto2);
        ibmas = (ImageButton) findViewById(R.id.ibmas2);
        ibseteo2 = (ImageButton) findViewById(R.id.ibseteo);
        ibborra = (ImageButton) findViewById(R.id.ibborra);
        Btn_Guardar = (ImageButton) findViewById(R.id.Btn_Guardar);

        //FIN DE LOS BOTONES
        //labal
        Lbl_ContadorActual = (TextView) findViewById(R.id.Lbl_ContadorActual);
        lbAjuste = (TextView) findViewById(R.id.Lbl_Ajuste);
        lbUni_masa_crg = (TextView) findViewById(R.id.Lbl_Masa);
        lbEtiq_crg = (TextView) findViewById(R.id.Lbl_Carga);
        lbUni_masa_crg = (TextView) findViewById(R.id.Lbl_Lectura);
        Txt_Contador = (TextView) findViewById(R.id.Txt_Contador);
        Lbl_CargaDesc = (TextView) findViewById(R.id.Lbl_CargaDesc);
        Toast.makeText(getApplicationContext(), "ACEPTO", Toast.LENGTH_SHORT).show();
        //Desactiva_Cajas();
        //Si acepta las cargas de sustitucion
        carga_sust.setVisibility(View.VISIBLE);
        carga_sust.setEnabled(true);
        carga_sust.requestFocus();
        Cargar_Datos();

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
                    try
                    {
                        //double sumado = Double.valueOf(masa_pesas.getText().toString()) + Double.valueOf(carga_sust.getText().toString());
                        ///--
                        double sumado=0.0;
                        ///--
                        String lacarga = formateado(String.valueOf(sumado));
                        txtCrg_mas_peso.setText(lacarga);
                        btguardaLecturaCgr.setEnabled(true);
                        Txt_CargaFinal.setEnabled(true);
                        // swSustitucion.setEnabled(false);
                        carga_sust.setEnabled(false);
                    }catch (Exception e)
                    {
                        mensaje("ERROR",e.getMessage());
                    }
                }
            }
        });



        //Controlamos el paso al Edit de ajuste con la acción "Siguiente" de carga_sust
        carga_sust.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    String ft = formateado(carga_sust.getText().toString());
                    carga_sust.setText(ft);
                    txtAjuste.setEnabled(true);
                    ibvisto.setEnabled(true);
                    txtAjuste.requestFocus();
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

        //Controlamos la visibilidad del imagebutton visto con la acción del edittext
        txtAjuste.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    try {
                        String ft = formateado(txtAjuste.getText().toString());
                        txtAjuste.setText(ft);
                        //txtAjuste.setEnabled(true);
                        ibvisto.setEnabled(false);
                        txtMaspeso.setEnabled(true);
                        txtMaspeso.requestFocus();
                        ibseteo2.setEnabled(true);
                        ibseteo2.setVisibility(View.VISIBLE);
                        ibvisto2.setEnabled(true);
                        ibvisto2.setVisibility(View.VISIBLE);



                        /*txtCrg_mas_peso.setEnabled(false);
                        txtMaspeso.setEnabled(true);
                        ibvisto.setVisibility(View.INVISIBLE);
                        ibvisto.setEnabled(false);
                        txtMaspeso.requestFocus();*/
                    }catch (Exception e)

                    {
                        mensaje("ERROR",e.getMessage());
                    }
                }
                return false;
            }
        });

        txtMaspeso.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    String msp = txtMaspeso.getText().toString();
                    if (msp.equals("")) {
                        Toast.makeText(getApplicationContext(), "Debe especificar la carga en pesas " +
                                "que se debe setear." + String.valueOf(contador), Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        try {
                            ibseteo2.setEnabled(true);
                            ibseteo2.setVisibility(View.VISIBLE);
                            ibvisto2.setEnabled(true);
                            ibvisto2.setVisibility(View.VISIBLE);
                        }catch (Exception e)
                        {
                            mensaje("ERROR",e.getMessage());
                        }
                    }
                }
                return false;
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
                String msp = txtMaspeso.getText().toString();
                if (msp.equals("")) {

                    Toast.makeText(getApplicationContext(), "Debe especificar la carga en pesas " +
                            "que se debe setear.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {

                        //cargar datos

                        String[] args = new String[]{idecombpr};
                        Cursor c = db.rawQuery("SELECT ClaBpr,DivEscBpr,DivEsc_dBpr,DivEscCalBpr,CapCalBpr,CapMaxBpr,CapUsoBpr FROM Balxpro where IdeComBpr=?", args);
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

                        // fin de carga a de datos
                        double crgenv = Double.valueOf(carga_sust.getText().toString());
                        double ajuenv = Double.valueOf(txtAjuste.getText().toString());
                        double realenv = crgenv - ajuenv + (division_calculo / 2);
                        String cgr_sust = carga_sust.getText().toString();
                        String aju = txtAjuste.getText().toString();
                        String msa = txtMaspeso.getText().toString();
                        String crg_mas_peso = txtCrg_mas_peso.getText().toString();//REVISAR
                        Intent acarga = new Intent(Pcarga_Sustitucion.this, SeteoCarga.class);
                        String crg_a = formateado(txtMaspeso.getText().toString());
                        acarga.putExtra("carga", crg_a);
                        acarga.putExtra("origen", "Pcarga_Sustitucion");//
                        acarga.putExtra("idecombpr", idecombpr);
                        acarga.putExtra("unidad", uni_recibida);//REVISAR
                        acarga.putExtra("conteo", Txt_Contador.getText().toString());
                        acarga.putExtra("estado_sw", est_sw);//REVISAR
                        acarga.putExtra("cta_susti", cuenta_sustitucion);
                        acarga.putExtra("cgr_sust", cgr_sust);
                        acarga.putExtra("ajuste", aju);
                        acarga.putExtra("masa", msa);
                        acarga.putExtra("maspeso", crg_mas_peso);//REVISAR
                        acarga.putExtra("etapa", 2);//REVISAR
                        acarga.putExtra("real", realenv);
                        acarga.putExtra("seteo", 1);//REVISAR
                        acarga.putExtra("bandera", bandera);
                        //nUEVAS CARGAS DE SUSTITUCION ANGEL AUCANCELA
                        acarga.putExtra("contadorN", contadorN);
                        acarga.putExtra("CSustitucion", carga_sust.getText().toString());
                        acarga.putExtra("ARequerido", txtAjuste.getText().toString());
                        acarga.putExtra("Contador_Actual", Lbl_ContadorActual.getText().toString());
                        ///
                        //Nuevas variables
                        startActivity(acarga);
                    }catch (Exception e)
                    {
                        mensaje("ERROR",e.getMessage());
                    }
                }
            }
        });

        ibmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               guarda_lectura(2);
                muestra_datos(idecombpr,(Integer.parseInt(contador_A )));
            }
        });


        Btn_Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                guarda_lecturadesc();
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Pcarga_Sustitucion.this);
                dialogo1.setTitle("Nueva Carga de Sustitucion");
                dialogo1.setMessage("¿Desea realizar otra carga de sustitucion? ");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                     cuenta_sustitucion++;
                     contador++;
                     String cgr_sust = carga_sust.getText().toString();
                     String aju = txtAjuste.getText().toString();
                     String msa = txtMaspeso.getText().toString();
                     String crg_mas_peso = txtCrg_mas_peso.getText().toString();
                     Intent PCargaS = new Intent(Pcarga_Sustitucion.this, Pcarga_Sustitucion.class);
                     PCargaS.putExtra("idecombpr", idecombpr);
                     PCargaS.putExtra("estado_sw", "Nueva");
                     //PCargaS.putExtra("cta_susti", cuenta_sust);
                     PCargaS.putExtra("cgr_sust",cgr_sust);
                     PCargaS.putExtra("bandera",bandera);
                     PCargaS.putExtra("Contador_Actual",contador_A);
                     PCargaS.putExtra("contadorN",contadorN + 1);
                     PCargaS.putExtra("unidad",uni_recibida);
                     startActivity(PCargaS);
                     finish();



                    }
                });
                dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent repetibilidad = new Intent(Pcarga_Sustitucion.this, Repetibilidad_iii.class);
                        repetibilidad.putExtra("idecombpr", idecombpr);
                        repetibilidad.putExtra("carga_escogida", "");
                        repetibilidad.putExtra("unidad",uni_recibida);
                        startActivity(repetibilidad);
                        finish();


                    }
                });
                dialogo1.show();



            }
        });





    }
    }


}