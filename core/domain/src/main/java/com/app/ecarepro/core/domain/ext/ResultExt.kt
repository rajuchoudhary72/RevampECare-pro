package com.app.ecarepro.core.domain.ext

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


/**
 * A generic helper that wraps a suspend block in a Flow<Result<T>> with standard error handling.
 */
inline fun <T> asResultFlow(crossinline block: suspend () -> T): Flow<Result<T>> = flow {
    emit(Result.success(block()))
}.catch { exception ->
    emit(Result.failure(exception))
}
