package com.ufotectandroididentificadorllamadas;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Llamada {
    private static Llamada instance;
    private RequestQueue mRequestQueue;
    public String agente;
    public String asunto;
    public  String empresa;
    public Date start;
    public Date end;
    private Context ctx;
    private String channel_id="UFOTECH";
    SharedPreferences preferences;

    private Llamada(Context ctx){
        this.ctx = ctx;
        preferences = ctx.getSharedPreferences("UFOCALL", Context.MODE_PRIVATE);
        mRequestQueue = Volley.newRequestQueue(ctx);
    }

    public void setAgente(String agente){
        this.agente = agente;
    }
    public void setAsunto(String asunto){
        this.asunto = asunto;
    }

    public void setStart(Date start){
        this.start = start;
        notificar("Llamada en curso "+this.empresa,"",true);
    }

    public void setCtx(Context ctx){
        this.ctx = ctx;
    }

    public void agenteDetectado(String agente,String empresa,String asunto){
        this.agente =   agente;
        this.asunto =   asunto;
        this.empresa = empresa;

        notificar(this.empresa,this.asunto,false);
    }

    public void setEnd(Date end){
        this.end = end;
        long duracion = end.getTime() - start.getTime();
        Log.d("UFO: ","Duracion Seg "+(duracion/1000));
        Log.d("UFO: ","Agente  "+this.agente);
        Log.d("UFO: ","Asunto  "+this.asunto);
        
        cerrarNotificacion();
        setAgente("");
        setAsunto("");
    }

    public int getNotificacionId(){
        return  Integer.parseInt(this.agente.substring(this.agente.length()-4,this.agente.length()));
    }

    public void cerrarNotificacion(){
        try{
            NotificationManager notificationManager = (NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(getNotificacionId());
        }catch (Exception e){

        }
    }

    private void notificar(String title,String subject,Boolean progress){
        Log.d("UFO:","Notify call");
        Intent fullScreenIntent = new Intent(Intent.ACTION_MAIN);
        fullScreenIntent.setComponent(new ComponentName(this.ctx.getPackageName(), this.ctx.getPackageName()+".MainActivity"));
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this.ctx, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.ctx, channel_id)
                .setSmallIcon(R.drawable.icon)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentTitle(title)
                .setContentText(subject)
                .setVibrate(new long[]{0, 500, 1000})
                .setStyle(new NotificationCompat.BigTextStyle().bigText(subject).setBigContentTitle("Motivo de la llamada"))
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(false)
                .setProgress(0,0,progress)
                .setFullScreenIntent(fullScreenPendingIntent, true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.ctx);
        notificationManager.notify(getNotificacionId(), builder.build());
    }

    public void notificarLlamada(){
        notificar("Consultando número "+this.agente,"",true);
    }

    public static Llamada getInstance(Context ctx) {
        if (instance == null) {
            instance = new Llamada(ctx);
        }
        return instance;
    }
}
