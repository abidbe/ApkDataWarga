package com.abidbe.myregister.ui.insert

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.abidbe.myregister.R
import com.abidbe.myregister.ViewModelFactory
import com.abidbe.myregister.database.User
import com.abidbe.myregister.databinding.ActivityRegisterBinding
import com.abidbe.myregister.getAddressFromLatLng
import com.abidbe.myregister.getImageUri
import com.abidbe.myregister.ui.maps.MapsActivity
import java.util.Calendar

class RegisterActivity : AppCompatActivity() {

    private var isEdit = false
    private var user: User? = null
    private lateinit var registerViewModel: RegisterViewModel
    private var _activityRegisterBinding: ActivityRegisterBinding? = null
    private val binding get() = _activityRegisterBinding!!

    private var currentImageUri: Uri? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission request granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_LONG).show()
            }
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        registerViewModel = obtainViewModel(this)

        user = intent.getParcelableExtra(EXTRA_USER)
        if (user != null) {
            isEdit = true
        } else {
            user = User()
        }
        val actionBarTitle: String
        val btnTitle: String
        if (isEdit) {
            actionBarTitle = getString(R.string.change)
            btnTitle = getString(R.string.update)
            if (user != null) {
                user?.let { user ->
                    binding.edtName.setText(user.name)
                    binding.edtDate.setText(user.date)
                    selectedLatitude = user.latitude
                    selectedLongitude = user.longitude
                    if (selectedLatitude != null && selectedLongitude != null) {
                        val address =
                            getAddressFromLatLng(this, selectedLatitude!!, selectedLongitude!!)
                        binding.edtLocation.setText(address)
                    }
                    if (user.photoUri != null) {
                        binding.preview.setImageURI(Uri.parse(user.photoUri))
                    }
                }
            }
        } else {
            actionBarTitle = getString(R.string.add)
            btnTitle = getString(R.string.save)
        }
        supportActionBar?.title = actionBarTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.btnSubmit.text = btnTitle

        binding.edtDate.setOnClickListener { showDatePickerDialog() }
        binding.fabGallery.setOnClickListener { startGallery() }
        binding.fabCamara.setOnClickListener { startCamera() }
        binding.btnPickLocation.setOnClickListener { startLocationPicker() }
        binding.btnSubmit.setOnClickListener { submit() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog(ALERT_DIALOG_CLOSE)
            }
        })
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherCamera.launch(currentImageUri)
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess: Boolean ->
        if (isSuccess) {
            showImage()
        } else {
            Toast.makeText(this, "Gagal mengambil gambar", Toast.LENGTH_SHORT).show()
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val launcherLocationPicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let {
                selectedLatitude = it.getDoubleExtra("selected_lat", 0.0)
                selectedLongitude = it.getDoubleExtra("selected_lng", 0.0)
                val address = getAddressFromLatLng(this, selectedLatitude!!, selectedLongitude!!)
                binding.edtLocation.setText(address)
            }
        }
    }

    private fun startLocationPicker() {
        val intent = Intent(this, MapsActivity::class.java)
        launcherLocationPicker.launch(intent)
    }

    private fun showImage() {
        currentImageUri?.let {
            try {
                val inputStream = contentResolver.openInputStream(it)
                val drawable = Drawable.createFromStream(inputStream, it.toString())
                binding.preview.setImageDrawable(drawable)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private fun submit() {
        val name = binding.edtName.text.toString().trim()
        val dateOfBirth = binding.edtDate.text.toString().trim()
        val location = binding.edtLocation.text.toString().trim()
        when {
            name.isEmpty() -> {
                binding.edtName.error = getString(R.string.empty)
            }

            dateOfBirth.isEmpty() -> {
                binding.edtDate.error = getString(R.string.empty)
            }

            location.isEmpty() -> {
                binding.edtLocation.error = getString(R.string.empty)
            }

            else -> {
                user?.let { user ->
                    user.name = name
                    user.date = dateOfBirth
                    user.photoUri = currentImageUri.toString()
                    user.latitude = selectedLatitude
                    user.longitude = selectedLongitude
                }
                if (isEdit) {
                    registerViewModel.update(user as User)
                    showToast(getString(R.string.changed))
                } else {
                    registerViewModel.insert(user as User)
                    showToast(getString(R.string.added))
                }
                finish()
            }
        }
    }


    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear/${selectedMonth + 1}/$selectedDay"
                binding.edtDate.setText(selectedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun obtainViewModel(activity: AppCompatActivity): RegisterViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory).get(RegisterViewModel::class.java)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.menufile, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showAlertDialog(ALERT_DIALOG_DELETE)
            android.R.id.home -> showAlertDialog(ALERT_DIALOG_CLOSE)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showAlertDialog(type: Int) {
        val isDialogClose = type == ALERT_DIALOG_CLOSE
        val dialogTitle: String
        val dialogMessage: String
        if (isDialogClose) {
            dialogTitle = getString(R.string.cancel)
            dialogMessage = getString(R.string.message_cancel)
        } else {
            dialogMessage = getString(R.string.message_delete)
            dialogTitle = getString(R.string.delete)
        }
        val alertDialogBuilder = AlertDialog.Builder(this)
        with(alertDialogBuilder) {
            setTitle(dialogTitle)
            setMessage(dialogMessage)
            setCancelable(false)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                if (!isDialogClose) {
                    registerViewModel.delete(user as User)
                    showToast(getString(R.string.deleted))
                }
                finish()
            }
            setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val EXTRA_USER = "extra_user"
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }
}
