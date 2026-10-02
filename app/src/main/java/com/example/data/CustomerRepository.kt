package com.example.data

import kotlinx.coroutines.flow.Flow

class CustomerRepository(private val customerDao: CustomerDao) {
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()

    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)

    fun getCustomerById(id: Long): Flow<Customer?> = customerDao.getCustomerById(id)

    suspend fun findByMac(mac: String): Customer? = customerDao.findCustomerByMac(mac.trim())

    suspend fun insert(customer: Customer): Long = customerDao.insertCustomer(customer)

    suspend fun update(customer: Customer) = customerDao.updateCustomer(customer)

    suspend fun delete(customer: Customer) = customerDao.deleteCustomer(customer)

    suspend fun deleteById(id: Long) = customerDao.deleteCustomerById(id)

    suspend fun prepopulateIfNeeded() {
        if (customerDao.getCount() == 0) {
            val initialCustomers = listOf(
                Customer(
                    mac = "AA:BB:CC:11:22:33",
                    name = "Demo User 1",
                    status = "PON Problem",
                    phone = "9816012345",
                    configType = "PPPoE",
                    pppoeUsername = "user1@bsnl.in",
                    pppoePassword = "password123",
                    vlanId = "100",
                    routerModel = "GX Earth 1000 E",
                    detectionNotes = "High optical loss on GPON link. Check fiber drop cable / splice.",
                    rxPowerDbm = -29.8,
                    txPowerDbm = 2.1
                ),
                Customer(
                    mac = "BC:F6:85:12:44:90",
                    name = "Rajesh Sharma",
                    status = "Online",
                    phone = "9805043210",
                    configType = "PPPoE",
                    pppoeUsername = "sharma_rj@bsnl.in",
                    pppoePassword = "bsnluser#2024",
                    vlanId = "100",
                    routerModel = "Syrotech SY-GPON-1110-WDONT",
                    detectionNotes = "ONT detected in Route Mode. Healthy optical Rx level.",
                    rxPowerDbm = -18.7,
                    txPowerDbm = 2.4
                ),
                Customer(
                    mac = "80:EA:07:4A:21:88",
                    name = "Pooja Verma",
                    status = "Internet Down",
                    phone = "9816598765",
                    configType = "PPPoE",
                    pppoeUsername = "verma_p@bsnl.in",
                    pppoePassword = "pooja@fiber",
                    vlanId = "100",
                    routerModel = "TP-Link Archer C80",
                    detectionNotes = "ONT detected in Bridge Mode. Targeted downstream Wi-Fi Router. PPPoE authentication timed out.",
                    rxPowerDbm = -21.4,
                    txPowerDbm = 2.2
                ),
                Customer(
                    mac = "00:E0:4C:68:01:23",
                    name = "Vikram Patel",
                    status = "Online",
                    phone = "9418011223",
                    configType = "Static IP",
                    ipAddress = "192.168.1.150",
                    subnet = "255.255.255.0",
                    gateway = "192.168.1.1",
                    routerModel = "Netlink HG323RW / V2801SG",
                    detectionNotes = "Static IP provisioned via TR-069 ACS.",
                    rxPowerDbm = -19.4,
                    txPowerDbm = 2.5
                )
            )
            customerDao.insertAll(initialCustomers)
        }
    }
}
