package com.alexian.metrologia;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ing.Iván on 2017-09-30.
 */

public class adiciona_equipo extends AppCompatActivity {

    private Spinner spAdiciona;
    private EditText txtDescr_adc,txtMarca_adc,txtMod_adc,txtCapmax_adc,txtCapuso_adc,txtDivision_adc,txtDivision_d_adc;
    private RadioButton rb_kg_adc,rb_g_adc;
    private Button btAdiciona;
    private RelativeLayout rl;
    private BDManager metrologia;
    private SQLiteDatabase db;
    private String metrologo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.adiciona_equipo);

        metrologo = getIntent().getExtras().getString("codmetro");

        metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
        db = metrologia.getWritableDatabase();

        //Declarando objetos
        spAdiciona = (Spinner) findViewById(R.id.spAdiciona);
        txtDescr_adc = (EditText) findViewById(R.id.txtDescr_adc);
        txtMarca_adc = (EditText) findViewById(R.id.txtMarca_adc);
        txtMod_adc = (EditText) findViewById(R.id.txtMod_adc);
        txtCapmax_adc = (EditText) findViewById(R.id.txtCapmax_adc);
        txtCapuso_adc = (EditText) findViewById(R.id.txtCapuso_adc);
        txtDivision_adc = (EditText) findViewById(R.id.txtDivision_adc);
        txtDivision_d_adc = (EditText) findViewById(R.id.txtDivision_d_adc);
        rb_kg_adc = (RadioButton) findViewById(R.id.rb_kg_adc);
        rb_g_adc = (RadioButton) findViewById(R.id.rb_g_adc);
        btAdiciona = (Button) findViewById(R.id.btAdicion_adc);
        rl = (RelativeLayout) findViewById(R.id.lyAdiciona);

        llena();

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtDescr_adc.getWindowToken(), 0);
            }
        });

        btAdiciona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numero="";
                String literal="";
                String cod_proy="";
                String ide_proy = spAdiciona.getItemAtPosition(spAdiciona.getSelectedItemPosition()).toString();
                String descrip = txtDescr_adc.getText().toString();
                String marca = txtMarca_adc.getText().toString();
                String modelo = txtMod_adc.getText().toString();
                String maxima = txtCapmax_adc.getText().toString();
                String uso = txtCapuso_adc.getText().toString();
                String division = txtDivision_adc.getText().toString();
                String division_d = txtDivision_d_adc.getText().toString();
                if ((ide_proy.equals("")) ||
                        (descrip.equals("")) || (marca.equals("")) || (modelo.equals("")) || (maxima.equals("")) ||
                        (uso.equals("")) || (division.equals("")) || (division_d.equals(""))) {
                    Toast.makeText(adiciona_equipo.this, "Debe llenar todos los campos.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    String unid = "";
                    if (rb_kg_adc.isChecked()) {
                        unid = "k";
                    } else {
                        unid = "g";
                    }
                    String[] args = new String[]{ide_proy};//Argumentos de la consulta
                    Cursor c = db.rawQuery("select codpro from proyectos where idepro=?", args);//carga del cursor
                    if (c.moveToFirst()) {
                        do {
                            cod_proy=c.getString(0);
                        } while (c.moveToNext());
                    }
                    c.close();

                    args = new String[]{cod_proy};
                    c = db.rawQuery("select max(numbpr),max(litBpr) from balxpro where codpro=?", args);
                    if (c.moveToFirst()) {
                        do {
                            Integer numbpr = c.getInt(0);
                            numero = String.valueOf(numbpr + 1);
                            String liter = c.getString(1);
                            literal = setea_literal(liter);
                        } while (c.moveToNext());
                    }
                    c.close();
                    String Str="Insert into balxpro (codbpr,numbpr,desbpr,IdentBpr,marbpr,modbpr,SerBpr," +
                            "capmaxbpr,UbiBpr,capusobpr,divescbpr,unidivescbpr,divesc_dbpr,unidivesc_dbpr," +
                            "RanBpr,ClaBpr,codpro,codmet,idebpr,estbpr,litbpr,idecombpr,divesccalbpr,capcalbpr" +
                            ",es_adi) values " +
                            "(null," + numero + ",'" + descrip + "','n/a','" + marca + "','" + modelo + "'," +
                            "'n/a'," + maxima + ",'n/a'," + uso + "," + division + ",'" + unid + "'," +
                            "" + division_d + ",'" + unid + "'," + maxima + ",'n/a'," + cod_proy + "," + metrologo + "," +
                            "'" + ide_proy + "','A','" + literal + "','" + ide_proy+literal + "','e','max',1)";
                    db.execSQL(Str);
                    String Str2="Update Proyectos set EstPro='A' where codpro=" + cod_proy + "";
                    db.execSQL(Str2);
                    Toast.makeText(adiciona_equipo.this,"Se ha creado " +
                            "exitosamente el equipo con el código: " + ide_proy+literal + "." , Toast.LENGTH_LONG).show();
                    Intent selproyecto = new Intent(adiciona_equipo.this,Selec_Proyecto.class);
                    selproyecto.putExtra("codmetro",metrologo);
                    startActivity(selproyecto);
                    finish();
                }
            }
        });
    }

    private String setea_literal(String lit){
        int posi=0;
        int i=0;
        String letras[]={"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"
                , "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"
                , "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC"
                , "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK"
                , "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS"
                , "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA"
                , "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI"
                , "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ"
                , "BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY"
                , "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG"
                , "CH", "CI", "CJ", "CK", "CL", "CM", "CN", "CO"
                , "CP", "CQ", "CR", "CS", "CT", "CU", "CV"};
        for ( i = 0;i<=99;i++){
            if (lit.equals(letras[i])){
                posi=i;
                break;
            }
        }

        lit=letras[i+1];
        return lit;
    }



    private void llena(){
        ArrayList<proyectos> proyectos = new ArrayList<proyectos>();
        try{
            String[] args = new String[] {metrologo};
            Cursor c = db.rawQuery("SELECT distinct(Idepro) FROM Proyectos  where CodMet=? order by idepro desc LIMIT 5", args);
            proyectos obj;
            while(c.moveToNext()){
                obj=new proyectos();
                obj.setIdepro(c.getInt(0));
                proyectos.add(obj);
            }
        }catch (Exception e){
            //TODO: Handle exception
        }

        ArrayAdapter<proyectos> adaptador= new ArrayAdapter<com.alexian.metrologia.proyectos>(this,
                android.R.layout.simple_list_item_1,proyectos);
        spAdiciona.setAdapter(adaptador);
    }

}
