package com.example.client.navigation

import com.example.client.data.model.SpaceResponse

/**
 * Holds the parking space selected for details/edit.
 * Set before navigating to ParkingDetails or EditSpace.
 * canEdit: true when opened from "Your rental spaces", false from Find parking / Activities.
 */
object SelectedSpaceHolder {
    var space: SpaceResponse? = null
    var canEdit: Boolean = false
}
