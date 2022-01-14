package com.smash.up.Itens;
import android.support.v7.app.*;
import android.support.v4.app.NotificationManagerCompat;
import android.graphics.*;
import com.smash.up.*;
import android.app.*;
import android.content.*;

public class SendNotification
{
	NotificationCompat.Builder mNotificationBuilder;
	
	public final static int NOTIFICATION_ID=789;
	NotificationManager mNotificationManager;
	private TaskService mService;
	
	public SendNotification(TaskService mService){
		this.mService=mService;
		mNotificationManager=(NotificationManager) mService.getSystemService(mService.NOTIFICATION_SERVICE);
		mNotificationBuilder=new NotificationCompat.Builder(mService);
	}
	
	public void startNotification(){
		mNotificationBuilder.setTicker("Task Service iniciado");
		mNotificationBuilder.setColor(Color.RED)
		.setStyle(new NotificationCompat.InboxStyle()
		.addLine("Enviadas"))
		.setContentTitle("SmashUp").setContentText(
		"Notification necessária").setOngoing(true)
		.addAction(android.R.drawable.ic_menu_close_clear_cancel,"Cancelar",
		PendingIntent.getService(mService,0,new Intent(TaskService.ACTION_CANCEL),0))
		.setSmallIcon(R.drawable.smash_white);
		mService.startForeground(NOTIFICATION_ID,mNotificationBuilder.build());
	}
	public void updateSent(String s){
		mNotificationBuilder.setStyle(new NotificationCompat.InboxStyle()
		 .addLine(s));
		mService.startForeground(NOTIFICATION_ID,mNotificationBuilder.build());
		
	}
	public void updatePorcentagem(int max,int now){
		mNotificationBuilder.setProgress(max,now,false);
		mService.startForeground(NOTIFICATION_ID,mNotificationBuilder.build());
		
	}
	public void cancel(){
		mService.stopForeground(true);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
}
