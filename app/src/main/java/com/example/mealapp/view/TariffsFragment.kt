package com.example.mealapp.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.mealapp.model.Tariff
import com.example.mealapp.roomdb.TariffDao
import com.example.mealapp.roomdb.TariffDatabase
import com.example.userinterface.databinding.FragmentTariffsBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.ByteArrayOutputStream

class TariffsFragment : Fragment() {

    private var _binding: FragmentTariffsBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null
    private val mDisposable =
        CompositeDisposable()     // Sürekli istek gelirse hepsini biriktirme, sil. RxJava sağlıyor bunu bize

    private lateinit var db: TariffDatabase
    private lateinit var dao: TariffDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLaunchers()
        db = Room.databaseBuilder(requireContext(), TariffDatabase::class.java, "Tariffs").build()
        dao = db.tariffDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTariffsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView5.setOnClickListener { chooseImage(it) }
        binding.saveButton.setOnClickListener { save(it) }
        binding.delButton.setOnClickListener { delete(it) }

        arguments?.let {
            val info = TariffsFragmentArgs.fromBundle(it).info
            if (info == "new") {
                binding.saveButton.isEnabled = true
                binding.delButton.isEnabled = false
                binding.mealNameTextView.text = null
                binding.ingredientsTextView.text = null

            } else {
                binding.saveButton.isEnabled = false
                binding.delButton.isEnabled = true
            }

        }
    }

    fun chooseImage(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // eğer Android sdk versiyon 33 'ten büyükse READ_MEDIA_IMAGES
            checkAndRequestPermission(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            // eğer Android sdk versiyon 33 'ten küçükse READ_EXTERNAL_STORAGE
            checkAndRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    fun save(view: View) {
        val name = binding.mealNameTextView.text.toString()
        val ingredient = binding.ingredientsTextView.text.toString()

        if (imageBitmap != null) {
            val imgSelectedBitmap = makeImagesSmaller(imageBitmap!!, 300)
            val outPutStream = ByteArrayOutputStream()
            imgSelectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
            val imgByteArray = outPutStream.toByteArray()

            val tariff = Tariff(name, ingredient, imgByteArray)
            mDisposable.add(
                dao.saveTariff(tariff)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse)
            )
        }
    }

    fun delete(view: View) {

    }

    private fun handleResponse() {
        val activity = TariffsFragmentDirections.actionTariffsFragmentToListFragment()
        Navigation.findNavController(requireView()).navigate(activity)
    }

    private fun makeImagesSmaller(selectedImage: Bitmap, maxResolution: Int): Bitmap {
        var width = selectedImage.width
        var height = selectedImage.height

        val customBitmapScale: Double = width.toDouble() / height.toDouble()
        if (customBitmapScale > 1) {
            width = maxResolution
            val smallerHeight = width / customBitmapScale
            height = smallerHeight.toInt()
        } else {
            height = maxResolution
            val smallerWidth = height / customBitmapScale
            width = smallerWidth.toInt()
        }

        return Bitmap.createScaledBitmap(selectedImage, width, height, true)
    }

    private fun checkAndRequestPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(), permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // kullanıcı izin vermemiş, izin almamız gerekiyor.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    permission
                )
            ) {
                // kullanıcıdan neden izin istediğimizi SnackBar ile göstermemiz lazım
                Snackbar.make(
                    requireView(),
                    "Uygulama devam edebilmek için galeriye erişmek zorunda",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Allow", View.OnClickListener {
                    // izin isteyeceğiz
                    permissionLauncher.launch(permission)
                }).show()
            } else {
                // izin isteyeceğiz
                permissionLauncher.launch(permission)
            }
        } else {
            // izin verilmiş ve galeriye gideceğiz
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLaunchers() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                try {
                    // izin verildi ve şimdi aktivite basladı
                    if (result.resultCode == AppCompatActivity.RESULT_OK) {
                        // kullanıcı resim seçti
                        val choosenImage = result.data

                        if (choosenImage != null) {
                            imageUri = choosenImage.data
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source =
                                    ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        imageUri!!
                                    )
                                imageBitmap = ImageDecoder.decodeBitmap(source)
                                binding.imageView5.setImageBitmap(imageBitmap)
                            } else {
                                imageBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    imageUri
                                )
                                binding.imageView5.setImageBitmap(imageBitmap)
                            }
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                }
            }


        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    // izin verildi ve galeriye gidilir
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    // izin verilmedi
                    Toast.makeText(requireContext(), "İzin verilmedi!", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }
}