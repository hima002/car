package com.hima.alwarsha.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hima.alwarsha.data.model.Workshop
import com.hima.alwarsha.data.repository.WorkshopRepository
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException

data class WorkshopUiState(
    val isLoading: Boolean = false,
    val workshops: List<Workshop> = emptyList(),
    val errorMessageAr: String? = null
)

class WorkshopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WorkshopRepository()
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    private val _uiState = MutableStateFlow(WorkshopUiState())
    val uiState: StateFlow<WorkshopUiState> = _uiState.asStateFlow()

    fun searchNearby(carBrand: String?) {
        val context = getApplication<Application>()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            _uiState.value = WorkshopUiState(errorMessageAr = "محتاج صلاحية الموقع الأول عشان أقدر أدور على ورش قريبة منك.")
            return
        }

        _uiState.value = WorkshopUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val location = fusedLocationClient.lastLocation.await()
                if (location == null) {
                    _uiState.value = WorkshopUiState(
                        errorMessageAr = "مقدرت أحدد موقعك الحالي. جرّب تفتح خرائط جوجل أول مرة عشان يحدد موقعك، وبعدين رجّع افتح الصفحة دي."
                    )
                    return@launch
                }
                val workshops = repository.findNearbyWorkshops(
                    userLat = location.latitude,
                    userLng = location.longitude,
                    carBrand = carBrand
                )
                _uiState.value = WorkshopUiState(
                    workshops = workshops,
                    errorMessageAr = if (workshops.isEmpty()) "مفيش ورش قريبة منك في نطاق 50 كم." else null
                )
            } catch (e: HttpException) {
                // Surfacing the raw status/body temporarily (not a public product) so failures are
                // diagnosable without needing device logcat access.
                val body = e.response()?.errorBody()?.string()?.take(300)
                _uiState.value = WorkshopUiState(errorMessageAr = "حصل خطأ (كود ${e.code()}) من Google Places: ${body ?: e.message()}")
            } catch (e: Exception) {
                _uiState.value = WorkshopUiState(errorMessageAr = e.message ?: "حصل خطأ في الاتصال: ${e::class.simpleName}")
            }
        }
    }
}
