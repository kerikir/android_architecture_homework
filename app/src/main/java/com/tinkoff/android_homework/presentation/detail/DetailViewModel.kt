package com.tinkoff.android_homework.presentation.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinkoff.android_homework.data.network.repo.detail.SubscribeDetailRepository
import com.tinkoff.android_homework.presentation.model.DetailItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author d.shtaynmets
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val detailRepository: SubscribeDetailRepository
) : ViewModel() {

    private val _details: MutableStateFlow<DetailItem?> = MutableStateFlow(null)
    val details: StateFlow<DetailItem?> = _details.asStateFlow()

    //TODO нужно создать юзкейс SubscribeDetailUseCase
    // и заменить на него вызов метода интерфейса репозитория

    init {
        viewModelScope.launch {
            val detail = detailRepository.getDetail(0)
            Log.e("TAGRTRT", "detail :${detail}")
        }
    }
}
