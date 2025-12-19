package nl.connectplay.scoreplay.viewModels.profile

import android.content.Context
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.connectplay.scoreplay.api.ProfileApi
import nl.connectplay.scoreplay.models.user.UserProfile

class ProfileEditViewModel(
    private val currentUser: UserProfile, private val profileApi: ProfileApi
) : ViewModel() {

    private val _updatedProfileState = MutableStateFlow<UiState<UserProfile>>(UiState.Idle)
    val updatedProfileState = _updatedProfileState.asStateFlow()
    private val _username = MutableStateFlow(currentUser.username)
    val username = _username.asStateFlow()
    private val _email = MutableStateFlow(currentUser.email)
    val email = _email.asStateFlow()

    // Existing picture URL from backend
    private val _pictureUrl = MutableStateFlow(currentUser.picture)
    val pictureUrl = _pictureUrl.asStateFlow()

    // New image selected by user (not uploaded yet)
    private val _pendingImageUri = MutableStateFlow<Uri?>(null)
    val pendingImageUri = _pendingImageUri.asStateFlow()

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPictureChanged(newPicture: Uri) {
        _pendingImageUri.value = newPicture
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun onSaveProfile(context: Context) {
        val emailState = _email.value
        if (!isValidEmail(emailState)) {
            _updatedProfileState.update { UiState.Error("Invalid email format") }
            return
        }

        viewModelScope.launch {
            _updatedProfileState.value = UiState.Loading
            try {
                _pendingImageUri.value?.let { uri ->
                    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                        ?: throw Exception("Failed to read image")
                    profileApi.uploadProfilePicture(bytes)
                }

                val result = profileApi.updateProfile(
                    username = _username.value,
                    email = _email.value,
                )

                _pictureUrl.value = result.picture
                _pendingImageUri.value = null
                _updatedProfileState.value = UiState.Success(result)
            } catch (e: Exception) {
                _updatedProfileState.value = UiState.Error(e.message ?: "Unknown error", e)
            }
        }
    }
}
