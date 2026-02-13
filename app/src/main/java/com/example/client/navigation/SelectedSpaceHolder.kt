package com.example.client.navigation

import com.example.client.data.model.SpaceResponse

/**
 * Holds the parking space selected from FindParking so ParkingDetails can display it.
 * Set before navigating to ParkingDetails, read when the details screen is shown.
 */
object SelectedSpaceHolder {
    var space: SpaceResponse? = null
}
