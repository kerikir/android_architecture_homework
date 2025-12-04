package com.tinkoff.android_homework.presentation.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room.databaseBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.tinkoff.android_homework.domain.main.mapper.operations.OperationApiToDbMapper
import com.tinkoff.android_homework.data.network.mappers.total.TotalApiToDbMapper
import com.tinkoff.android_homework.data.network.repo.operations.OperationsRepositoryImpl
import com.tinkoff.android_homework.data.network.repo.total.TotalRepositoryImpl
import com.tinkoff.android_homework.data.network.repo.utils.InternetChecker
import com.tinkoff.android_homework.data.network.services.OperationsService
import com.tinkoff.android_homework.data.network.services.TotalService
import com.tinkoff.android_homework.data.storage.database.AppDatabase
import com.tinkoff.android_homework.data.storage.database.AppDatabase.Companion.DATABASE_NAME
import com.tinkoff.android_homework.data.storage.mappers.operations.OperationsDbToDomainMapper
import com.tinkoff.android_homework.data.storage.mappers.total.TotalDbToDomainMapper
import com.tinkoff.android_homework.di.ApplicationModule.BASE_URL
import com.tinkoff.android_homework.domain.main.usecases.SubscribeOperationsUseCaseImpl
import com.tinkoff.android_homework.domain.main.usecases.SubscribeTotalUseCaseImpl
import com.tinkoff.android_homework.presentation.mappers.operations.OperationToUiItemMapper
import com.tinkoff.android_homework.presentation.model.operations.OperationItem
import com.tinkoff.android_homework.presentation.model.operations.OperationType
import com.tinkoff.android_homework.presentation.model.total.TotalItem
import dagger.Provides
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * @author d.shtaynmets
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {

    private val _operations: MutableStateFlow<List<OperationItem>> = MutableStateFlow(emptyList())
    val operations: StateFlow<List<OperationItem>> = _operations.asStateFlow()

    private val _total: MutableStateFlow<TotalItem?> = MutableStateFlow(null)
    val total: StateFlow<TotalItem?> = _total.asStateFlow()

    //TODO Нужные для вьюмодели зависимости лучше передавать через конструктор,
    // а не создавать внутри

    val uiMapper = OperationToUiItemMapper()

    private val subscribeTotalUseCase = SubscribeTotalUseCaseImpl(
        TotalRepositoryImpl(
            totalDao = db.totalDao(),
            totalService = provideRetrofit().create(TotalService::class.java),
            totalApiToDbMapper = TotalApiToDbMapper(),
            totalDbToDomainMapper = TotalDbToDomainMapper(),
            internetChecker =  internetChecker,
        )
    )
    private val subscribeOperationsUseCase = SubscribeOperationsUseCaseImpl(
        OperationsRepositoryImpl(
            operationsService = provideRetrofit().create(OperationsService::class.java),
            operationDao = db.operationDao(),
            operationsApiToDbMapper = OperationApiToDbMapper(),
            operationsDbToDomainMapper = OperationsDbToDomainMapper(),
            internetChecker =  internetChecker,
        )
    )

    init {
        viewModelScope.launch {
            _operations.value =
                subscribeOperationsUseCase
                    .invoke()
                    .operations
                    .map { uiMapper.invoke(it) }

            _total.value = subscribeTotalUseCase
                .invoke()
                .map { total ->
                    val incomes = _operations
                        .value
                        .filter { it.operationType == OperationType.INCOME }
                        .map { it.operationSum }.sum()

                    val outcomes = _operations
                        .value
                        .filter { it.operationType == OperationType.OUTCOME }
                        .map { it.operationSum }.sum()

                    val progress = (outcomes.toFloat() / incomes.toFloat()) * 100f

                    TotalItem(
                        total = total.amount,
                        income = incomes,
                        outcome = outcomes,
                        progress = progress
                    )
                }.first()
        }
    }
}
