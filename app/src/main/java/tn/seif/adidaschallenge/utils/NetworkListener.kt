package tn.seif.adidaschallenge.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await
import tn.seif.adidaschallenge.BuildConfig
import javax.inject.Inject

/**
 * A listener responsible of monitoring available networks on the device.
 * Checks if the device is still able to reach the server after a network change.
 * Notifies the observers through [NetworkListener.networkAvailability] livedata.
 *
 * @property okHttpClient - Http client instance used to ping the server.
 * @param application - Application used as context to register a
 */
open class NetworkListener @Inject constructor(
    application: Application,
    private val okHttpClient: OkHttpClient
) : ConnectivityManager.NetworkCallback() {

    private val connectivityManager by lazy {
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val _networkAvailability = MutableLiveData<Boolean>()

    /**
     * [LiveData] notifying observers of the server reachability status.
     *  - true - The server is reachable
     *  - false - The server is not reachable
     */
    val networkAvailability: LiveData<Boolean> = _networkAvailability

    private val availableNetworks = mutableSetOf<Network>()

    init {
        registerNetworkCallback()
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        checkServerReachability(network)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        // Remove the lost network from the list, and only check server reachability if we still have available networks.
        availableNetworks.remove(network)
        when (availableNetworks.isEmpty()) {
            true -> updateConnectionState(false)
            false -> checkServerReachability()
        }
    }

    /* region public functions */
    /**
     * Updates the actual connection state.
     * Can be used to notify the listener of tha status of the last network call to the server.
     *
     * @param isSuccessful - true if server is reachable, false otherwise.
     */
    open fun updateConnectionState(isSuccessful: Boolean) {
        if (_networkAvailability.value != isSuccessful)
            _networkAvailability.postValue(isSuccessful)
    }
    /* endregion */

    /* region private functions */
    /**
     * Checks the ability of the device to reach the server.
     * Handles the list of available networks if needed.
     *
     * @param network - The current network we are checking. Will be added/removed to/from the list of available network if not null.
     */
    private fun checkServerReachability(network: Network? = null) {
        CoroutineScope(IO).launch {
            try {
                val request = Request.Builder()
                    .url("${BuildConfig.API_BASE_URL}:${BuildConfig.PRODUCTS_PORT}")
                    .build()

                // This will fail if no internet connection is available.
                okHttpClient.newCall(request).await()

                updateConnectionState(true)
                handleAvailableNetworks(network, true)
            } catch (e: Exception) {
                updateConnectionState(false)
                handleAvailableNetworks(network, false)
            }
        }
    }

    private fun handleAvailableNetworks(network: Network?, isServerReachable: Boolean) {
        network?.let {
            when (isServerReachable) {
                true -> availableNetworks.add(it)
                false -> availableNetworks.remove(it)
            }
        }
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest
            .Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }
    /* endregion */
}
