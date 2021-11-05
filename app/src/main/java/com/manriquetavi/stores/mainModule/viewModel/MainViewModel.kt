package com.manriquetavi.stores.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manriquetavi.stores.common.entities.StoreEntity
import com.manriquetavi.stores.common.utlis.Constants
import com.manriquetavi.stores.common.utlis.StoresException
import com.manriquetavi.stores.common.utlis.TypeError
import com.manriquetavi.stores.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private var interactor: MainInteractor = MainInteractor()


    private val typeError: MutableLiveData<TypeError> = MutableLiveData()
    private val showProgress: MutableLiveData<Boolean> = MutableLiveData()


    private val stores = interactor.stores

    fun getTypeError(): MutableLiveData<TypeError> = typeError

    fun isShowProgress(): LiveData<Boolean>{
        return showProgress
    }


    fun getStores(): LiveData<MutableList<StoreEntity>>{
        return stores
    }

    fun deleteStore(storeEntity: StoreEntity){
        executeAction { interactor.deleteStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity) {
        storeEntity.isFavorite = !storeEntity.isFavorite
        executeAction { interactor.updateStore(storeEntity) }
    }

    private fun executeAction(block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            showProgress.value = Constants.SHOW
            try {
                block()
            } catch (e: StoresException) {
                typeError.value = e.typeError
                e.printStackTrace()
            } finally {
                showProgress.value = Constants.HIDE
            }
        }
    }
}