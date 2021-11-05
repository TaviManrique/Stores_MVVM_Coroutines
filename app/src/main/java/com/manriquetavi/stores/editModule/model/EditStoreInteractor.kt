package com.manriquetavi.stores.editModule.model

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import com.manriquetavi.stores.StoreApplication
import com.manriquetavi.stores.common.entities.StoreEntity
import com.manriquetavi.stores.common.utlis.StoresException
import com.manriquetavi.stores.common.utlis.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditStoreInteractor {

    fun getStoreById(id: Long): LiveData<StoreEntity>{
        return StoreApplication.database.storeDao().getStoreById(id)
    }

    suspend fun saveStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        try {
            StoreApplication.database.storeDao().addStore(storeEntity)
        } catch (e: SQLiteConstraintException){
            throw StoresException(TypeError.INSERT)
        }
    }


    suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        //storeEntity.id = -1 provocar un error al actualizar
        try {
            val result = StoreApplication.database.storeDao().updateStore(storeEntity)
            if (result == 0) throw StoresException(TypeError.UPDATE)
        } catch (e: SQLiteConstraintException){
            throw StoresException(TypeError.UPDATE)
        }
    }
}