package com.dicoding.storyapp.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")
class UserPreference private constructor(private val dataStore: DataStore<Preferences>){

    suspend fun saveUser(userModel: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = userModel.name
            preferences[TOKEN_KEY] = userModel.token
            preferences[STATE_KEY] = userModel.isLogin
        }
    }

    fun getUser(): Flow<UserModel> {
        return dataStore.data.map {
            val name = it[NAME_KEY] ?: ""
            val token = it[TOKEN_KEY] ?: ""
            val isLogin = it[STATE_KEY] ?: false

            UserModel(name, token, isLogin)
        }
    }

    suspend fun getToken(): String {
        return dataStore.data.map {
            it[TOKEN_KEY] ?: ""
        }.first()
    }

    suspend fun logout(){
        dataStore.edit { preferences ->
            preferences.remove(NAME_KEY)
            preferences.remove(TOKEN_KEY)
            preferences.remove(STATE_KEY)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this){
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}