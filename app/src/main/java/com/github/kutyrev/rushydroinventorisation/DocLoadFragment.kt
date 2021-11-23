package com.github.kutyrev.rushydroinventorisation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.kutyrev.rushydroinventorisation.databinding.FragmentDocLoadBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope

import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
import java.net.SocketTimeoutException


class DocLoadFragment : Fragment() {

    val Context.defDataStore: DataStore<Preferences> by preferencesDataStore(name = "last_values")
    private var _binding: FragmentDocLoadBinding? = null

    var isNetworkConnected: Boolean = false
    var docType: String = Common.GOODS_DOC_TYPE
    val DOC_NUM = stringPreferencesKey("document_number")

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDocLoadBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerNetworkCallback()


        binding.buttonSearchAndLoad.setOnClickListener {


            if (activity != null){
                val prefs = PreferenceManager.getDefaultSharedPreferences(activity?.applicationContext)
                if(Common.base_url != prefs.getString("server_addr", "").toString()){
                    Common.base_url = prefs.getString("server_addr", "").toString()
                }
            }

            if (Common.db_name.isEmpty() || Common.base_url.isEmpty()) {
                findNavController().navigate(R.id.open_settings_fragment)
            } else {

                lifecycleScope.launch {
                    activity?.let { it1 ->
                        saveDocDefValue(
                            it1.defDataStore,
                            binding.editDocNumber.text.toString()
                        )
                    }
                }

                binding.progressLoad.show()
                getDocInfo()
            }
        }

        binding.radioGoods.isChecked = true

        binding.radioGroupDocType.setOnCheckedChangeListener { radioGroup, i ->

            if (i == binding.radioGoods.id) {
                docType = Common.GOODS_DOC_TYPE
            } else {
                docType = Common.FIXED_ASSETS_DOC_TYPE
            }
        }

        binding.progressLoad.hide()

        lifecycleScope.launch() {
            activity?.let {
                getDocDefValue(it.defDataStore).collect { state ->
                    withContext(Dispatchers.Main) {
                        binding.editDocNumber.setText(state)
                    }
                }
            }
        }

    }

// Network Check
private fun registerNetworkCallback() {
    try {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                isNetworkConnected = true // Global Static Variable
            }

            override fun onLost(network: Network) {
                isNetworkConnected = false // Global Static Variable

                val snack = view?.let {
                    Snackbar.make(
                        it, getString(R.string.connection_not_found),
                        Snackbar.LENGTH_LONG
                    )
                }
                snack?.show()

            }
        }
        )
        isNetworkConnected = false

    } catch (e: Exception) {
        isNetworkConnected = false
        val snack = view?.let {
            Snackbar.make(
                it, getString(R.string.connection_not_found),
                Snackbar.LENGTH_LONG
            )
        }
        snack?.show()
    }
}


private fun getDocInfo() {

    val retrofitServices: RetrofitServices = Common.retrofitService


    //"00БП-000007"
    retrofitServices.getInvDocument(Common.db_name, docType, binding.editDocNumber.text.toString())
        .enqueue(object : Callback<DocInv> {
            override fun onFailure(call: Call<DocInv>, t: Throwable) {
                if (t is SocketTimeoutException) {
                    val snack = view?.let {
                        Snackbar.make(
                            it, getString(R.string.timeout_exception),
                            Snackbar.LENGTH_LONG
                        )
                    }
                    snack?.show()
                } else {
                    val snack = view?.let {
                        Snackbar.make(
                            it, getString(R.string.connection_to_db),
                            Snackbar.LENGTH_LONG
                        )
                    }
                    snack?.show()
                }

                binding.progressLoad.hide()
            }

            override fun onResponse(call: Call<DocInv>, response: Response<DocInv>) {
                binding.progressLoad.hide()
                val action =
                    DocLoadFragmentDirections.actionDocLoadFragmentToDocFragment(response.body())
                findNavController().navigate(action)
            }
        })
}


override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}

private suspend fun getDocDefValue(prefsDataStore: DataStore<Preferences>): Flow<String> {
    return prefsDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[DOC_NUM] ?: ""
        }
}

suspend fun saveDocDefValue(prefsDataStore: DataStore<Preferences>, ducNum: String) {
    prefsDataStore.edit { preferences ->
        preferences[DOC_NUM] = ducNum
    }
}
}