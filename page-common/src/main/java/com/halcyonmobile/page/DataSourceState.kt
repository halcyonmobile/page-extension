/*
 * Copyright (c) 2020 Halcyon Mobile.
 * https://www.halcyonmobile.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.halcyonmobile.page

/**
 * Model containing the different states a [com.halcyonmobile.page.StateProvidingListDataSource] can be.
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