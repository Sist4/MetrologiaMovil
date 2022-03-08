package com.alexian.metrologia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;

/**
 * Created by Ing.Iván on 2017-08-27.
 */

public class visuales extends AppCompatActivity {

    private BDManager metrologia;
    private SQLiteDatabase db;
    private Switch visual1,visual2,visual3;
    private EditText obs;
    private Button btGuardavisual;
    private RelativeLayout rl;
    public static Activity cl_visuales;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visual);
        {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            cl_visuales=this;

            final String idecombpr = getIntent().getExtras().getString("idecombpr");
            final String clase = getIntent().getExtras().getString("clase");

            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();

            visual1=(Switch)findViewById(R.id.swVisual1);
            visual2=(Switch)findViewById(R.id.swVisual2);
            visual3=(Switch)findViewById(R.id.swVisual1);
            obs=(EditText)findViewById(R.id.txtObs);
            btGuardavisual=(Button)findViewById(R.id.btGuardaVisuales);
            rl=(RelativeLayout)findViewById(R.id.lyVisual);

            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(obs.getWindowToken(), 0);
                }
            });

            btGuardavisual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String vs1="";
                    String vs2="";
                    String vs3="";
                    String vobs;
                    String Str;
                    if (visual1.isChecked()){
                        vs1="si";
                    }else{
                        vs1="no";
                    }
                    if (visual2.isChecked()){
                        vs2="si";
                    }else{
                        vs2="no";
                    }
                    if (visual3.isChecked()){
                        vs3="si";
                    }else{
                        vs3="no";
                    }
                    vobs=obs.getText().toString();
                    if (vobs.equals("")){
                        Str="Update Balxpro set BalLimpBpr='" + vs1 + "', AjuBpr='" + vs2 + "', IRVBpr='" + vs3 + "' " +
                                "where IdeComBpr='" + idecombpr + "'";
                                //"where codBpr=" + codigobpr + "";
                    }else{
                        Str="Update Balxpro set BalLimpBpr='" + vs1 + "', AjuBpr='" + vs2 + "', IRVBpr='" + vs3 + "' " +
                                ",ObsVBpr='" + vobs + "' " +
                                "where IdeComBpr='" + idecombpr + "'";
                                //"where codBpr=" + codigobpr + "";
                    }
                    db.execSQL(Str);
                    if (clase.equals("Camionera")){
                        Intent excec_cam = new Intent(visuales.this,Excentricidad_cam.class);
                        excec_cam.putExtra("idecombpr",idecombpr);
                        excec_cam.putExtra("carga_escogida","");
                        excec_cam.putExtra("unidad","");
                        startActivity(excec_cam);
                    }else{
                        Intent excec_ii_iii = new Intent(visuales.this,Excentricidad_ii_iii.class);
                        excec_ii_iii.putExtra("idecombpr",idecombpr);
                        excec_ii_iii.putExtra("carga_escogida","");
                        excec_ii_iii.putExtra("unidad","");
                        startActivity(excec_ii_iii);
                    }
                }
            });

        }
    }
}
