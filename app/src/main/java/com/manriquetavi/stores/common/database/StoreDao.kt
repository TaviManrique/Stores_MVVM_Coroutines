package com.manriquetavi.stores.common.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.manriquetavi.stores.common.entities.StoreEntity

@Dao
interface StoreDao {
    @Query("SELECT * FROM StoreEntity")
    fun getAllStores() : LiveData<MutableList<StoreEntity>>

    @Query("SELECT * FROM StoreEntity where id = :id")
    fun getStoreById(id: Long): LiveData<StoreEntity>

    @Insert
    suspend fun addStore(storeEntity: StoreEntity): Long

    @Update
    suspend fun updateStore(storeEntity: StoreEntity): Int
    //Int representa un valor numerico que indica todas las tiendas afectadas por esta accion

    @Delete
    suspend fun deleteStore(storeEntity: StoreEntity): Int
    //Int representa un valor numerico que indica todas las tiendas afectadas por esta accion
}