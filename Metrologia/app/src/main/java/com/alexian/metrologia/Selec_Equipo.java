package com.alexian.metrologia;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Ing.Iván on 2017-05-02.
 */

public class Selec_Equipo extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner sp, sp1;
    private Cursor pry;
    private BDManager metrologia;
    private SQLiteDatabase db;
    private String proyecto;
    private ArrayList<equipos_x_proyectos> equipos;
    private ArrayList<String> equipos2;
    private String idecombpr,marca,codigobpr;
    private String detail,equ,equ_cut;
    private Switch swDescarta;
    private EditText txtMotivo;
    private Button btProcede;
    private RelativeLayout rl;
    public static Activity cl_select_equipo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seleccion_equipo);

        cl_select_equipo=this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.sp = (Spinner) findViewById(R.id.spmarca);
        this.sp1=(Spinner) findViewById(R.id.spmodelo);

        metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
        db = metrologia.getWritableDatabase();

        //Registrando componentes
        rl=(RelativeLayout)findViewById(R.id.lySeleEquip);
        swDescarta=(Switch)findViewById(R.id.swDescarta );
        txtMotivo=(EditText)findViewById(R.id.txtMotivo);
        btProcede=(Button)findViewById(R.id.btProcede);
        txtMotivo.setVisibility(View.INVISIBLE);
        btProcede.setVisibility(View.INVISIBLE);

        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtMotivo.getWindowToken(), 0);
            }
        });

        proyecto = getIntent().getExtras().getString("proyecto");
        Globals g = (Globals) getApplication();
        String cmet = g.getCodmetrologo();

        loadSpinnerEquipos();

        Button btnseleccionequipo = (Button)findViewById(R.id.btnSeleccionEquipo);
        btnseleccionequipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String identi=sp1.getSelectedItem().toString();
                int pos = identi.indexOf("|");
                String identi_cut=identi.substring(0,pos-1);
                String[] args = new String[] {identi_cut};
                Cursor c = db.rawQuery(" SELECT CodBpr,IdeComBpr FROM BalxPro where IdeComBpr=?", args);
                if (c.moveToFirst()) {
                    do {
                        //codigobpr=c.getString(0);
                        idecombpr=c.getString(1);
                    } while(c.moveToNext());
                }
                c.close();
                Intent verifequipo = new Intent(Selec_Equipo.this,Verifica_equipo.class);
                verifequipo.putExtra("idecombpr",idecombpr);
                startActivity(verifequipo);
            }
        });

        btProcede.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String motivo = txtMotivo.getText().toString();
                if (motivo.equals("")){
                    Toast.makeText(getApplicationContext(), "Debe especificar un motivo por el que " +
                            "se descarta el equipo." , Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    String identif =sp1.getSelectedItem().toString();
                    AlertDialog.Builder dialogo1=new AlertDialog.Builder(Selec_Equipo.this);
                    dialogo1.setTitle("DESCARTE DE EQUIPOS");
                    dialogo1.setMessage("¿Esta seguro " +
                            "de descartar la calibración del equipo con el código: " + identif + "? Tenga en " +
                            "consideración que una vez realizada esta operación no se podrá volver a activar la " +
                            "calibración de este equipo.");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Descartar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String identi=sp1.getSelectedItem().toString();
                            int pos = identi.indexOf("|");
                            String identi_cut=identi.substring(0,pos-1);
                            String[] args = new String[] {identi_cut};
                            Cursor c = db.rawQuery(" SELECT CodBpr FROM BalxPro where IdeComBpr=?", args);
                            if (c.moveToFirst()) {
                                do {
                                    codigobpr=c.getString(0);
                                } while(c.moveToNext());
                            }
                            c.close();
                            String Str="Update Balxpro set est_esc='DS',EstBpr='D',ObsVBpr='" + motivo + "' where codBpr=" + codigobpr + "";
                            db.execSQL(Str);
                            Intent equipo = new Intent(Selec_Equipo.this,Selec_Equipo.class);
                            equipo.putExtra("proyecto",proyecto);
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
            }
        });

        swDescarta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(getApplicationContext(),"Switch" + buttonView.getId(), Toast.LENGTH_SHORT).show();
                if (swDescarta.isChecked()) {
                    txtMotivo.setVisibility(View.VISIBLE);
                    btProcede.setVisibility(View.VISIBLE);
                }else{
                    txtMotivo.setVisibility(View.INVISIBLE);
                    btProcede.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void loadSpinnerEquipos(){
        equipos = new ArrayList<equipos_x_proyectos>();
        try {
            metrologia = new BDManager(this, "SistMetPrec.db", null, 1);
            db = metrologia.getWritableDatabase();
            String[] args = new String[]{proyecto, "A","RV"};
            Cursor c = db.rawQuery(" SELECT distinct(marbpr) FROM balxpro where idebpr=? and (Estbpr=? or EstBpr=?)", args);

            equipos_x_proyectos obj;
            while (c.moveToNext()) {
                obj = new equipos_x_proyectos();
                obj.setMabpr(c.getString(0));
                equipos.add(obj);
            }
        } catch (Exception e) {
            //TODO: Handle exception
        }
        ArrayAdapter<equipos_x_proyectos> adaptador = new ArrayAdapter<equipos_x_proyectos>(this,
                android.R.layout.simple_list_item_1, equipos);
        this.sp.setAdapter(adaptador);

        this.sp.setOnItemSelectedListener(this);
        this.sp1.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spmarca:
                //Toast.makeText(getApplicationContext(), "entra al on" , Toast.LENGTH_SHORT).show();
                marca=sp.getSelectedItem().toString();
                equipos2=new ArrayList<String>();
                try{
                    String[] args=new String[]{proyecto,"A",marca};//ANGEL AUCANCELA
                    Cursor c1=db.rawQuery(" SELECT idecombpr,modbpr FROM balxpro where idebpr=? and Estbpr=? and marBpr=?", args );
                    equipos_x_proyectos obj;
                    while (c1.moveToNext()){
                        obj = new equipos_x_proyectos();
                        obj.setModbpr(c1.getString(0));
                        String lleva=c1.getString(0) + " |/| " + c1.getString(1);
                        equipos2.add(lleva);
                    }
                } catch (Exception e){
                    //TODO: Handle exception
                }
                sp1=(Spinner) findViewById(R.id.spmodelo);
                ArrayAdapter<String> adaptador2=new ArrayAdapter<String>(Selec_Equipo.this,
                        android.R.layout.simple_list_item_1, equipos2);
                sp1.setAdapter(adaptador2);
                break;

            case R.id.spmodelo:

                equ=sp1.getSelectedItem().toString();
                int pos = equ.indexOf("|");
                equ_cut=equ.substring(0,pos-1);
                String[] args = new String[] {equ_cut};
                Cursor c = db.rawQuery(" SELECT desbpr FROM balxpro where idecombpr=?", args);
                if (c.moveToFirst()) {
                    do {
                        detail =c.getString(0);
                    } while(c.moveToNext());
                }
                c.close();
                EditText txdetalle = (EditText)findViewById(R.id.txtdescribe_enSeleccion);
                txdetalle.setText(detail);
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


