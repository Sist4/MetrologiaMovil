package com.alexian.metrologia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ing.Iván on 2017-04-30.
 */

public class Selec_Proyecto extends AppCompatActivity {
    private Spinner sp;
    private Cursor pry;
    private BDManager metrologia;
    private SQLiteDatabase db;
    private String linea;
    private List<String> listado = new ArrayList<String>();
    private ProgressDialog progreso;
    private String resulta;
    private String metrologo,rec_servidor,rec_usuario,rec_password;
    private ImageButton ibt,ibsqlite;
    private String direcc,equipo;
    private TextView txtInfo;
    public static Activity cl_selec_proyecto;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cl_selec_proyecto=this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.seleccion_proyecto);
        metrologo = getIntent().getExtras().getString("codmetro");
        rec_servidor=getIntent().getExtras().getString("servidor");
        rec_usuario=getIntent().getExtras().getString("usuario");
        rec_password=getIntent().getExtras().getString("password");

        metrologia=new BDManager(this,"SistMetPrec.db",null,1);
        db = metrologia.getWritableDatabase();

        sp=(Spinner)findViewById(R.id.spnProyecto);
        final Button btnseleccionar = (Button)findViewById(R.id.btnSeleccion);

        sp.setEnabled(false);
        btnseleccionar.setEnabled(false);
        final Button btadic=(Button)findViewById(R.id.btAdiciona_equ);
        btadic.setEnabled(false);

        final SimpleDateFormat dateFormat_i = new SimpleDateFormat("yyyy-MM-dd-HH-mm");


        ibt=(ImageButton)findViewById(R.id.ibtRecarga);
        ibt.setEnabled(false);
        ibsqlite=(ImageButton)findViewById(R.id.imsqlite);
        txtInfo = (TextView)findViewById(R.id.txtInfo);

        progreso=new ProgressDialog(this);
        progreso.setMessage("Conectando Con el Servidor FTP");
        progreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progreso.setProgress(0);
        progreso.show();


        //Verifica o crea carpetas SisMetPrec y Salida
        File folder=new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec");
        if (folder.exists()){
        }else{
            folder.mkdirs();
            File folder_sal=new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Salida");
            folder_sal.mkdirs();
        }

        //Escribimos el archivo SaliInfo
        escribe();

        // Thread para conexión con servidor FTP y lectura del archivo NewInfo.txt
        new Thread(new Runnable() {
            @Override
            public void run() {
                //String host="ftp.260mb.net";
                final String host=rec_servidor;
                //String username="n260m_22369291";
                final String username=rec_usuario;
                //String password="Sistemas";
                final String password=rec_password;
                MyFTPClient ftpClient = new MyFTPClient();
                progreso.setProgress(20);
                try {
                    boolean conecto=false;
                    conecto=ftpClient.ftpConnect(host, username, password, 21);
                    if (conecto==false){
                        progreso.cancel();
                        resulta="¡ALERTA! No se ha podido conectar con el Servidor FTP. Información" +
                                " NO ACTUALIZADA.";
                        //Toast.makeText(Selec_Proyecto.this,host + ',' + username + ',' + password ,Toast.LENGTH_LONG).show();
                    }else {
                        resulta = "Información Actualizada";
                    }
                    //Descarga del archivo de información NewInfo.txt
                    String remoteFile1 = "/htdocs/Metrologia/NewInfo.txt";
                    File downloadFile1 = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/NewInfo.txt");
                    OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
                    boolean success = ftpClient.ftpDownload(remoteFile1,outputStream1);
                    outputStream1.close();
                    if (success) {
                        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(downloadFile1)));
                        while ((linea=fin.readLine())!=null){
                            listado.add(linea.split(";")[0]);
                        }
                        progreso.setProgress(50);
                    }

                    //Carga del archivo SalInfo_**.txt con la nueva información para la Base de Datos
                    equipo= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                    String destino = "SalInfo_" +  equipo + ".txt";
                    File dir_arch = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Salida/SalInfo_" + equipo + ".txt");
                    direcc=dir_arch.toString();
                    ftpClient.ftpUpload(direcc,destino,null,null);
                    progreso.setProgress(75);

                } catch (IOException ex) {
                    System.out.println("Error: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    ftpClient.ftpDisconnect();
                    //Toast.makeText(Selec_Proyecto.this,"LLaga aqui"  ,Toast.LENGTH_LONG).show();
                    progreso.setProgress(85);
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnseleccionar.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnseleccionar.setEnabled(true);

                                    }
                                });
                                btadic.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        btadic.setEnabled(true);

                                    }
                                });
                                sp.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        sp.setEnabled(true);
                                    }
                                });
                                ibt.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ibt.setEnabled(true);
                                    }
                                });
                                //
                                guarda();
                                progreso.setProgress(95);
                                progreso.cancel();
                                Toast.makeText(Selec_Proyecto.this,resulta  ,Toast.LENGTH_LONG).show();
                                //Toast.makeText(Selec_Proyecto.this,host + ',' + username + ',' + password ,Toast.LENGTH_LONG).show();
                                llena();
                            }
                        }, 5000);
                    }
                });
            }
        }).start();
        // Fin Thread para conexión con servidor FTP y lectura del archivo NewInfo.txt


        btnseleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String elegido = sp.getItemAtPosition(sp.getSelectedItemPosition()).toString();
                //Toast.makeText(Selec_Proyecto.this,elegido ,Toast.LENGTH_LONG).show();
                Intent verifcliente = new Intent(Selec_Proyecto.this,Verifica_cliente.class);
                verifcliente.putExtra("elegido",elegido);
                startActivity(verifcliente);

            }
        });

        ibt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selproyecto = new Intent(Selec_Proyecto.this,Selec_Proyecto.class);
                selproyecto.putExtra("codmetro",metrologo);
                selproyecto.putExtra("servidor",rec_servidor);
                selproyecto.putExtra("usuario",rec_usuario);
                selproyecto.putExtra("password",rec_password);
                startActivity(selproyecto);

            }
        });

        ibsqlite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backupdDatabase();

            }
        });

        Button bta=(Button)findViewById(R.id.btauxilia);
        bta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent eval = new Intent(Selec_Proyecto.this,Ambientales_fin.class);
                eval.putExtra("idecombpr","170901V");
                startActivity(eval);
            }
        });


        btadic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogo1=new AlertDialog.Builder(Selec_Proyecto.this);
                dialogo1.setTitle("ADICIÓN DE EQUIPOS");
                dialogo1.setMessage("La adición de equipos está disponible " +
                        "para los últimos 5 proyectos asignados a su código de Metrólogo. " +
                        "Tenga en cuenta que si adiciona un nuevo equipo a un proyecto cerrado, " +
                        "este volverá a estar activo mientras no se realice la calibración " +
                        "del(los) equipo(s) adicionado(s).");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Ir a adición de equipos", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent adic = new Intent(Selec_Proyecto.this,adiciona_equipo.class);
                        adic.putExtra("codmetro",metrologo);
                        startActivity(adic);
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
        });

    }
    private void guarda(){
        String datos[] = listado.toArray(new String[listado.size()]);
        int cont;
        for (String c:datos){
            cont=0;
            String Str=c+";";
            //Toast.makeText(Selec_Proyecto.this,  Str , Toast.LENGTH_LONG).show();
            String inic = Str.substring(0,2);
            if (inic.equals("In")){
                String cadena = Str;
                String subcad = cadena.substring(23);
                int ind = subcad.indexOf(" ");
                String tabla = subcad.substring(0,ind);
               if (tabla.equals("Balxpro")) {
                   //Toast.makeText(Selec_Proyecto.this,  Str , Toast.LENGTH_LONG).show();
                    int ind2 = subcad.indexOf("(", 40);
                    String subcad2 = subcad.substring(ind2);
                    int ind3 = subcad2.indexOf(",");
                    int ind4 = subcad2.indexOf("(");
                    ///
                    int lrg = subcad.length();
                    int iniide=lrg-26;
                    //int finide=lrg-13;
                   int finide=lrg-19;
                    String subcad_a=subcad.substring(iniide,finide);
                    //Toast.makeText(Selec_Proyecto.this, subcad_a , Toast.LENGTH_LONG).show();
                    //Toast.makeText(Selec_Proyecto.this, "Ejecutó: " + subcad_a , Toast.LENGTH_LONG).show();
                    ///
                    String cdbpr = subcad2.substring(ind4 +1,ind3);
                    String idecombpr = subcad_a;

                    Cursor cu = db.rawQuery(" SELECT * FROM " + tabla + " where IdeCombpr='" + idecombpr + "'", null);
                    while(cu.moveToNext()){
                        cont=1;
                    }
                    if (cont == 0){
                        try{
                            //Toast.makeText(Selec_Proyecto.this, Str , Toast.LENGTH_LONG).show();
                            db.execSQL(Str);
                        } catch (Exception e) {
                            //Log.i("Backup", e.toString());
                            //Toast.makeText(Selec_Proyecto.this, Str , Toast.LENGTH_LONG).show();
                        }
                    }
                }else {
                   try{
                       //Toast.makeText(Selec_Proyecto.this, Str , Toast.LENGTH_LONG).show();
                       db.execSQL(Str);
                   } catch (Exception e) {
                       //Log.i("Backup", e.toString());
                       //Toast.makeText(Selec_Proyecto.this, Str , Toast.LENGTH_LONG).show();
                   }
                }
            }else{
                try{
                    //Toast.makeText(Selec_Proyecto.this, Str , Toast.LENGTH_LONG).show();
                    db.execSQL(Str);
                } catch (Exception e) {
                    //Log.i("Backup", e.toString());
                    //Toast.makeText(Selec_Proyecto.this, Str , Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void escribe(){
        try {
            String equipo= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            File salida = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "/SisMetPrec/Salida/SalInfo_" + equipo + ".txt");
            OutputStreamWriter fout=new OutputStreamWriter(new FileOutputStream(salida));
            //Inserción de registros nuevos (equipos adicionales)
            String[] args = new String[]{"1"};
            String  num_bpr,des_bpr,mar_bpr,mod_bpr,capmax_bpr,capuso_bpr,divesc_bpr,unidivesc_bpr,divesc_d_bpr,ran_bpr,
                    cod_pro,cod_met,ide_bpr,est_bpr,lit_bpr,idecom_bpr,divesccal_bpr,capcal_bpr;
            Cursor c = db.rawQuery("Select numbpr,desbpr,marbpr,modbpr,capmaxbpr,capusobpr,divescbpr,unidivescbpr,divesc_dbpr,ranBpr, " +
                    "codpro,codmet,idebpr,estbpr,litbpr,ideComBpr,DivEscCalBpr,CapCalBpr " +
                    "from Balxpro " +
                    "where es_adi=?", args);
            if (c.moveToFirst()) {
                do {
                    num_bpr=c.getString(0);des_bpr=c.getString(1);mar_bpr=c.getString(2);
                    mod_bpr=c.getString(3);capmax_bpr=c.getString(4);capuso_bpr=c.getString(5);
                    divesc_bpr=c.getString(6);unidivesc_bpr=c.getString(7);divesc_d_bpr=c.getString(8);
                    ran_bpr=c.getString(9); cod_pro=c.getString(10);cod_met=c.getString(11);
                    ide_bpr=c.getString(12);
                    est_bpr=c.getString(13);lit_bpr=c.getString(14);idecom_bpr=c.getString(15);
                    divesccal_bpr=c.getString(16);capcal_bpr=c.getString(17);

                    fout.write("IF NOT EXISTS (SELECT 1 FROM balxpro WHERE idecombpr='" + idecom_bpr + "') BEGIN " +
                            "Insert into balxpro (numbpr,desbpr,IdentBpr,marbpr,modbpr,SerBpr," +
                            "capmaxbpr,UbiBpr,capusobpr,divescbpr,unidivescbpr,divesc_dbpr,unidivesc_dbpr," +
                            "RanBpr,ClaBpr,codpro,codmet,idebpr,estbpr,litbpr,idecombpr,divesccalbpr,capcalbpr,lugcalBpr " +
                            ") values " +
                            "(" + num_bpr + ",'" + des_bpr + "','n/a','" + mar_bpr + "','" + mod_bpr + "'," +
                            "'n/a'," + capmax_bpr + ",'n/a'," + capuso_bpr + "," + divesc_bpr + ",'" + unidivesc_bpr + "'," +
                            "" + divesc_d_bpr + ",'" + unidivesc_bpr + "'," + ran_bpr + ",'n/a'," + cod_pro + "," + cod_met + "," +
                            "'" + ide_bpr + "','" + est_bpr + "','" + lit_bpr + "','" + idecom_bpr + "','" + divesccal_bpr +
                            "','" + capcal_bpr + "','n/a') END;" + "\r\n");

                    //Toast.makeText(getApplicationContext(),idecom_bpr,Toast.LENGTH_SHORT).show();
                } while (c.moveToNext());
            }
            c.close();
            //Escritura de la tabla Balxpro y de las tablas dependientes de cada código disponible.
            args = new String[]{"I","D","P","NP","DS"};
            String cod, des,ident,mar,mod,ser,capmax,ubi,capuso,divesc,unidivesc,divesc_d,unidivesc_d,ran,
                    cla,n1,n2,n2a,n5,n10,n20,n20a,n50,n100,n200,n200a,n500,n1000,n2000,n2000a,n5000,n10000,
                    n20000,n500000,n1000000,est,recporcli,divesccal,capcal,est_esc,proy,fech,vis1,vis2,vis3,obsvis,idecombpr,
                    fecha_prx,lugcal;
            c = db.rawQuery("Select DesBpr,IdentBpr,MarBpr,ModBpr,SerBpr,CapMaxBpr,UbiBpr,CapUsoBpr," +
                    "DivEscBpr,UniDivEscBpr,DivEsc_dBpr,UniDivEsc_dBpr,RanBpr,ClaBpr,N1,N2,N2A,N5,N10," +
                    "N20,N20A,N50,N100,N200,N200A,N500,N1000,N2000,N2000A,N5000,N10000,N20000,N500000,N1000000," +
                    "EstBpr,RecPorCliBpr,DivEscCalBpr,CapCalBpr,est_esc,CodBpr,CodPro,fec_cal,BalLimpBpr," +
                    "AjuBpr,IRVBpr,ObsVBpr,IdeComBpr,fec_proxBpr,lugcalBpr " +
                    "from Balxpro " +
                    "where (EstBpr=? or EstBpr=?) and (est_esc=? or est_esc=? or est_esc=?)", args);
            if (c.moveToFirst()) {
                do {
                    des=c.getString(0);ident=c.getString(1);mar=c.getString(2);mod=c.getString(3);
                    ser=c.getString(4);capmax=c.getString(5);ubi=c.getString(6);capuso=c.getString(7);
                    divesc=c.getString(8);unidivesc=c.getString(9);divesc_d=c.getString(10);
                    unidivesc_d=c.getString(11);ran=c.getString(12);cla=c.getString(13);n1=c.getString(14);
                    n2=c.getString(15);n2a=c.getString(16);n5=c.getString(17);n10=c.getString(18);
                    n20=c.getString(19);n20a=c.getString(20);n50=c.getString(21);n100=c.getString(22);
                    n200=c.getString(23);n200a=c.getString(24);n500=c.getString(25);n1000=c.getString(26);
                    n2000=c.getString(27);n2000a=c.getString(28);n5000=c.getString(29);n10000=c.getString(30);
                    n20000=c.getString(31);n500000=c.getString(32);n1000000=c.getString(33);est=c.getString(34);recporcli=c.getString(35);
                    divesccal=c.getString(36);capcal=c.getString(37);est_esc=c.getString(38);
                    cod=c.getString(39);proy=c.getString(40);fech=c.getString(41);vis1 = c.getString(42);
                    vis2=c.getString(43);vis3=c.getString(44);obsvis=c.getString(45);idecombpr=c.getString(46);
                    fecha_prx=c.getString(47);lugcal=c.getString(48);

                    //fout.write("Update Balxpro set CodBpr=" +cod + ",DesBpr='" + des + "',IdentBpr='"+ ident +"',MarBpr='" + mar + "'," +
                    //fout.write("If (select est_esc from Balxpro where IdeComBpr = '" + idecombpr + "')<> 'CR' " + "\r\n");
                    fout.write("If (((select est_esc from Balxpro where IdeComBpr = '" + idecombpr + "')= 'RV') or ((select est_esc from Balxpro where IdeComBpr = '" + idecombpr + "') is null)) " + "\r\n");
                    fout.write(" Begin " + "\r\n");
                    fout.write("Update Balxpro set DesBpr='" + des + "',IdentBpr='"+ ident +"',MarBpr='" + mar + "'," +
                            "ModBpr='" + mod + "',SerBpr='" + ser + "',CapMaxBpr=" + capmax  + ",UbiBpr='" + ubi + "'," +
                            "CapUsoBpr=" + capuso + ",DivEscBpr=" + divesc +",UniDivEscBpr='" + unidivesc + "'," +
                            "DivEsc_dBpr=" + divesc_d + ",UniDivEsc_dBpr='" + unidivesc_d + "',RanBpr=" + ran + "," +
                            "ClaBpr='" + cla + "',N1=" + n1 + ",N2=" + n2 + ",N2A=" + n2a +",N5=" + n5 + "," +
                            "N10=" + n10 + ",N20=" + n20 + ",N20A=" + n20a + ",N50=" + n50 + ",N100=" + n100 +"," +
                            "N200=" + n200 + ",N200A=" + n200a + ",N500=" + n500 + ",N1000=" + n1000 + "," +
                            "N2000=" + n2000 + ",N2000A=" + n2000a + ",N5000=" + n5000 + ",N10000=" + n10000 + "," +
                            "N20000=" + n20000 + ",N500000=" + n500000  + ",N1000000=" + n1000000 + ",EstBpr='" + est + "'," +
                            "RecPorCliBpr='" + recporcli + "',DivEscCalBpr='" + divesccal + "'," +
                            "CapCalBpr='" + capcal + "',est_esc='" + est_esc  + "',fec_cal='" + fech + "'," +
                            "BalLimpBpr='" + vis1 + "',AjuBpr='" + vis2 + "',IRVBpr='" + vis3 +
                            "',ObsVBpr='" + obsvis + "',fec_proxBpr='" + fecha_prx  + "',lugcalBpr='" + lugcal + "' " +
                            "where IdeComBpr='" + idecombpr + "';" + "\r\n");

                    //Escritura de la tabla Ambientales
                    Integer cuenta_cab3=1;
                    String[] args2 = new String[]{idecombpr};
                    String  CodAmb,TemIni,TemFin,HumRelIni,HumRelFin,idecombpr_a;
                    Cursor c1 = db.rawQuery("Select * " +
                            "from Ambientales " +
                            "where IdeComBpr=?", args2);
                    if (c1.moveToFirst()) {
                        do {
                            CodAmb=c1.getString(0);TemIni=c1.getString(1);TemFin=c1.getString(2);
                            HumRelIni=c1.getString(3);HumRelFin=c1.getString(4);idecombpr_a=c1.getString(5);

                            //CodAmb=String.valueOf(Integer.valueOf(CodAmb)+500000);
                            if (cuenta_cab3 <= 1) {
                                fout.write("Delete from Ambientales where IdeComBpr='" + idecombpr_a + "';" + "\r\n");
                            }

                            fout.write("Insert Into Ambientales values(" + CodAmb + "," + TemIni + "," + TemFin + "," +
                                    "" + HumRelIni + "," + HumRelFin + ",'" + idecombpr_a  + "');" + "\r\n");
                            cuenta_cab3 = cuenta_cab3 + 1;
                        } while (c1.moveToNext());
                    }
                    c1.close();

                    // Escritura de la tabla ExecCam_Cab
                    args2 = new String[]{idecombpr};
                    String CodCam_c,CarCam_c,PrbCam_c,SatCam_c,idecombpr_exc;
                    Integer cuenta_cab=1;
                    c1 = db.rawQuery("Select * " +
                            "from ExecCam_Cab " +
                            "where IdeComBpr=?", args2);
                    if (c1.moveToFirst()) {
                        do {
                            CodCam_c=c1.getString(0);CarCam_c=c1.getString(1);PrbCam_c=c1.getString(2);
                            SatCam_c=c1.getString(3);idecombpr_exc=c1.getString(4);

                            //String CodCam_c_mul=String.valueOf(Integer.valueOf(CodCam_c)+500000);
                            if (cuenta_cab <= 1) {
                                fout.write("Delete from ExecII_Cab where IdeComBpr='" + idecombpr_exc + "';" + "\r\n");
                            }

                            fout.write("Insert Into ExecCam_Cab values(" + CodCam_c + "," + CarCam_c + "," +
                                    "" + PrbCam_c + "," +
                                    "'" + SatCam_c + "','" + idecombpr_exc + "');" + "\r\n");
                            // Escritura de la tabla ExecCam_Det (dependiente de ExecCam_Cab)
                            //String[] args3 = new String[]{CodCam_c + "_"};
                            String[] args3 = new String[]{idecombpr_exc + cuenta_cab};
                            String CodCam_d,pos1,pos1r,pos2,pos2r,pos3,pos3r,ExecMax,Emp,CodCam;
                            Cursor c2 = db.rawQuery("Select * " +
                                    "from ExecCam_Det " +
                                    "where CodCam_c=?",args3);
                            if (c2.moveToFirst()){
                                do {
                                    CodCam_d=c2.getString(0);pos1=c2.getString(1);pos1r=c2.getString(2);
                                    pos2=c2.getString(3);pos2r=c2.getString(4);pos3=c2.getString(5);
                                    pos3r=c2.getString(6);ExecMax=c2.getString(7);Emp=c2.getString(8);
                                    CodCam=c2.getString(9);

                                    //CodCam_d=String.valueOf(Integer.valueOf(CodCam_d)+500000);
                                    //CodCam=String.valueOf(Integer.valueOf(CodCam)+500000);

                                    fout.write("Delete from ExecCam_Det where CodCam_c='" + idecombpr_exc + cuenta_cab + "';" + "\r\n");
                                    fout.write("Insert Into ExecCam_Det values (" + CodCam_d + "," + pos1 + "," +
                                            "" + pos1r + "," +
                                            "" + pos2 + "," + pos2r + "," + pos3 + "," + pos3r + "," + ExecMax + "," +
                                            "" + Emp + ",'" + CodCam + "');" + "\r\n");
                                }
                                while (c2.moveToNext());
                            }
                            c2.close();
                            cuenta_cab = cuenta_cab + 1;
                        } while (c1.moveToNext());
                    }
                    c1.close();

                    // Escritura de la tabla ExecII_Cab
                    args2 = new String[]{idecombpr};
                    String CodEii_c,CarEii_c,PrbEii_c,SatEii_c,idecombpr_Eii;
                    Integer cuenta_cab2=1;
                    c1 = db.rawQuery("Select * " +
                            "from ExecII_Cab " +
                            "where IdeComBpr=?", args2);
                    if (c1.moveToFirst()) {
                        do {
                            CodEii_c=c1.getString(0);CarEii_c=c1.getString(1);PrbEii_c=c1.getString(2);
                            SatEii_c=c1.getString(4);idecombpr_Eii=c1.getString(3);

                            //String CodEii_c_mul=String.valueOf(Integer.valueOf(CodEii_c)+500000);
                            if (cuenta_cab2 <= 1) {
                                fout.write("Delete from ExecII_Cab where IdeComBpr='" + idecombpr_Eii + "';" + "\r\n");
                            }

                            fout.write("Insert Into ExecII_Cab values(" + CodEii_c + "," + CarEii_c + "," +
                                    "" + PrbEii_c + "," +
                                    "'" + idecombpr_Eii + "','" + SatEii_c + "');" + "\r\n");
                            // Escritura de la tabla ExecII_Det (dependiente de ExecII_Cab)
                            String[] args3 = new String[] {idecombpr_Eii + cuenta_cab2};//CodEii_c};
                            String CodEii_d,pos1Eii,pos2Eii,pos3Eii,pos4Eii,pos5Eii,ExecMaxEii,EmpEii,CodEii;
                            Cursor c2 = db.rawQuery("Select * " +
                                    "from ExecII_Det " +
                                    "where CodEii_c=?",args3);
                            if (c2.moveToFirst()){
                                do {
                                    CodEii_d=c2.getString(0);pos1Eii=c2.getString(1);pos2Eii=c2.getString(2);
                                    pos3Eii=c2.getString(3);pos4Eii=c2.getString(4);pos5Eii=c2.getString(5);
                                    ExecMaxEii=c2.getString(6);EmpEii=c2.getString(7);CodEii=c2.getString(8);

                                    //CodEii_d=String.valueOf(Integer.valueOf(CodEii_d)+500000);
                                    //CodEii=String.valueOf(Integer.valueOf(CodEii)+500000);
                                    fout.write("Delete from ExecII_Det where CodEii_c='" + idecombpr_Eii + cuenta_cab2 + "';" + "\r\n");
                                    fout.write("Insert Into ExecII_Det values (" + CodEii_d + "," + pos1Eii + "," +
                                            "" + pos2Eii + "," +
                                            "" + pos3Eii + "," + pos4Eii + "," + pos5Eii + "," + ExecMaxEii + "," +
                                            "" + EmpEii + ",'" + CodEii + "');" + "\r\n");
                                }
                                while (c2.moveToNext());
                            }
                            c2.close();
                            cuenta_cab2 = cuenta_cab2 + 1;
                        } while (c1.moveToNext());
                    }
                    c1.close();

                    // Escritura de la tabla PCarga_Cab
                    args2 = new String[]{idecombpr};
                    String CodPca,CarPca,NumPca,idecombpr_Pca,SatPca;
                    cuenta_cab2=1;
                    c1 = db.rawQuery("Select * " +
                            "from PCarga_Cab " +
                            "where IdeComBpr=?", args2);
                    if (c1.moveToFirst()) {
                        do {
                            CodPca=c1.getString(0);CarPca=c1.getString(1);NumPca=c1.getString(2);
                            idecombpr_Pca=c1.getString(3);SatPca=c1.getString(4);

                            //String CodPca_mul=String.valueOf(Integer.valueOf(CodPca)+500000);
                            if (cuenta_cab2 <= 1) {
                                fout.write("Delete from PCarga_Cab where IdeComBpr='" + idecombpr_Pca + "';" + "\r\n");
                            }


                            fout.write("Insert Into PCarga_Cab values(" + CodPca + "," + CarPca + "," +
                                    "'" + NumPca + "','" + idecombpr_Pca + "','" + SatPca + "');" + "\r\n");
                            // Escritura de la tabla PCarga_Det (dependiente de PCarga_Cab)
                            String[] args3 = new String[]{idecombpr_Pca + NumPca};
                            String CodPca_d,LecAsc,LecDsc,ErrAsc,ErrDsc,EmpPca_d,CodPca_c,SatPca_d;
                            Cursor c2 = db.rawQuery("Select * " +
                                    "from PCarga_Det " +
                                    "where CodPca_C=?",args3);
                            if (c2.moveToFirst()){
                                do {
                                    CodPca_d=c2.getString(0);LecAsc=c2.getString(1);LecDsc=c2.getString(2);
                                    ErrAsc=c2.getString(3);ErrDsc=c2.getString(4);EmpPca_d=c2.getString(5);
                                    CodPca_c=c2.getString(6);SatPca_d=c2.getString(7);

                                    //CodPca_d=String.valueOf(Integer.valueOf(CodPca_d)+500000);
                                    //CodPca_c=String.valueOf(Integer.valueOf(CodPca_c)+500000);
                                    fout.write("Delete from PCarga_Det where CodPca_c='" + CodPca_c + "';" + "\r\n");
                                    fout.write("Insert Into PCarga_Det values (" + CodPca_d + "," + LecAsc + "," +
                                            "" + LecDsc + "," + ErrAsc + "," + ErrDsc + "," + EmpPca_d + "," +
                                            "'" + CodPca_c + "','" + SatPca_d + "');" + "\r\n");
                                }
                                while (c2.moveToNext());
                            }
                            c2.close();
                            cuenta_cab2 = cuenta_cab2 + 1;
                        } while (c1.moveToNext());
                    }
                    c1.close();

                    //Escritura de la tabla Pesxpro
                    args2 = new String[]{idecombpr};
                    cuenta_cab2=1;
                    String  codpxp,idecombpr_pxp,tippxp,noncerpxp,n1pxp,n2pxp,n2apxp,n5pxp,n10pxp,n20pxp,n20apxp,
                            n50pxp,n100pxp,n200pxp,n200apxp,n500pxp,n1000pxp,n2000pxp,n2000apxp,n5000pxp,
                            n10000pxp,n20000pxp,n500000pxp,n1000000pxp,cp1pxp,cp2pxp,cp3pxp,cp4pxp,cp5pxp,cp6pxp,cp7pxp,
                            cp8pxp,cp9pxp,cp10pxp,cp11pxp,cp12pxp,ajspxp;
                    c1 = db.rawQuery("Select * " +
                            "from Pesxpro " +
                            "where IdeComBpr=?", args2);
                    if (c1.moveToFirst()) {
                        do {

                            codpxp=c1.getString(0);idecombpr_pxp=c1.getString(1);tippxp=c1.getString(2);
                            noncerpxp=c1.getString(3);n1pxp=c1.getString(4);n2pxp=c1.getString(5);
                            n2apxp=c1.getString(6);n5pxp=c1.getString(7);n10pxp=c1.getString(8);
                            n20pxp=c1.getString(9);n20apxp=c1.getString(10);n50pxp=c1.getString(11);
                            n100pxp=c1.getString(12);n200pxp=c1.getString(13);n200apxp=c1.getString(14);
                            n500pxp=c1.getString(15);n1000pxp=c1.getString(16);n2000pxp=c1.getString(17);
                            n2000apxp=c1.getString(18);n5000pxp=c1.getString(19);n10000pxp=c1.getString(20);
                            n20000pxp=c1.getString(21);n500000pxp=c1.getString(22);n1000000pxp=c1.getString(23);cp1pxp=c1.getString(24);
                            cp2pxp=c1.getString(25);cp3pxp=c1.getString(26);cp4pxp=c1.getString(27);
                            cp5pxp=c1.getString(28);cp6pxp=c1.getString(29);cp7pxp=c1.getString(30);
                            cp8pxp=c1.getString(31);cp9pxp=c1.getString(32);cp10pxp=c1.getString(33);
                            cp11pxp=c1.getString(34);cp12pxp=c1.getString(35);ajspxp=c1.getString(36);
                            codpxp=String.valueOf(Integer.valueOf(codpxp)+500000);

                            if (cuenta_cab2 <= 1) {
                                fout.write("Delete from Pesxpro where IdeComBpr='" + idecombpr_pxp + "';" + "\r\n");
                            }

                            fout.write("Insert Into Pesxpro values(" + codpxp + ",'" + idecombpr_pxp + "','" + tippxp + "','" +
                                    "" + noncerpxp + "'," + n1pxp + "," + n2pxp  + "," + n2apxp + "," + n5pxp + "," +
                                    "" + n10pxp + "," + n20pxp + "," + n20apxp + "," + n50pxp + "," + n100pxp + "," +
                                    "" + n200pxp + "," + n200apxp + "," + n500pxp + "," + n1000pxp + "," + n2000pxp + "," +
                                    "" + n2000apxp + "," + n5000pxp + "," + n10000pxp + "," + n20000pxp + "," +
                                    "" + n500000pxp + "," +  n1000000pxp + "," + cp1pxp + "," + cp2pxp + "," + cp3pxp + "," + cp4pxp + "," +
                                    "" + cp5pxp + "," + cp6pxp + "," + cp7pxp + "," + cp8pxp + "," + cp9pxp + "," + cp10pxp + "," +
                                    "" + cp11pxp + "," + cp12pxp + "," + ajspxp + ");" + "\r\n");
                            cuenta_cab2 = cuenta_cab2 + 1;
                        } while (c1.moveToNext());
                    }
                    c1.close();

                    //Escritura de la tabla Cert_Balxpro
                    args2 = new String[]{idecombpr};
                    cuenta_cab2=1;
                    String  codcbp,nomcer,idecombpr_cbp;
                    c1 = db.rawQuery("Select * " +
                            "from Cert_Balxpro " +
                            "where IdeComBpr=?", args2);
                    if (c1.moveToFirst()) {
                        do {

                            codcbp=c1.getString(0);nomcer=c1.getString(1);idecombpr_cbp=c1.getString(2);

                            //codcbp=String.valueOf(Integer.valueOf(codcbp)+500000);
                            if (cuenta_cab2 <= 1) {
                                fout.write("Delete from Cert_Balxpro where IdeComBpr='" + idecombpr_cbp + "';" + "\r\n");
                            }

                            fout.write("Insert Into Cert_Balxpro values(" + codcbp + ",'" + nomcer + "'," +
                                    "'" + idecombpr_cbp + "');" + "\r\n");
                            cuenta_cab2 = cuenta_cab2 + 1;
                        } while (c1.moveToNext());
                    }
                    c1.close();

                    // Escritura de la tabla RepetIII_Cab
                    args2 = new String[]{idecombpr};
                    String codriii_c,carriii,difmaxriii,empriii,satriii,idecombpr_riii;
                    c1 = db.rawQuery("Select * " +
                            "from RepetIII_Cab " +
                            "where IdeComBpr=?", args2);
                    if (c1.moveToFirst()) {
                        do {
                            codriii_c=c1.getString(0);carriii=c1.getString(1);difmaxriii=c1.getString(2);
                            empriii=c1.getString(3);satriii=c1.getString(4);idecombpr_riii=c1.getString(5);

                            //String codriii_c_mul=String.valueOf(Integer.valueOf(codriii_c)+500000);

                            fout.write("Delete from RepetIII_Cab where IdeComBpr='" + idecombpr_riii + "';" + "\r\n");
                            fout.write("Insert Into RepetIII_Cab values(" + codriii_c + "," + carriii + "," +
                                    "'" + difmaxriii + "'," + empriii + ",'" + satriii + "','" + idecombpr_riii + "'" +
                                    ");" + "\r\n");
                            // Escritura de la tabla RepetIII_Det (dependiente de RepetIII_Cab)
                            String[] args3 = new String[]{idecombpr_riii};
                            String codriii_d,lec1riii,lec1_0riii,lec2riii,lec2_0riii,lec3riii,
                                    lec3_0riii,codriii_ca;
                            Cursor c2 = db.rawQuery("Select * " +
                                    "from RepetIII_Det " +
                                    "where CodRiii_C=?",args3);
                            if (c2.moveToFirst()){
                                do {
                                    codriii_d=c2.getString(0);lec1riii=c2.getString(1);lec1_0riii=c2.getString(2);
                                    lec2riii=c2.getString(3);lec2_0riii=c2.getString(4);lec3riii=c2.getString(5);
                                    lec3_0riii=c2.getString(6);codriii_ca=c2.getString(7);

                                    //codriii_d=String.valueOf(Integer.valueOf(codriii_d)+500000);
                                    //codriii_ca=String.valueOf(Integer.valueOf(codriii_ca)+500000);

                                    fout.write("Delete from RepetIII_Det where Codriii_c='" + codriii_ca + "';" + "\r\n");
                                    fout.write("Insert Into RepetIII_Det values (" + codriii_d + "," + lec1riii + "," +
                                            "" + lec1_0riii + "," + lec2riii + "," + lec2_0riii + "," + lec3riii + "," +
                                            "" + lec3_0riii + ",'" + codriii_ca + "');" + "\r\n");
                                }
                                while (c2.moveToNext());
                            }
                            c2.close();

                        } while (c1.moveToNext());
                    }
                    c1.close();


                    // Escritura de la tabla RepetII_Cab
                    args2 = new String[]{idecombpr};
                    String codrii_c,carrii,difmaxrii,emprii,satrii,idecombpr_rii;
                    c1 = db.rawQuery("Select * " +
                            "from RepetII_Cab " +
                            "where IdeComBpr=?", args2);
                    if (c1.moveToFirst()) {
                        do {
                            codrii_c=c1.getString(0);carrii=c1.getString(1);difmaxrii=c1.getString(2);
                            emprii=c1.getString(3);satrii=c1.getString(4);idecombpr_rii=c1.getString(5);

                            //String codrii_c_mul=String.valueOf(Integer.valueOf(codrii_c)+500000);

                            fout.write("Delete from RepetII_Cab where IdeComBpr='" + idecombpr_rii + "';" + "\r\n");
                            fout.write("Insert Into RepetII_Cab values(" + codrii_c + "," + carrii + "," +
                                    "'" + difmaxrii + "'," + emprii + ",'" + satrii + "','" + idecombpr_rii + "'" +
                                    ");" + "\r\n");
                            // Escritura de la tabla RepetII_Det (dependiente de RepetII_Cab)
                            String[] args3 = new String[]{idecombpr_rii};
                            String codrii_d,lec1rii,lec1_0rii,lec2rii,lec2_0rii,lec3rii,lec3_0rii,lec4rii,lec4_0rii,
                                    lec5rii,lec5_0rii,lec6rii,lec6_0rii,codrii_ca;
                            Cursor c2 = db.rawQuery("Select * " +
                                    "from RepetII_Det " +
                                    "where CodRii_C=?",args3);
                            if (c2.moveToFirst()){
                                do {
                                    codrii_d=c2.getString(0);lec1rii=c2.getString(1);lec1_0rii=c2.getString(2);
                                    lec2rii=c2.getString(3);lec2_0rii=c2.getString(4);lec3rii=c2.getString(5);
                                    lec3_0rii=c2.getString(6);lec4rii=c2.getString(7);lec4_0rii=c2.getString(8);
                                    lec5rii=c2.getString(9);lec5_0rii=c2.getString(10);lec6rii=c2.getString(11);
                                    lec6_0rii=c2.getString(12);codrii_ca=c2.getString(13);

                                    //codrii_d=String.valueOf(Integer.valueOf(codrii_d)+500000);
                                    //codrii_ca=String.valueOf(Integer.valueOf(codrii_ca)+500000);

                                    fout.write("Delete from RepetII_Det where Codrii_c='" + codrii_ca + "';" + "\r\n");
                                    fout.write("Insert Into RepetII_Det values (" + codrii_d + "," + lec1rii + "," +
                                            "" + lec1_0rii + "," + lec2rii + "," + lec2_0rii + "," + lec3rii + "," +
                                            "" + lec3_0rii + "," + lec4rii + "," + lec4_0rii + "," + lec5rii + "," +
                                            "" + lec5_0rii + "," + lec6rii + "," + lec6_0rii + ",'" + codrii_ca +
                                            "');" + "\r\n");
                                }
                                while (c2.moveToNext());
                            }
                            c2.close();

                        } while (c1.moveToNext());
                    }
                    c1.close();

                    //Escritura de la tabla Sustxpro
                    cuenta_cab2=1;
                    args2 = new String[]{idecombpr};
                    String  codsxp,idecombpr_sxp,tipsxp,crgsxp1,crgsxp2,crgsxp3,crgsxp4,crgsxp5,crgsxp6,crgsxp7,crgsxp8,
                            crgsxp9,crgsxp10,pesxpro_aso;
                    c1 = db.rawQuery("Select * " +
                            "from Sustxpro " +
                            "where IdeComBpr=?", args2);
                    if (c1.moveToFirst()) {
                        do {

                            codsxp=c1.getString(0);idecombpr_sxp=c1.getString(1);tipsxp=c1.getString(2);
                            crgsxp1=c1.getString(3);crgsxp2=c1.getString(4);crgsxp3=c1.getString(5);
                            crgsxp4=c1.getString(6);crgsxp5=c1.getString(7);crgsxp6=c1.getString(8);
                            crgsxp7=c1.getString(9);crgsxp8=c1.getString(10);crgsxp9=c1.getString(11);

                            if (cuenta_cab2 <= 1) {
                                fout.write("Delete from Sustxpro where IdeComBpr='" + idecombpr_sxp + "';" + "\r\n");
                            }
                            crgsxp10=c1.getString(12);pesxpro_aso=c1.getString(13);

                            fout.write("Insert Into Sustxpro values(" + codsxp + ",'" + idecombpr_sxp + "','" +
                                    "" + tipsxp + "'," + crgsxp1 + "," + crgsxp2 + "," + crgsxp3 + "," + crgsxp4 + "," +
                                    "" + crgsxp5 + "," + crgsxp6 + "," + crgsxp7 + "," + crgsxp8 + "," + crgsxp9 + "," +
                                    "" + crgsxp10 + "," + pesxpro_aso + ");" + "\r\n");
                            cuenta_cab2=cuenta_cab2+1;
                        } while (c1.moveToNext());
                    }
                    c1.close();


                    //Actualización de la tabla Proyectos
                    args2 = new String[]{proy};
                    String  estpry,fecprox;
                    c1 = db.rawQuery("Select EstPro,FecSigCalPro " +
                            "from Proyectos " +
                            "where CodPro=?", args2);
                    if (c1.moveToFirst()) {
                        do {

                            estpry=c1.getString(0);fecprox=c1.getString(1);

                            fout.write("Update Proyectos set EstPro='" + estpry + "',FecSigCalPro='" + fecprox + "'" +
                                    " where CodPro=" + proy + ";" + "\r\n");
                            fout.write(" End " + "\r\n");

                        } while (c1.moveToNext());
                    }
                    c1.close();

                } while (c.moveToNext());
            }
            c.close();
            fout.close();
        }catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Proceso de escritura fallido. " +
                    "Por favor intente nuevamente.", Toast.LENGTH_SHORT).show();
        }
    }

    private void llena(){
        ArrayList<proyectos> proyectos = new ArrayList<proyectos>();
        try{
            String[] args = new String[] {metrologo,"A"};
          //  Cursor c = db.rawQuery(" SELECT distinct(IdePro) FROM Proyectos where CodMet=? and EstPro=?", args);

            Cursor c = db.rawQuery(" SELECT distinct(IdePro) FROM Proyectos where CodMet=? and EstPro=? and idePro in (SELECT distinct(IDEBPR) FROM balxpro where (Estbpr='A' or EstBpr='RV'))", args);

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
        this.sp.setAdapter(adaptador);

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> spn,
                                               android.view.View v,
                                               int posicion,
                                               long id) {
                        String proye=sp.getSelectedItem().toString();
                        String total ="";
                        String realizados="";
                        String norealizados ="";
                        String descartados="";
                        String cliente="";
                        try{
                            String[] args=new String[]{proye};
                            Cursor c1=db.rawQuery("select COUNT(idecombpr) as Total from Balxpro where Idebpr = ?", args );
                            while (c1.moveToNext()){
                                total=c1.getString(0);
                            }

                            args=new String[]{proye,"I"};
                            c1=db.rawQuery("select COUNT(idecombpr) as Total from Balxpro where Idebpr = ? and EstBpr = ?", args );
                            while (c1.moveToNext()){
                                realizados=c1.getString(0);
                            }

                            args=new String[]{proye,"A"};
                            c1=db.rawQuery("select COUNT(idecombpr) as Total from Balxpro where Idebpr = ? and EstBpr = ?", args );
                            while (c1.moveToNext()){
                                norealizados=c1.getString(0);
                            }

                            args=new String[]{proye,"D"};
                            c1=db.rawQuery("select COUNT(idecombpr) as Total from Balxpro where Idebpr = ? and EstBpr = ?", args );
                            while (c1.moveToNext()){
                                descartados=c1.getString(0);
                            }

                            args=new String[]{proye};
                            c1=db.rawQuery("SELECT DISTINCT(Clientes.NomCli) AS Cliente FROM Proyectos INNER JOIN Clientes ON Proyectos.CodCli = Clientes.CodCli INNER JOIN Balxpro ON Proyectos.CodPro = Balxpro.CodPro WHERE Balxpro.IdeBpr = ?", args );
                            while (c1.moveToNext()){
                                cliente=c1.getString(0);
                            }

                        } catch (Exception e){
                            //TODO: Handle exception
                        }
                        txtInfo.setText(" Cliente: " + cliente + "\r\n" +
                                        " -> Hojas de trabajo cargadas: " + total + "\r\n" +
                                        " -> Equipos calibrados:  " + realizados + "\r\n"  +
                                        " -> Equipos pendientes:  " + norealizados + "\r\n" +
                                        " -> Equipos descartados: " + descartados );
                    }
                    public void onNothingSelected(AdapterView<?> spn) {
                    }
                });

    }

    /*@Override
    public void onBackPressed() {
        AlertDialog.Builder dialogo1=new AlertDialog.Builder(Selec_Proyecto.this);
        dialogo1.setTitle("Cerrar Metrología");
        dialogo1.setMessage("¿Salir de Metrología?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
    }*/

    public void backupdDatabase(){
        try {
            final File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            String packageName  = "com.alexian.metrologia";
            String sourceDBName = "SistMetPrec.db";
            final String targetDBName = "SistMetPrec";
            if (sd.canWrite()) {
                final Date now = new Date();
                String currentDBPath = "data/" + packageName + "/databases/" + sourceDBName;
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                final String backupDBPath = targetDBName + dateFormat.format(now) + "-" + equipo  + ".db";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.i("backup","backupDB=" + backupDB.getAbsolutePath());
                Log.i("backup","sourceDB=" + currentDB.getAbsolutePath());

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            Log.i("Backup", e.toString());
        }
    }
}
