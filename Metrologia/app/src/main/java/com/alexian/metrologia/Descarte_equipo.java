package com.alexian.metrologia;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ing.Iván on 2018-05-03.
 */

public class Descarte_equipo extends AppCompatActivity {

    private BDManager metrologia;
    private SQLiteDatabase db;
    private Switch sw_descarte;
    private Button bt_procede;
    private TextView txt_motivo;
    private String codigobpr,proyecto;
    public static Activity cl_descarte_equipo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            cl_descarte_equipo=this;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            setContentView(R.layout.descarte);

            final String idecombpr=getIntent().getExtras().getString("idecombpr");
            proyecto = getIntent().getExtras().getString("proyecto");

            metrologia=new BDManager(this,"SistMetPrec.db",null,1);
            db = metrologia.getWritableDatabase();


            sw_descarte=(Switch)findViewById(R.id.swDescarta);
            bt_procede=(Button)findViewById(R.id.btProcede);
            txt_motivo=(TextView)findViewById(R.id.txtMotivo);
            txt_motivo.setVisibility(View.INVISIBLE);
            bt_procede.setText("CONTINUAR");

            bt_procede.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String textobt = bt_procede.getText().toString();
                    if (textobt.equals("DESCARTAR")) {
                        final String motivo = txt_motivo.getText().toString();
                        if (motivo.equals("")) {
                            Toast.makeText(getApplicationContext(), "Debe especificar un motivo por el que " +
                                    "se descarta el equipo.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Descarte_equipo.this);
                            dialogo1.setTitle("DESCARTE DE EQUIPOS");
                            dialogo1.setMessage("¿Esta seguro " +
                                    "de descartar la calibración del equipo con el código: " + idecombpr + "? Tenga en " +
                                    "consideración que una vez realizada esta operación no se podrá volver a activar la " +
                                    "calibración de este equipo.");
                            dialogo1.setCancelable(false);
                            dialogo1.setPositiveButton("Descartar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String[] args = new String[]{idecombpr};
                                    Cursor c = db.rawQuery(" SELECT CodBpr FROM BalxPro where IdeComBpr=?", args);
                                    if (c.moveToFirst()) {
                                        do {
                                            codigobpr = c.getString(0);
                                        } while (c.moveToNext());
                                    }
                                    c.close();
                                    String Str = "Update Balxpro set est_esc='DS',EstBpr='D',ObsVBpr='" + motivo + "' where codBpr=" + codigobpr + "";
                                    db.execSQL(Str);
                                    Intent equipo = new Intent(Descarte_equipo.this, Selec_Equipo.class);
                                    equipo.putExtra("proyecto", proyecto);
                                    startActivity(equipo);
                                    finish();
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
                    }else{
                        Intent certificado = new Intent(Descarte_equipo.this,Certificados.class);
                        certificado.putExtra("proyecto",proyecto);
                        certificado.putExtra("idecombpr",idecombpr);
                        startActivity(certificado);
                    }
                }
            });

            sw_descarte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //Toast.makeText(getApplicationContext(),"Switch" + buttonView.getId(), Toast.LENGTH_SHORT).show();
                    if (sw_descarte.isChecked()) {
                        txt_motivo.setVisibility(View.VISIBLE);
                        bt_procede.setText("DESCARTAR");
                    }else{
                        txt_motivo.setVisibility(View.INVISIBLE);
                        bt_procede.setText("CONTINUAR");
                    }
                }
            });




    }
}