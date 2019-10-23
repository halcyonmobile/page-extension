/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */


package com.halcyonmobile.page

/**
 * Model containing the different states a [StateProvidingListDataSource] can be.
 */
sealed class DataSourceState<Error> {
    class Normal<Error> : DataSourceState<Error>()
    class Empty<Error> : DataSourceState<Error>()
    class InitialLoading<Error> : DataSourceState<Error>()
    class LoadingMore<Error> : DataSourceState<Error>()
    class EndReached<Error> : DataSourceState<Error>()
    abstract class ErrorLoadingMore<Error>(val error: Error) : DataSourceState<Error>() {
        abstract fun retry()
    }

    abstract class ErrorLoadingInitial<Error>(val error: Error) : DataSourceState<Error>() {
        abstract fun retry()
    }

    @Suppress("FunctionName")
    companion object {
        inline fun <Error> ErrorLoadingInitial(error: Error, crossinline retry: () -> Unit) = object : ErrorLoadingInitial<Error>(error) {
            override fun retry() = retry()
        }

        inline fun <Error> ErrorLoadingMore(error: Error, crossinline retry: () -> Unit) = object : ErrorLoadingMore<Error>(error) {
            override fun retry() = retry()
        }
    }
}