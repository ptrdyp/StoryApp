package com.dicoding.storyapp.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.StoryWidgetRepository
import com.dicoding.storyapp.data.di.UserPreference
import com.dicoding.storyapp.data.di.dataStore
import com.dicoding.storyapp.data.retrofit.ApiConfig

class StackRemoteViewsFactory(
    private val context: Context,
    private val pref: UserPreference,
    private val appwidgetId: Int,
    private val appWidgetManager: AppWidgetManager,
    private val token: String
) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var viewModel: StoryWidgetViewModel
    private val widgetItems = ArrayList<Bitmap>()
    private var lastUpdateTime: Long = 0

    override fun onCreate() {
        val apiService = ApiConfig.getApiService(token)
        val userPreference = UserPreference.getInstance(context.dataStore)
        val repository = StoryWidgetRepository.create(apiService, userPreference, appwidgetId, appWidgetManager)

        viewModel = StoryWidgetViewModel(apiService, userPreference, repository)
    }

    private fun shouldUpdateData(): Boolean {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastUpdateTime
        return elapsedTime > INTERVAL_BETWEEN_UPDATES
    }
    override fun onDataSetChanged() {
        if (shouldUpdateData()) {
            viewModel.refreshWidgetItems(ImageLoader(context), appwidgetId) {
                widgetItems.clear()
                widgetItems.addAll(it)
                appWidgetManager.notifyAppWidgetViewDataChanged(appwidgetId, R.id.stack_view)
                lastUpdateTime = System.currentTimeMillis()
            }
        }
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = widgetItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView, widgetItems[position])

        val extras = Intent().putExtra(StoryAppWidget.EXTRA_ITEM, position)
        extras.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appwidgetId)

        val fillInIntent = Intent().putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = false

    companion object {
        private const val INTERVAL_BETWEEN_UPDATES = 5 * 60 * 1000
    }
}