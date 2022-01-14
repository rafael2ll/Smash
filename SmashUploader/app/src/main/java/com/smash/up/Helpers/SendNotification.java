package com.smash.up.Helpers;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.support.v7.app.*;
import com.smash.up.*;
import com.smash.up.Helpers.*;

public class SendNotification
{
	
	NotificationCompat.Builder mNotificationBuilder;
	NotificationCompat.InboxStyle mInbox= new NotificationCompat.InboxStyle()
	.setSummaryText("-Enviadas-");
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
			.setStyle(mInbox)
			.setContentTitle("SmashUp").setContentText(
			"Notification necessária").setOngoing(true)
			.addAction(android.R.drawable.ic_menu_close_clear_cancel,"Cancelar",
					   PendingIntent.getService(mService,0,new Intent(TaskService.ACTION_CANCEL),0))
			.setSmallIcon(R.drawable.smash_white);
		mService.startForeground(NOTIFICATION_ID,mNotificationBuilder.build());
	}
	public void updateSent(String s){
		mInbox.addLine(s);

		mNotificationBuilder.setStyle(mInbox);
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
