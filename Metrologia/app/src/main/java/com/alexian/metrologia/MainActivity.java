package com.alexian.metrologia;

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



public class MainActivity extends AppCompatActivity {
    private String passw;
    private String codmetro;
    private EditText txtusu,txtpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        txtusu=(EditText)findViewById(R.id.txtUsuario);
        txtpass=(EditText)findViewById(R.id.txtContrasena);
        BDManager metrologia =
                new BDManager(this,"SistMetPrec.db",null,1);

        final SQLiteDatabase db = metrologia.getWritableDatabase();

        txtusu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String eseusu=txtusu.getText().toString();
                eseusu=eseusu.toUpperCase();
                txtusu.setText(eseusu);
            }
        });

        Button btnIngreso = (Button) findViewById(R.id.btnIngreso);
        btnIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usuario=((EditText)findViewById(R.id.txtUsuario)).getText().toString().trim();
                String contraseña=((EditText)findViewById(R.id.txtContrasena)).getText().toString();

                String[] args = new String[] {usuario,"A"};
                Cursor c = db.rawQuery(" SELECT ClaMet,CodMet FROM Metrologos where NomMet=? and estMet=?", args);
                if (c.moveToFirst()) {
                    //Toast.makeText(getApplicationContext(),"Entro",Toast.LENGTH_SHORT).show();
                    do {
                        passw=c.getString(0);
                        codmetro=c.getString(1);
                    } while(c.moveToNext());
                }
                c.close();
                db.close();

                if (contraseña.equals(passw))
                {
                    //Toast.makeText(getApplicationContext(),"Ingreso Correcto" ,Toast.LENGTH_SHORT).show();

                    Globals g = (Globals)getApplication();
                    g.setCodmetrologo(codmetro);
                    /*Intent selproyecto = new Intent(MainActivity.this,Selec_Proyecto.class);
                    selproyecto.putExtra("codmetro",codmetro);
                    startActivity(selproyecto);*/
                    Intent servidorftp = new Intent(MainActivity.this,servidorftp.class);
                    servidorftp.putExtra("codmetro",codmetro);
                    startActivity(servidorftp);
                    finish();

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Usuario Incorrecto",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(getIntent());
                }
            }

        });
    }
}

