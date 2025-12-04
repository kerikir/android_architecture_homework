package com.tinkoff.android_homework.domain.main.usecases

import com.tinkoff.android_homework.domain.main.entities.Detail


interface SubscribeDetailUseCase {

    suspend fun invoke() : Detail
}