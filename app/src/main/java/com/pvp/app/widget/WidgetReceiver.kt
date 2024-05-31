package com.pvp.app.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class WidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = Widget()

//    override fun onReceive(context: Context, intent: Intent) {
//        Log.e("Widget", "Received update broadcast")
//
//        // Trigger a widget update
//        //Widget().updateAll(context)
//    }
}
