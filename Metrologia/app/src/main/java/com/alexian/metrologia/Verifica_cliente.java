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

/**
 * Created by Ing.Iván on 2017-05-02.
 */

public class Verifica_cliente extends AppCompatActivity {

    private BDManager metrologia;
    private SQLiteDatabase db;
    private String cliente;
    private EditText tnombre,truc,tciudad,tdirreccion,ttelefono,tlugar,tsolicita;
    private clientes obj;
    public static Activity cl_verifica_cliente;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verifica_cliente);//{

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            cl_verifica_cliente=this;

            final String proyecto = getIntent().getExtras().getString("elegido");
            metrologia=new BDManager(this,"SistMetPrec.db",null,1);
            db = metrologia.getWritableDatabase();
            //Consulta del código de Cliente de acuerdo al proyecto.
            String[] args = new String[] {proyecto};
            Cursor c = db.rawQuery(" SELECT CodCli FROM proyectos where IdePro=?", args);
            if (c.moveToFirst()) {
                //Toast.makeText(getApplicationContext(),"Entro",Toast.LENGTH_SHORT).show();
                do {
                    cliente=c.getString(0);
                } while(c.moveToNext());
            }
            c.close();

            //Consulta de los datos del cliente
            args = new String[] {cliente};
            Cursor c1 = db.rawQuery(" SELECT * FROM Clientes where CodCli=?", args);

            if (c1.moveToFirst()) {
                //Toast.makeText(getApplicationContext(),"Entro",Toast.LENGTH_SHORT).show();
                do {
                    obj=new clientes();
                    obj.setCodcli(c1.getInt(0));
                    obj.setNomcli(c1.getString(1));
                    obj.setCiruccli(c1.getString(2));
                    obj.setCiucli(c1.getString(3));
                    obj.setDircli(c1.getString(4));
                    obj.setEmacli(c1.getString(5));
                    obj.setTelcli(c1.getString(6));
                    obj.setConcli(c1.getString(7));
                    obj.setEstcli(c1.getString(8));
                    obj.setLugCalCli(c1.getString(9));
                } while(c1.moveToNext());
            }
            c1.close();

            tnombre=(EditText)findViewById(R.id.txtCliente);
            truc= (EditText) findViewById(R.id.txtRuc);
            tciudad=(EditText)findViewById(R.id.txtCiudad);
            tdirreccion=(EditText)findViewById(R.id.txtDireccion);
            ttelefono=(EditText)findViewById(R.id.txtTelefono);
            tsolicita=(EditText)findViewById(R.id.txtSolicitado);
            tlugar=(EditText)findViewById(R.id.txtLugarCal);

            tnombre.setText(obj.getNomcli());
            tnombre.setEnabled(false);
            truc.setText(obj.getCiruccli());
            truc.setEnabled(false);
            tciudad.setText(obj.getCiucli());
            tciudad.setEnabled(false);
            tdirreccion.setText(obj.getDircli());
            tdirreccion.setEnabled(false);
            ttelefono.setText(obj.getTelcli());
            ttelefono.setEnabled(false);
            tsolicita.setText(obj.getConcli());
            tsolicita.setEnabled(false);
            tlugar.setText(obj.getDircli());
            tlugar.requestFocus();

            Button btnactualiza = (Button) findViewById(R.id.btnActualizar);
            btnactualiza.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                //Actualiza el llugar de calibración si se ingresa
                    String lugar= tlugar.getText().toString();
                    if (lugar.equals("")) {
                        String Str ="update Clientes set LugCalCli = '"+ lugar + "' where codcli = " + obj.getCodcli() + "";
                        db.execSQL(Str);
                    }
                //pasamos a la siguiente activity
                    //String elegido = sp.getItemAtPosition(sp.getSelectedItemPosition()).toString();
                    Intent equipo = new Intent(Verifica_cliente.this,Selec_Equipo.class);
                    equipo.putExtra("proyecto",proyecto);
                    startActivity(equipo);
                }
            });

            db.close();
        //}
    }
}
