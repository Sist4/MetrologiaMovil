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

public class Ambientales_fin extends AppCompatActivity {

    private BDManager metrologia;
    private SQLiteDatabase db;
    private double temperatura,humedad;
    private ambientales_bdd obj;
    private int contador_t,contador_h;
    private EditText txtTempfin,txtHumdfin;
    private String idecombpr;
    public static Activity cl_ambientales_fin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magnitudes_final);
        {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            cl_ambientales_fin=this;

            idecombpr = getIntent().getExtras().getString("idecombpr");

            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();

            txtTempfin =(EditText)findViewById(R.id.txtTempFinal);
            txtHumdfin =(EditText)findViewById(R.id.txtHumedadFinal);


            String[] args = new String[] {idecombpr};
            Cursor c = db.rawQuery("SELECT count(TemFinAmb),count(HumRelFinAmb) FROM Ambientales where IdeComBpr=?", args);

            if (c.moveToFirst()) {
                do {
                    contador_t=c.getInt(0);
                    contador_h=c.getInt(1);
                } while(c.moveToNext());
            }
            c.close();

            if (contador_t == 0){
                //txtTempfin.setText("0");
                txtTempfin.setHint("0");
            }else{
                args=new String[] {idecombpr};
                c = db.rawQuery(" SELECT TemFinAmb FROM Ambientales where IdeComBpr=?", args);

                if (c.moveToFirst()) {
                    do {
                        temperatura=c.getDouble(0);
                    } while(c.moveToNext());
                }
                c.close();
                txtTempfin.setText(String.valueOf(temperatura));
            }

            if (contador_h == 0){
                //txtHumdfin.setText("0");
                txtHumdfin.setHint("0");
            }else{
                args=new String[] {idecombpr};
                c = db.rawQuery(" SELECT HumRelFinAmb FROM Ambientales where IdeComBpr=?", args);

                if (c.moveToFirst()) {
                    do {
                        humedad=c.getDouble(0);
                    } while(c.moveToNext());
                }

                txtHumdfin.setText(String.valueOf(humedad));
            }


            Button btnguardaambientalesf=(Button)findViewById(R.id.btnGuardarAmbientalesF);
            btnguardaambientalesf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String temp=txtTempfin.getText().toString();
                    String hum = txtHumdfin.getText().toString();
                    if ((temp.equals(""))||(hum.equals(""))){
                        Toast.makeText(getApplicationContext(), "Por favor digite datos válidos.", Toast.LENGTH_SHORT).show();
                        return;
                    }else {

                        String inserta_amb = "update Ambientales set TemFinAmb=" + txtTempfin.getText().toString() + ", " +
                                "HumRelFinAmb=" + txtHumdfin.getText().toString() + " where IdeComBpr='" + idecombpr + "'";
                        db.execSQL(inserta_amb);
                        Toast.makeText(getApplicationContext(), "Datos Registrados exitosamente.", Toast.LENGTH_SHORT).show();


                        Intent eval = new Intent(Ambientales_fin.this, Evaluacion_previa.class);
                        eval.putExtra("idecombpr", idecombpr);
                        startActivity(eval);
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent ambientalesI = new Intent(Ambientales_fin.this,Ambientales_fin.class);
        ambientalesI.putExtra("idecombpr",idecombpr);
        startActivity(ambientalesI);
        finish();
    }

}