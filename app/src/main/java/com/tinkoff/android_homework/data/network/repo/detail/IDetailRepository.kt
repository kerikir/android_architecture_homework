package com.tinkoff.android_homework.data.network.repo.detail

import com.tinkoff.android_homework.domain.main.entities.Detail


interface IDetailRepository {

    suspend fun getDetail(id: Int) : Detail
}