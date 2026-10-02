package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.Customer
import com.example.data.CustomerRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface Screen {
    data object Dashboard : Screen
    data object SearchCustomer : Screen
    data object AddCustomer : Screen
    data class CustomerDetail(val customerId: Long) : Screen
    data class Detecting(val customerId: Long) : Screen
    data class RouterConfig(val customerId: Long) : Screen
    data object OpticalCalculator : Screen
}

data class DashboardStats(
    val total: Int = 0,
    val online: Int = 0,
    val internetDown: Int = 0,
    val ponProblems: Int = 0
)

data class RouterConfigState(
    val configType: String = "PPPoE", // "PPPoE" or "Static IP"
    val routerModel: String = "GX Earth 1000 E",
    val pppoeUsername: String = "",
    val pppoePassword: String = "",
    val vlanId: String = "100",
    val ipAddress: String = "",
    val subnet: String = "255.255.255.0",
    val gateway: String = "",
    val detectionNotes: String = "",
    val isPushing: Boolean = false
)

data class DetectionState(
    val isRunning: Boolean = false,
    val currentStep: Int = 0,
    val currentMessage: String = "",
    val logs: List<String> = emptyList()
)

data class AcsSettings(
    val isLocalMode: Boolean = true,
    val isAcsOnline: Boolean = true,
    val serverUrl: String = "http://192.168.1.1:7557",
    val authUsername: String = "admin",
    val authSecret: String = "genieacs"
)

class FtthManagerViewModel(private val repository: CustomerRepository) : ViewModel() {

    private val screenStack = mutableListOf<Screen>(Screen.Dashboard)
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _filterStatus = MutableStateFlow("All")
    val filterStatus: StateFlow<String> = _filterStatus.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeCustomer = MutableStateFlow<Customer?>(null)
    val activeCustomer: StateFlow<Customer?> = _activeCustomer.asStateFlow()

    private val _configState = MutableStateFlow(RouterConfigState())
    val configState: StateFlow<RouterConfigState> = _configState.asStateFlow()

    private val _detectionState = MutableStateFlow(DetectionState())
    val detectionState: StateFlow<DetectionState> = _detectionState.asStateFlow()

    private val _acsSettings = MutableStateFlow(AcsSettings())
    val acsSettings: StateFlow<AcsSettings> = _acsSettings.asStateFlow()

    private val _notificationMessage = MutableSharedFlow<String>()
    val notificationMessage: SharedFlow<String> = _notificationMessage.asSharedFlow()

