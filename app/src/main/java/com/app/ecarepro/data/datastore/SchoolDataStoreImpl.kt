package com.app.ecarepro.data.datastore

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SchoolDataStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : SchoolDataStore {

    /*    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "school_datastore")


        override suspend fun saveSlides(sliders: List<Slide>) {
            context.dataStore.edit { preferences ->
                preferences[slidesKey] = gson.toJson(sliders)
            }
        }

        override fun getSlides(): Flow<List<Slide>> {
            return context.dataStore.data.map { preferences ->
                val itemType = object : TypeToken<List<Slide>>() {}.type
                gson.fromJson<List<Slide>>(preferences[slidesKey], itemType)
            }
        }


        companion object {
            private val slidesKey = stringPreferencesKey("slides")

        }*/


}