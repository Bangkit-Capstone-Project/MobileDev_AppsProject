package com.example.tanamin.nonui.userpreference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    fun getToken() : Flow<String> {
        return dataStore.data.map {
            it[TOKEN_KEY] ?: ""
        }
    }

    fun getRefreshToken(): Flow<String>{
        return dataStore.data.map {
            it[REFRESH_TOKEN_KEY] ?: ""
        }
    }

    fun getSession() : Flow<Boolean> {
        return dataStore.data.map {
            it[STATE_KEY] ?: false
        }
    }

    suspend fun setToken(token: String){
        dataStore.edit {
            it[TOKEN_KEY] = token
        }
    }

    suspend fun setRefreshToken(refreshToken: String){
        dataStore.edit {
            it[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun login() {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = false
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null
        private val STATE_KEY = booleanPreferencesKey("state")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}