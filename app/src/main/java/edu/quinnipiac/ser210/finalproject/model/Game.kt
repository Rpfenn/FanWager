package edu.quinnipiac.ser210.finalproject.model

data class Game(
    val id: Int?,
    val name: String?,
    val description: String?,
    val marketing_name: String?,
    val short_name: String?,
    val menu_item_no: String?,
    val type: String?,
    val keywords: List<String>?,
    // Manually injected fields for UI
    val calories: Int? = null,
    val apiImageUrl: String? = null,
    val apiPrice: String? = null
){

}