package com.manriquetavi.stores.mainModule

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.manriquetavi.stores.*
import com.manriquetavi.stores.common.entities.StoreEntity
import com.manriquetavi.stores.common.utlis.TypeError
import com.manriquetavi.stores.databinding.ActivityMainBinding
import com.manriquetavi.stores.editModule.EditStoreFragment
import com.manriquetavi.stores.editModule.viewModel.EditStoreViewModel
import com.manriquetavi.stores.mainModule.adapter.OnClickListener
import com.manriquetavi.stores.mainModule.adapter.StoreListAdapter
import com.manriquetavi.stores.mainModule.viewModel.MainViewModel

class MainActivity : AppCompatActivity(), OnClickListener {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAdapter: StoreListAdapter
    private lateinit var mGridlayout: GridLayoutManager

    //MVVM
    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditStoreViewModel: EditStoreViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        /*mBinding.btnSave.setOnClickListener {
            val store = StoreEntity(name = mBinding.edtName.text.toString().trim())

            Thread {
                StoreApplication.database.storeDao().addStore(store)
            }.start()
            mAdapter.add(store)
        }*/

        mBinding.fab.setOnClickListener {
            launchEditStoreFragment()
        }

        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this ).get(MainViewModel::class.java)
        mMainViewModel.getStores().observe(this, {  stores ->
            mBinding.pgBar.visibility = View.GONE
            mAdapter.submitList(stores)
            //mBinding.pgBar.visibility = if (stores.isEmpty()) View.VISIBLE else View.GONE
        })
        //Optimizando
        mMainViewModel.isShowProgress().observe(this, { isShowProgress ->
            mBinding.pgBar.visibility = if (isShowProgress) View.VISIBLE else View.GONE
        })

        mMainViewModel.getTypeError().observe(this, { typeError ->
            val msgRes = when(typeError){
                TypeError.GET -> getString(R.string.main_error_get)
                TypeError.INSERT -> getString(R.string.main_error_insert)
                TypeError.UPDATE -> getString(R.string.main_error_update)
                TypeError.DELETE -> getString(R.string.main_error_delete)
                else -> getString(R.string.main_error_unknown)
            }
            Snackbar.make(mBinding.root, msgRes, Snackbar.LENGTH_SHORT).show()

        })
        mEditStoreViewModel = ViewModelProvider(this).get(EditStoreViewModel::class.java)
        mEditStoreViewModel.getShowFab().observe(this,{ isVisible ->
            if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
        })
    }

    private fun launchEditStoreFragment(storeEntity: StoreEntity = StoreEntity()) {

        mEditStoreViewModel.setShowFab(false)
        mEditStoreViewModel.setStoreSelected(storeEntity)
        val fragment = EditStoreFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(R.id.containerMain, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        //mBinding.fab.hide()
        //hideFab()
    }

    private fun setupRecyclerView() {
        mAdapter = StoreListAdapter(this)
        mGridlayout = GridLayoutManager(this, resources.getInteger(R.integer.main_columns_rv))
        //getStores()

        mBinding.rv.apply {
            setHasFixedSize(true)
            layoutManager = mGridlayout
            adapter = mAdapter

        }
    }


    override fun onClick(storeEntity: StoreEntity) {
        launchEditStoreFragment(storeEntity)
    }

    override fun onFavoriteStore(storeEntity: StoreEntity) {
        mMainViewModel.updateStore(storeEntity)
    }

    override fun onDeleteStore(storeEntity: StoreEntity) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_options_title)
            .setItems(items, DialogInterface.OnClickListener { dialog, which ->
                when(which) {
                    0 -> confirmDialogDelete(storeEntity)
                    1 -> dial(storeEntity.phone)
                    2 -> goToWebsite(storeEntity.website)
                }
            })
            .show()
    }

    private fun confirmDialogDelete(storeEntity: StoreEntity) {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_delete_title)
            .setPositiveButton(R.string.dialog_delete_confirm, DialogInterface.OnClickListener { dialog, which ->
                mMainViewModel.deleteStore(storeEntity)
            })
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun dial(phone: String){
        val callIntent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phone")
        }
        startIntent(callIntent)
    }

    private fun goToWebsite(website: String){
        if(website.isEmpty()){
            Toast.makeText(this, "Esta tienda no tiene sitio Web", Toast.LENGTH_LONG).show()
        } else {
            val websiteIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(website)
            }
            startIntent(websiteIntent)
        }
    }

    private fun startIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null){
            startActivity(intent)
        } else Toast.makeText(this, "El celular no cuenta con recursos para llamar o ir al sitio web", Toast.LENGTH_LONG).show()
    }

    /*MainAux*/
    /*
    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mBinding.fab.show() else mBinding.fab.hide()
    }

    override fun addStore(storeEntity: StoreEntity) {
        mAdapter.add(storeEntity)

    }

    override fun updateStore(storeEntity: StoreEntity) {
        mAdapter.update(storeEntity)
    }*/

}