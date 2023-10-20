package com.dicoding.storyapp.widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.widget.RemoteViewsService
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.di.dataStore
import kotlinx.coroutines.runBlocking

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        val userPreference = UserPreference.getInstance(this.applicationContext.dataStore)

        val appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val appWidgetManager = AppWidgetManager.getInstance(this.applicationContext)

        return if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            val token = runBlocking{userPreference.getToken()}
            StackRemoteViewsFactory(this.applicationContext, userPreference, appWidgetId, appWidgetManager, token)
        } else {
            StackRemoteViewsFactory(this.applicationContext, userPreference, AppWidgetManager.INVALID_APPWIDGET_ID, appWidgetManager, "")
        }
    }

}