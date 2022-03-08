package com.alexian.metrologia;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Ing.Iván on 2017-05-11.
 */

public class Ambientales_Inicio extends AppCompatActivity {

    private BDManager metrologia;
    private SQLiteDatabase db;
    private double temperatura,humedad;
    private ambientales_bdd obj;
    private int contador_t,contador_h;
    public static Activity cl_ambientales_inicio;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnitudes_inicial);
        {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            cl_ambientales_inicio=this;

            final String idecombpr = getIntent().getExtras().getString("idecombpr");
            //Toast.makeText(getApplicationContext(),idecombpr , Toast.LENGTH_SHORT).show();

            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();
            final EditText txtTempIni =(EditText)findViewById(R.id.txtTempFinal);
            final EditText txtHumdIni =(EditText)findViewById(R.id.txtHumedadFinal);


            //Toast.makeText(getApplicationContext(),codigobpr , Toast.LENGTH_SHORT).show();
            String[] args = new String[] {idecombpr};
            Cursor c = db.rawQuery(" SELECT count(TemIniAmb),count(HumRelIniAmb) FROM Ambientales where IdeComBpr=?", args);

            if (c.moveToFirst()) {
                do {
                    contador_t=c.getInt(0);
                    contador_h=c.getInt(1);
                    } while(c.moveToNext());
            }
            c.close();

            if (contador_t == 0){
                //txtTempIni.setText("0");
                txtTempIni.setHint("0");
            }else{
                args=new String[] {idecombpr};
                c = db.rawQuery(" SELECT TemIniAmb FROM Ambientales where IdeComBpr=?", args);

                if (c.moveToFirst()) {
                    do {
                        temperatura=c.getDouble(0);
                    } while(c.moveToNext());
                }
                c.close();
                txtTempIni.setText(String.valueOf(temperatura));
            }

            if (contador_h == 0){
                //txtHumdIni.setText("0");
                txtHumdIni.setHint("0");
            }else{
                args=new String[] {idecombpr};
                c = db.rawQuery(" SELECT HumRelIniAmb FROM Ambientales where IdeComBpr=?", args);

                if (c.moveToFirst()) {
                    do {
                        humedad=c.getDouble(0);
                    } while(c.moveToNext());
                }

                txtHumdIni.setText(String.valueOf(humedad));
            }


            Button btnguardaambientales=(Button)findViewById(R.id.btnGuardarAmbientales);
            btnguardaambientales.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String temp=txtTempIni.getText().toString();
                    String hum = txtHumdIni.getText().toString();
                    if ((temp.equals(""))||(hum.equals(""))){
                        Toast.makeText(getApplicationContext(), "Por favor digite datos válidos.", Toast.LENGTH_SHORT).show();
                        return;
                    }else {
                        String borra = "Delete from Ambientales where IdeComBpr = '" + idecombpr + "'";
                        db.execSQL(borra);

                        String inserta_amb = "Insert into Ambientales (CodAmb,TemIniAmb,HumRelIniAmb,IdeComBpr) " +
                                "values (null," + txtTempIni.getText() + "," + txtHumdIni.getText() + ",'" + idecombpr + "')";
                        db.execSQL(inserta_amb);
                        Toast.makeText(getApplicationContext(), "Datos Registrados exitosamente.", Toast.LENGTH_SHORT).show();

                        Intent patrones = new Intent(Ambientales_Inicio.this, Patrones_peso.class);
                        patrones.putExtra("idecombpr", idecombpr);
                        startActivity(patrones);
                    }
                }
            });

        }
    }
}

