package com.openclassrooms.realestatemanager.data.cache.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.openclassrooms.realestatemanager.data.cache.AppDatabase
import com.openclassrooms.realestatemanager.models.property.Property
import com.openclassrooms.realestatemanager.models.property.PropertyType
import com.openclassrooms.realestatemanager.util.ConstantsTest
import com.openclassrooms.realestatemanager.util.JsonUtil
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class PropertyCacheDataSourceTest : TestCase() {

    private lateinit var database: AppDatabase
    private lateinit var jsonUtil: JsonUtil
    private lateinit var fakeProperties: List<Property>

    private lateinit var cacheDataSource: PropertyCacheDataSource

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java).allowMainThreadQueries().build()

        jsonUtil = JsonUtil()
        val rawJson = jsonUtil.readJSONFromAsset(ConstantsTest.PROPERTIES_DATA_FILENAME)
        fakeProperties = Gson().fromJson(rawJson, object : TypeToken<List<Property>>() {}.type)

        cacheDataSource = PropertyCacheDataSource(database.propertyDao())
    }

    @After
    fun clearDatabase() = database.clearAllTables()

    @Test
    fun given_cache_data_source_when_save_photos_then_counted_successfully() {
        // Given photos list and When photos list saved
        cacheDataSource.saveProperties(fakeProperties).blockingAwait()

        // Then count of photos in database is equal to given photos list size
        assertThat(cacheDataSource.count().blockingGet()).isEqualTo(fakeProperties.size)
    }

    @Test
    fun given_cache_data_source_when_save_a_property_then_saved_successfully() {
        // Given properties list and When properties list saved
        cacheDataSource.saveProperty(fakeProperties[0]).blockingAwait()

        // Then count of properties in database is equal to given properties list size
        assertThat(cacheDataSource.findPropertyById(fakeProperties[0].id).blockingGet()).isEqualTo(fakeProperties[0])
    }

    @Test
    fun given_cache_data_source_when_save_properties_then_saved_successfully() {
        // Given properties list and When properties list saved
        fakeProperties = fakeProperties.sortedBy { it.id }
        cacheDataSource.saveProperties(fakeProperties).blockingAwait()

        // Then count of properties in database is equal to given properties list size
        var actualProperties = cacheDataSource.findAllProperties().blockingGet()

        actualProperties = actualProperties.sortedBy { it.id }
        assertThat(actualProperties).isEqualTo(fakeProperties)
    }

    @Test
    fun given_cache_data_source_when_find_all_properties_then_found_successfully() {

        // Given properties list
        fakeProperties = fakeProperties.sortedBy { it.id }

        // When properties list saved
        cacheDataSource.saveProperties(fakeProperties).blockingAwait()

        var actualProperties = cacheDataSource.findAllProperties().blockingGet()

        // Then returned properties in database is equal to given properties list
        actualProperties = actualProperties.sortedBy { it.id }
        actualProperties.forEachIndexed { index, property ->
            assertThat(property).isEqualTo(fakeProperties[index])
        }
    }

    @Test
    fun given_cache_data_source_when_find_property_by_id_then_found_successfully() {
        cacheDataSource.saveProperties(fakeProperties).blockingAwait()
        val property = fakeProperties[fakeProperties.indices.random()]
        val expectedProperty: Property = cacheDataSource.findPropertyById(property.id).blockingGet()
        assertThat(expectedProperty).isEqualTo(property)
    }

    @Test
    fun given_cache_data_source_when_find_properties_by_ids_then_found_successfully() {
        cacheDataSource.saveProperties(fakeProperties).blockingAwait()
        val propertyIds = fakeProperties.subList(0, 2).map { property -> property.id }
        val expectedProperties: List<Property> = cacheDataSource.findPropertiesByIds(propertyIds).blockingGet()
        assertThat(expectedProperties).isEqualTo(fakeProperties.subList(0, 2))
    }

    @Test
    fun given_cache_data_source_when_update_property_then_updated_successfully() {
        val initialProperty = fakeProperties[fakeProperties.indices.random()]

        cacheDataSource.saveProperties(fakeProperties).blockingAwait()

        val updatedProperty = initialProperty.copy()
        with(updatedProperty) {
            description = "new description"
            type = PropertyType.values().first { type -> type != initialProperty.type }
        }
        cacheDataSource.updateProperty(updatedProperty).blockingAwait()

        val finalProperty = cacheDataSource.findPropertyById(initialProperty.id).blockingGet()
        assertThat(finalProperty).isEqualTo(updatedProperty)
    }

    @Test
    fun given_cache_data_source_when_update_properties_then_updated_successfully() {
        var initialProperties = arrayOf(fakeProperties[0], fakeProperties[1])

        cacheDataSource.saveProperties(fakeProperties).blockingAwait()

        var updatedProperties = initialProperties.copyOf().toList()
        updatedProperties.forEachIndexed { index,  updatedProperty ->
            with(updatedProperty) {
                description = "new description"
                type = PropertyType.values().first { type -> type != initialProperties[index].type }
            }
        }
        updatedProperties = updatedProperties.sortedBy { it.id }
        cacheDataSource.updateProperties(updatedProperties).blockingAwait()

        val ids = initialProperties.map { photo -> photo.id }
        var finalProperties = cacheDataSource.findAllProperties().blockingGet().filter {
                photo -> ids.contains(photo.id)
        }
        finalProperties = finalProperties.sortedBy { it.id }

        assertThat(finalProperties).isEqualTo(updatedProperties.toList())
    }

    @Test
    fun given_cache_data_source_when_delete_property_by_id_then_deleted_successfully() {
        cacheDataSource.saveProperties(fakeProperties).blockingAwait()
        val property = fakeProperties[fakeProperties.indices.random()]
        cacheDataSource.deletePropertyById(property.id).blockingAwait()
        assertThat(cacheDataSource.findAllProperties().blockingGet().contains(property))
            .isFalse()
    }

    @Test
    fun given_cache_data_source_when_delete_properties_by_ids_then_deleted_successfully() {
        cacheDataSource.saveProperties(fakeProperties).blockingAwait()
        val propertyIds = fakeProperties.subList(0, 2).map { property -> property.id }
        cacheDataSource.deletePropertiesByIds(propertyIds).blockingAwait()

        val findAllProperties = cacheDataSource.findAllProperties().blockingGet()
        assertThat(findAllProperties.size).isEqualTo((fakeProperties.size - 2))
        assertThat(findAllProperties.containsAll(fakeProperties.subList(0, 2))).isFalse()
    }

    @Test
    fun given_cache_data_source_when_delete_properties_then_deleted_successfully() {
        cacheDataSource.saveProperties(fakeProperties).blockingAwait()
        assertThat(cacheDataSource.count().blockingGet()).isEqualTo(fakeProperties.size)

        cacheDataSource.deleteProperties(fakeProperties.subList(0, 2)).blockingAwait()

        val findAllProperties = cacheDataSource.findAllProperties().blockingGet()
        assertThat(findAllProperties.size).isEqualTo((fakeProperties.size - 2))
    }

    @Test
    fun given_cache_data_source_when_delete_all_properties_then_deleted_successfully() {
        cacheDataSource.saveProperties(fakeProperties).blockingAwait()
        assertThat(cacheDataSource.count().blockingGet()).isEqualTo(fakeProperties.size)
        cacheDataSource.deleteAllProperties().blockingAwait()
        assertThat(cacheDataSource.findAllProperties().blockingGet()).isEmpty()
    }
}