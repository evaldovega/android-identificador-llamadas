package com.ufotectandroididentificadorllamadas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;


public class Servicio2 extends BroadcastReceiver {

    static boolean sonando=false;
    static String estado_anterior="";
    static String numero="";
    static Llamada llamada;

    @Override
    public void onReceive(Context context, Intent intent) {
        try{

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d("UFO: BroadcastReceiver ",state);




            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {


                numero = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                if(!numero.equals("")) {

                    llamada=Llamada.getInstance(null);

                    if(numero.equals(llamada.agente)) {
                        estado_anterior = state;
                        Log.d("UFO: BroadcastReceiver", "Sonando llamada del agente " + llamada.agente);
                    }
                }
            }

            //Contesto
            if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK) && numero.equals(llamada.agente)){
                estado_anterior=state;
                llamada.setStart(new Date());
            }

            //Finalizo
            if(state.equals(TelephonyManager.EXTRA_STATE_IDLE) &&  numero.equals(llamada.agente)){

                if(estado_anterior.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    Log.d("UFO: ","contesto la llamada del agente");
                    llamada.setEnd(new Date());
                }else if(estado_anterior.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    Log.d("UFO: ","Rechazo la llamada del agente");
                    llamada.rechazar();
                }

                if(estado_anterior!=TelephonyManager.EXTRA_STATE_IDLE){
                    estado_anterior = TelephonyManager.EXTRA_STATE_IDLE;
                }
            }



        }catch (Exception e){
            Log.e("UFO: ",e.getMessage());
        }
    }
}
