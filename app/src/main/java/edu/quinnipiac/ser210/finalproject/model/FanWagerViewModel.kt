package edu.quinnipiac.ser210.finalproject.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import edu.quinnipiac.ser210.finalproject.api.ApiInterface
import edu.quinnipiac.ser210.finalproject.ui.theme.ColorSchemeType

class FanWagerViewModel : ViewModel(){

    private val mcdApi = ApiInterface.create()

    private val _mcdItems = MutableLiveData<List<Game>>()
    val mcdItems: LiveData<List<Game>> = _mcdItems
    //d686fd815cmsh70abd06154772a8p11311cjsn0f323eb4688f
    //a4a83495ddmshf7e0965c9e681e9p14c029jsn3aaf07be0b5c
    private val apiKey = "d686fd815cmsh70abd06154772a8p11311cjsn0f323eb4688f"
    private val host = "mcdonald-s-products-api.p.rapidapi.com"

    private val _themeType = MutableLiveData(ColorSchemeType.LIGHT)
    val themeType: LiveData<ColorSchemeType> = _themeType

    fun setThemeType(newType: ColorSchemeType) {
        _themeType.value = newType
    }
    fun getMenuItems() {
        viewModelScope.launch {
            try {
                val menuResponse = mcdApi.getCurrentMenu(apiKey, host)

                if (menuResponse.isSuccessful) {
                    val categories = menuResponse.body()?.categories

                    val productIds = categories
                        ?.flatMap { it.products }
                        ?.distinct()
                        ?.take(10)
                        ?: emptyList()

                    val detailedItems = mutableListOf<Game>()

                    for (id in productIds) {
                        val itemResponse = mcdApi.getProductById(id, apiKey, host)
                        if (itemResponse.isSuccessful) {
                            itemResponse.body()?.let { item ->
                                val calories = item.nutrient_facts
                                    ?.firstOrNull { it.name?.contains("Calories", ignoreCase = true) == true }
                                    ?.value?.toIntOrNull()

                                val updatedItem = item.copy(
                                    calories = calories
                                    //apiImageUrl = "https://via.placeholder.com/400x200.png?text=McItem+$id"
                                )

                                detailedItems.add(updatedItem)
                            }
                        } else {
                            Log.e("REAL_API_ITEM_FAIL", " Product $id failed with code ${itemResponse.code()}")
                        }
                    }

                    Log.d("REAL_API_RESULT", " Loaded ${detailedItems.size} real items")
                    _mcdItems.value = detailedItems

                } else {
                    Log.e("REAL_API_FAIL", " Menu load failed: ${menuResponse.code()} - ${menuResponse.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("REAL_API_EXCEPTION", " ${e.message}")
            }
        }
    }
}
