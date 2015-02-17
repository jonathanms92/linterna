package org.example.linterna;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity implements OnClickListener {

    private static final String WAKE_LOCK_TAG = "Linterna";
    private static final int NOTIFICAION_ID = 1;
    private Linterna linterna;
    private WakeLock wakeLock;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleButton button =(ToggleButton) findViewById(R.id.button_on_off);
        button.setOnClickListener(this);
        if (!initLinterna())
        {
            return;
        }

        //Encender el flash

     //  linterna.on();

        //adquirir el wake lock

        PowerManager powermanager = (PowerManager)getSystemService(POWER_SERVICE);

        wakeLock = powermanager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);

        wakeLock.setReferenceCounted(false);//no es necesario
        if (!wakeLock.isHeld())
        {
            wakeLock.acquire();
        }

        //iniciar el NotificationManager
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        //Creamos la notificación

        createNotification();

    }


    private boolean initLinterna()
    {
        try
        {
            // Acceder a la c�mara.
            linterna = new Linterna();
        }
        catch (Exception e)
        {
            // Mostrar mensaje de error al usuario.
            Toast.makeText(this,
                    getResources().getString(R.string.text_error),
                    Toast.LENGTH_LONG).show();
            // Salir de la aplicaci�n.
            finish();

            return false;
        }

        return true;
    }

    private void createNotification()
    {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.notification_text))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(NOTIFICAION_ID,notification);



    }
    private void destroyNotification()
    {
        notificationManager.cancel(NOTIFICAION_ID);

    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //destruir notificacion
        destroyNotification();

        //apagar flash

       linterna.release();
        linterna=null;

        //soltar el wake lock
        wakeLock.release();
    }

    @Override
    public void onClick(View v) {
        if (linterna.isOn())
        {
            linterna.off();
            destroyNotification();

        }
        else
        {
            linterna.on();
            createNotification();

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (linterna == null && initLinterna())
        {
            wakeLock.acquire();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (linterna != null && !linterna.isOn())
        {
            linterna.release();
            linterna = null;
            wakeLock.release();
        }
    }
}
