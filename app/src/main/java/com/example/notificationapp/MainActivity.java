package com.example.notificationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

//    inisiasi class notification manager yang akan digunakan untuk menampilkan notif
    private NotificationManager notificationManager;
    private final static String CHANNEL_ID ="primary-channel";
    private int NOTIFICATION_ID = 0;
    private final static String UPDATE_NOTIFICATION = "action-update-notification";
    private NotificationReceiver receiver = new NotificationReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        membuat notif manager (menampilkan, menghapus, mengupdate)
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

//        registrasi channel ke system
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            high = notif berupa popup
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    "app_notif", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        findViewById(R.id.notif_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        findViewById(R.id.update_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNotification();
            }
        });

        findViewById(R.id.cancel_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationManager.cancel(NOTIFICATION_ID);
            }
        });

        registerReceiver(receiver, new IntentFilter(UPDATE_NOTIFICATION));
    }

    private void updateNotification(){
//        menampilkan gambar
        Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.custom);

//        mengeset isi notifikasi
        NotificationCompat.Builder notifyBuilder = getNotifBuilder();
        notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(androidImage)
                .setBigContentTitle("Notification Update"));

//        untuk menampilkan notifikasi
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

//    untuk mengeset konten, dll
    private NotificationCompat.Builder getNotifBuilder(){
//        mengarahkan ke activity 2 ketika notif ditekan
        Intent notifIntent = new Intent(this, MainActivity2.class);
        PendingIntent notifPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID, notifIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.
                Builder(this, CHANNEL_ID).
                setContentTitle("New Notification")
                .setContentText("This is my notification")
                .setSmallIcon(R.drawable.ic_android)
                .setContentIntent(notifPendingIntent);
        return notifyBuilder;
    }

    private void sendNotification(){
        Intent updateIntent = new Intent(UPDATE_NOTIFICATION);

//        intent untuk respon ketika notif ditekan(?)
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                updateIntent, PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder notifyBuilder = getNotifBuilder();
        notifyBuilder.addAction(R.drawable.ic_android, "Update Notification", updatePendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

//    unregister lifecycle dari notif (broadcaster tidak aktif lagi) (
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //    register sebuah receiver dengan sebuah context
    public class NotificationReceiver extends BroadcastReceiver{

        public NotificationReceiver(){ }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UPDATE_NOTIFICATION)){
                updateNotification();
            }
        }
    }
}
//  function NotificationCompat.Builder untuk mendefinisikan sebuah instance notifikasi dan menampilkannya dengan memanggil function ‘notify‘.