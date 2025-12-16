package com.tinkoff.android_homework.data.network.mappers.operations

import com.tinkoff.android_homework.data.network.entities.operations.OperationsApi
import com.tinkoff.android_homework.data.storage.entities.OperationDb
import javax.inject.Inject

/**
 * @author d.shtaynmets
 */

//TODO маппер знает как про модельки domain слоя, так и про можельки data слоя,
// поэтому его лучше перенести в соответствующий пакет data слоя,
// так как domain не должен ни о чем знать

class OperationApiToDbMapper @Inject constructor() : (OperationsApi) -> List<OperationDb> {

    override fun invoke(operationsApi: OperationsApi): List<OperationDb> {
        return operationsApi.operation.mapIndexed { index, operation ->
            OperationDb(
                id = operation.id.toLong(),
                type = operation.type,
                name = operation.name,
                amount = operation.amount
            )
        }
    }
}