package com.example.mealapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.mealapp.model.Tariff
import com.example.mealapp.roomdb.TariffDao
import com.example.mealapp.roomdb.TariffDatabase
import com.example.userinterface.databinding.FragmentListBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private val mDisposable =
        CompositeDisposable()     // Sürekli istek gelirse hepsini biriktirme, sil. RxJava sağlıyor bunu bize

    private lateinit var db: TariffDatabase
    private lateinit var dao: TariffDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(), TariffDatabase::class.java, "Tariffs").build()
        dao = db.tariffDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener { addNewMeal(it) }
        binding.tariffRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        getAllTariffs()
    }

    private fun getAllTariffs() {
        mDisposable.add(
            dao.getAllTariffs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    private fun handleResponse(tariffs: List<Tariff>) {
        tariffs.forEach { result ->
            println(result.name)
            println(result.ingredients)

        }
    }

    fun addNewMeal(view: View) {
        val action =
            ListFragmentDirections.actionListFragmentToTariffsFragment(info = "new", id = 0)
        Navigation.findNavController(view).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }
}