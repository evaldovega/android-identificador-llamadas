package com.ufotectandroididentificadorllamadas;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Llamada  {
    private static Llamada instance;
    private RequestQueue mRequestQueue;
    public String agente;
    public String asunto;
    public  String empresa;
    public Date start;
    public Date end;
    private Context ctx;
    private String channel_id="UFOTECH";
    private String repetir="";
    SharedPreferences preferences;
    TextToSpeech tts;


    private Llamada(Context ctx){
        this.ctx = ctx;
        this.tts = new TextToSpeech(ctx, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        this.tts.setLanguage(Locale.getDefault());
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
        this.repetir="";
        this.tts.stop();
        notificar("Estas hablando con "+this.empresa,this.asunto,false);
    }

    public void setCtx(Context ctx){
        this.ctx = ctx;
    }

    private void hablar(){
        String parrafo="";
        for(int i=0;i<2;i++){
            parrafo=parrafo+this.repetir+".";
        }
        this.tts.speak(parrafo, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void agenteNoDetectado(){
        this.repetir = "TRULI. No ha detectado esté número";
        hablar();
    }

    public void agenteDetectado(String agente,String empresa,String asunto){
        this.agente =   agente;
        this.asunto =   asunto;
        this.empresa = empresa;
        this.repetir    =   "TRULI. Llamada entrante de "+this.empresa+". "+this.asunto;
        hablar();
        notificar("Llamada de "+this.empresa,this.asunto,false);
    }

    public void setEnd(Date end){
        this.end = end;
        long duracion = end.getTime() - start.getTime();
        Log.d("UFO: ","Duracion Seg "+(duracion/1000));
        Log.d("UFO: ","Agente  "+this.agente);
        Log.d("UFO: ","Asunto  "+this.asunto);

        String backend_call_store=preferences.getString("backend_call_store","");
        cerrarNotificacion();

        if(!backend_call_store.equals("")){

            String my_number = preferences.getString("my_number","");
            Map<String,String> params=new HashMap<String,String>();
            params.put("agente",this.agente);
            params.put("usuario",my_number);
            params.put("fecha_inicio",android.text.format.DateFormat.format("yyyy-MM-dd",this.start).toString());
            params.put("fecha_fin",android.text.format.DateFormat.format("yyyy-MM-dd",this.end).toString());

            String fecha_inicio=android.text.format.DateFormat.format("yyyy-MM-dd",this.start).toString();
            String fecha_fin=android.text.format.DateFormat.format("yyyy-MM-dd",this.end).toString();

            Log.i("UFO:","Guardar llamada");
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, backend_call_store+"?agente="+agente+"&usuario="+my_number+"&fecha_inicio="+fecha_inicio+"&fecha_fin="+fecha_fin, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.i("UFO:","Llamada guardada");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("UFO:","No se guardo la llamada "+error.toString());
                }
            }){
                @Override
                public Map<String,String> getHeaders(){
                    HashMap<String, String> headers = new HashMap<String, String>();
                    String backend_auth=preferences.getString("backend_auth","");
                    if(!backend_auth.equals("")){
                        headers.put("Authorization","Bearer "+backend_auth);
                    }
                    return headers;
                }
                @Override
                public Map<String,String> getParams(){
                    return  params;
                }
            };
            mRequestQueue.add(request);
        }


        setAgente("");
        setAsunto("");
    }

    public int getNotificacionId(){
        int id = Integer.parseInt(agente.substring(agente.length()-4));
        Log.d("UFO:","Notificacion id "+id+" Agente "+agente);
        return  id;
    }

    public void rechazar(){
        this.repetir="";
        this.cerrarNotificacion();
        this.tts.stop();
    }

    public void cerrarNotificacion(){
        try{
            NotificationManager notificationManager = (NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(getNotificacionId());
        }catch (Exception e){

        }
    }

    private void notificar(String title,String subject,Boolean progress){
        Log.d("UFO:","Notify call title "+title+" subject "+subject);
        Intent fullScreenIntent = new Intent(Intent.ACTION_MAIN);
        fullScreenIntent.setComponent(new ComponentName(this.ctx.getPackageName(), this.ctx.getPackageName()+".MainActivity"));
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this.ctx, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap largeIcon = BitmapFactory.decodeResource(this.ctx.getResources(), R.drawable.trulli);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.ctx, channel_id)
                .setSmallIcon(R.drawable.icon)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setContentTitle(title)
                .setContentText(subject)
                .setVibrate(new long[]{0, 500, 1000})
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(subject).setBigContentTitle(title))
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
