package com.app.ecarepro.core.domain.model

/**
 * Data class to hold structured address information obtained from reverse geocoding.
 *
 * @property city The name of the city (e.g., "Mountain View"). Can be null.
 * @property state The name of the state or administrative area (e.g., "California"). Can be null.
 * @property country The name of the country (e.g., "United States"). Can be null.
 * @property postalCode The postal code (e.g., "94043"). Can be null.
 * @property fullAddress The first full address line returned by the geocoder. Can be null.
 */
data class LocationAddress(
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String,
    val fullAddress: String,
)