    val allCustomers: StateFlow<List<Customer>> = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCustomers: StateFlow<List<Customer>> = combine(
        repository.allCustomers,
        _filterStatus,
        _searchQuery
    ) { list, filter, query ->
        list.filter { customer ->
            val matchesFilter = when (filter) {
                "Online" -> customer.status == "Online"
                "Internet Down" -> customer.status == "Internet Down"
                "PON Problem" -> customer.status == "PON Problem"
                else -> true
            }
            val matchesQuery = if (query.isBlank()) true else {
                customer.mac.contains(query, ignoreCase = true) ||
                customer.name.contains(query, ignoreCase = true) ||
                customer.pppoeUsername.contains(query, ignoreCase = true) ||
                customer.routerModel.contains(query, ignoreCase = true)
            }
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<DashboardStats> = repository.allCustomers.combine(_filterStatus) { list, _ ->
        DashboardStats(
            total = list.size,
            online = list.count { it.status == "Online" },
            internetDown = list.count { it.status == "Internet Down" },
            ponProblems = list.count { it.status == "PON Problem" }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    init {
        viewModelScope.launch {
            repository.prepopulateIfNeeded()
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            val previous = screenStack.last()
            _currentScreen.value = previous
            return true
        }
        return false
    }

    fun setFilterStatus(status: String) {
        _filterStatus.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCustomer(customer: Customer) {
        _activeCustomer.value = customer
        navigateTo(Screen.CustomerDetail(customer.id))
    }

    fun selectCustomerById(id: Long) {
        viewModelScope.launch {
            val customer = allCustomers.value.find { it.id == id }
            _activeCustomer.value = customer
        }
    }

    fun addCustomer(
        mac: String,
        name: String,
        status: String,
        phone: String = "",
        vlan: String = "100",
        goToConfig: Boolean = false
    ) {
        viewModelScope.launch {
            val formattedMac = mac.trim().uppercase()
            val newCustomer = Customer(
                mac = formattedMac,
                name = name.trim(),
                status = status,
                phone = phone.trim(),
                vlanId = vlan.ifBlank { "100" },
                configType = "PPPoE",
                pppoeUsername = if (name.isNotBlank()) "${name.lowercase().replace(" ", "")}@bsnl.in" else "",
                pppoePassword = "password123",
                routerModel = "GX Earth 1000 E",
                rxPowerDbm = if (status == "PON Problem") -29.5 else -19.2,
                txPowerDbm = 2.3
            )
            val newId = repository.insert(newCustomer)
            val savedCustomer = newCustomer.copy(id = newId)
            _activeCustomer.value = savedCustomer
            _notificationMessage.emit("Customer ${savedCustomer.name} added successfully")

            if (goToConfig) {
                openManualConfig(savedCustomer)
            } else {
                navigateTo(Screen.Dashboard)
            }
        }
    }

    fun searchCustomerByMac(mac: String) {
        viewModelScope.launch {
            val queryMac = mac.trim()
            val found = repository.findByMac(queryMac)
                ?: allCustomers.value.find { it.mac.equals(queryMac, ignoreCase = true) }

            if (found != null) {
                _activeCustomer.value = found
                navigateTo(Screen.CustomerDetail(found.id))
            } else {
                _notificationMessage.emit("Customer MAC '$queryMac' not found in database")
            }
        }
    }

    fun openManualConfig(customer: Customer) {
        _activeCustomer.value = customer
        _configState.value = RouterConfigState(
            configType = customer.configType.ifBlank { "PPPoE" },
            routerModel = customer.routerModel.ifBlank { "GX Earth 1000 E" },
            pppoeUsername = customer.pppoeUsername,
            pppoePassword = customer.pppoePassword,
            vlanId = customer.vlanId.ifBlank { "100" },
            ipAddress = customer.ipAddress,
            subnet = customer.subnet.ifBlank { "255.255.255.0" },
            gateway = customer.gateway,
            detectionNotes = ""
        )
        navigateTo(Screen.RouterConfig(customer.id))
    }

    fun startAutoDetection(customer: Customer) {
        _activeCustomer.value = customer
        navigateTo(Screen.Detecting(customer.id))

        viewModelScope.launch {
            _detectionState.value = DetectionState(
                isRunning = true,
                currentStep = 1,
                currentMessage = "Probing ONT via OMCI & TR-069 port 7547...",
                logs = listOf("Initiating hardware probe on ${customer.mac}...")
            )
            delay(500)

            _detectionState.value = _detectionState.value.copy(
                currentStep = 2,
                currentMessage = "Analyzing Layer 2 Bridge vs Layer 3 Route mode...",
                logs = _detectionState.value.logs + "Connected to local gateway 192.168.1.1" + "Querying WAN Service configurations..."
            )
            delay(600)

            val isBridged = (customer.id % 2L == 1L) || (System.currentTimeMillis() % 2L == 0L)
            val detectedModel = if (isBridged) "TP-Link Archer C80" else "GX Earth 1000 E"
            val detectionNote = if (isBridged) {
                "ONT detected in Bridge Mode. Targeted downstream Wi-Fi Router."
            } else {
                "ONT detected in Route Mode. Targeted ONT directly."
            }

            _detectionState.value = _detectionState.value.copy(
                currentStep = 3,
                currentMessage = "Hardware identified: $detectedModel",
                logs = _detectionState.value.logs +
                        "Optical Signal Rx: ${customer.rxPowerDbm} dBm, Tx: ${customer.txPowerDbm} dBm" +
                        "Mode detected: ${if (isBridged) "Bridge Mode" else "Route Mode"}" +
                        "Auto-detect complete!"
            )
            delay(500)

            _configState.value = RouterConfigState(
                configType = customer.configType.ifBlank { "PPPoE" },
                routerModel = detectedModel,
                pppoeUsername = customer.pppoeUsername,
                pppoePassword = customer.pppoePassword,
                vlanId = customer.vlanId.ifBlank { "100" },
                ipAddress = customer.ipAddress,
                subnet = customer.subnet.ifBlank { "255.255.255.0" },
                gateway = customer.gateway,
                detectionNotes = detectionNote
            )
            _detectionState.value = DetectionState(isRunning = false)
            navigateTo(Screen.RouterConfig(customer.id))
        }
    }

    fun updateConfigState(updater: (RouterConfigState) -> RouterConfigState) {
        _configState.value = updater(_configState.value)
    }

    fun pushConfigToAcs(customer: Customer) {
        val currentCfg = _configState.value
        viewModelScope.launch {
            _configState.value = currentCfg.copy(isPushing = true)
            delay(800) // Realistic ACS push latency

            val updatedCustomer = customer.copy(
                status = "Online",
                configType = currentCfg.configType,
                routerModel = currentCfg.routerModel,
                pppoeUsername = currentCfg.pppoeUsername,
                pppoePassword = currentCfg.pppoePassword,
                vlanId = currentCfg.vlanId,
                ipAddress = currentCfg.ipAddress,
                subnet = currentCfg.subnet,
                gateway = currentCfg.gateway,
                detectionNotes = currentCfg.detectionNotes,
                lastConfiguredAt = System.currentTimeMillis()
            )
            repository.update(updatedCustomer)
            _activeCustomer.value = updatedCustomer
            _configState.value = currentCfg.copy(isPushing = false)

            _notificationMessage.emit("Success: Pushed ${currentCfg.configType} to ${currentCfg.routerModel} for ${customer.mac}")
            navigateTo(Screen.CustomerDetail(customer.id))
        }
    }

    fun updateCustomerStatus(customer: Customer, newStatus: String) {
        viewModelScope.launch {
            val updated = customer.copy(status = newStatus)
            repository.update(updated)
            _activeCustomer.value = updated
            _notificationMessage.emit("Status updated to $newStatus")
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.delete(customer)
            _activeCustomer.value = null
            _notificationMessage.emit("Customer ${customer.name} deleted")
            navigateTo(Screen.Dashboard)
        }
    }

    fun toggleAcsMode() {
        val current = _acsSettings.value
        _acsSettings.value = current.copy(
            isLocalMode = !current.isLocalMode
        )
    }

    fun toggleAcsConnection() {
        val current = _acsSettings.value
        _acsSettings.value = current.copy(
            isAcsOnline = !current.isAcsOnline
        )
    }

    fun saveAcsSettings(url: String, username: String, secret: String) {
        _acsSettings.value = _acsSettings.value.copy(
            serverUrl = url,
            authUsername = username,
            authSecret = secret
        )
    }
}
