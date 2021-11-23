package com.github.kutyrev.rushydroinventorisation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kutyrev.rushydroinventorisation.databinding.FragmentDocBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DocFragment : Fragment() {

    private var _binding: FragmentDocBinding? = null
    private lateinit var adapter : DocListAdapter
    private var docInv : DocInv = DocInv("","")
    private val args: DocFragmentArgs by navArgs()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDocBinding.inflate(inflater, container, false)

        binding.fab.setOnClickListener { view ->
            findNavController().navigate(R.id.action_DocFragment_to_CameraFragment)
        }

        binding.send.setOnClickListener { view ->
            val retrofitServices: RetrofitServices = Common.retrofitService
            retrofitServices.updateInvDocument(Common.db_name, DocInv(docInv.docType, docInv.docNumber,
                adapter.currentList)).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    Log.d("DocFragment", "onResponse")
                    val snack = view?.let { Snackbar.make(it,getString(R.string.doc_updated),
                        Snackbar.LENGTH_LONG) }
                    snack?.show()
                }

                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Log.d("DocFragment", "onFailure")
                    val snack = view?.let { Snackbar.make(it,getString(R.string.doc_not_updated),
                        Snackbar.LENGTH_LONG) }
                    snack?.show()
                }

            })
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DocListAdapter()

        binding.listRecycler.adapter = adapter
        binding.listRecycler.layoutManager = LinearLayoutManager(context)

        args.goods?.let {
            docInv = it
        }

        adapter.submitList(docInv.goods)

        if (args.scannedBarcode.isNotEmpty() ){
            Log.d("DocFragment", args.scannedBarcode)
            val updated = adapter.incFactNum(args.scannedBarcode)
            if (!updated){
                val barcodeArg = args.scannedBarcode
                val snack = view.let { Snackbar.make(it,getString(R.string.barcode_search_error),
                    Snackbar.LENGTH_LONG) }
                snack.show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}