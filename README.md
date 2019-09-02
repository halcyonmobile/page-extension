# A simplified pagination library for specific use cases

## Purpose

The purpose of this library is to simplify the pagination library implementation for specific use-cases.
Currently the supported use-case is to have an infinite list comming from network, without database caching.
In this case we need the state of the network beside the data returned meaning we have to know if the list is loading some data, or failed to load.

## Setup

The library provides a helper method to create a PagedResult model which can be returned from your repository to your consumer.
The consumer is able to use a delegate to build and publish PagedList from the PagedResult model.
For the UI a RecyclerView.Adapter is introduced which handles the loading at the end of your list.
Some helper methods are also introduced to ease the observation of the delegate.
Currently only coroutine is supported, if requested RxJava or callback based implementation can be introduced also.

### Add artifactory to your dependencies
- in your top-level build.gradle add the following setup to access halcyon libraries :
```groovy

 allprojects {
     repositories {
          /*...*/
          // For internal HalcyonMobile libraries
          maven {
               url "https://artifactory.build.halcyonmobile.com/artifactory/libs-release-local/"
               credentials {
                    username = "${artifactory_username}"
                    password = "${artifactory_password}"
               }
          }
     }
 }
```

### Setup with coroutines

This will describe the most basic setup and some common use-cases.
 - Simple list without any modification
 - Simple list with additional force-refresh
 - Paginated search
 - Paginated List that can be modified by the user.
 - Introducing additional Header items to the paginated list.

#### core module

Add the following dependency in your build.gradle file.

```groovy
implementation "com.halcyonmobile.page-extension:page-coroutine:latest-version"
```

Simple example repository how the helper function should be used when you don't need any modification of the data, you only want to show it on the ui.
```kotlin
class FooRepository(private val fooRemoteSource: FooRemoteSource) {

    fun get(coroutineScope: CoroutineScope): PagedResult<String, Foo, NetworkError> =
        createPagedResultFromRequest(
            coroutineScope = coroutineScope,
            request = { key: String, pageSize : Int -> fooRemoteSource.get(key, pageSize) },
            initialPageKey = ""
        )

}

// the signiture of the fooRemoteSource.get():
@Throws(NetworkError::class)
suspend fun get(key: String, pageSize: Int): Pair<List<Foo>, Int>

//NetworkError is your specific exception which you throw when the network didn't suceed.
```

#### app module

Add the following dependency in your build.gradle file.
```groovy
implementation "com.halcyonmobile.page-extension:pageui-coroutine:latest-version"
```

##### ViewModel
Simple example how to consume the PagedResult from the fooRepository.

```kotlin
class MainViewModel(
    private val repository: FooRepository,
    delegate : PagedListViewModelDelegate<String, Foo, NetworkError>
) : ViewModel(), PagedListViewModel<Foo, FooRemoteSource.NetworkError> by delegate {

    init {
        viewModelScope.launch{
            delegate.setupPagedListByRequest {
                repository.get(viewModelScope)
            }
        }
    }
}
```

This will make your viewModel provide two livedata defined in PagedListViewModel interface.
The data into these livedata will be provided by the delegate using the PagedResult returned by the repository.
```kotlin
val pagedListResult: LiveData<PagedList<Foo>>
val state: LiveData<DataSourceState<NetworkError>>
```

##### RecyclerView.Adapter
Simple example how to handle the PagedList with Loading indicator at the end of the list

```korlin
class FooAdapter: LoadingMorePagedListAdapter<Foo, FooAdapter.MainViewHolder>(FooDiffUtilCallback()){

    override fun getDataItemViewType(position: Int): Int = R.layout.item_foo

    override fun onCreateDataItemViewHolder(parent: ViewGroup, viewType: Int): FooViewHolder = FooViewHolder(parent)

    override fun onBindDataItemViewHolder(holder: FooViewHolder, position: Int) {
        getItem(position)?.let(holder::bind)
    }

    class FooViewHolder(parent: ViewGroup) : BindingViewHolder<ItemMainViewBinding>(parent, R.layout.item_foo){

        fun bind(foo: Foo){
            binding.foo = foo
        }
    }

    class FooDiffUtilCallback : DiffUtil.ItemCallback<Foo>(){
        override fun areItemsTheSame(oldItem: Foo, newItem: Foo): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Foo, newItem: Foo): Boolean =
            oldItem == newItem
    }
}
```

As you can see it's really similar how you implement a PagedListAdapter.
The only thing you have to consider is that do not return the same viewType as for the laoding-more-indicator or loading-more-error-indicator.
If you can't garantee this with the default values consider overriding them.
You are able to switch out them completely if you need that.


##### Activity / Fragment
Simple example how to tie together your adapter with the viewModel.
```kotlin
val adapter = FooAdapter()
binding.recyclerView.adapter = adapter
binding.recyclerView.layoutManager = LinearLayoutManager(this)
viewModel.observeList(this, adapter)
viewModel.observeInitialLoadingAndShowIndicator(this, binding.initialProgressBar)
viewModel.observeInitialErrorAndShowIndicator(this, binding.errorState) {
    binding.errorState.retryView.setOnClickListener{ it.invoke() }
}
viewModel.observeEmptyStateAndShowIndicator(this, binding.emptyState)
```

#### I did the setup steps, but I need to have force-refresh, what should I do?

You are in luck, that's a pretty common use case. You have to options:
 - You either have job for the delegate call and cancel it and recreate the whole PagedResukt / PagedList etc
 - Or you add an invadilator to the createPagedResultFromRequest function and define a fetch method in the repository which invokes that invalidator

The first option is advised when you have something like a paginated search, because in this case you need to change the request parameters also, so for this we will use the second approach.
The second aproach is advised if your request should be in the same format, because in this case only the datasource will be recreated.

##### Modifications in the repository
```kotlin
private val dataSourceInvalidator = DataSourceInvalidator<String, Foo>()

fun get(coroutineScope: CoroutineScope): PagedResult<String, Foo, NetworkError> =
        createPagedResultFromRequest(
            coroutineScope = coroutineScope,
            request = { key: String, pageSize : Int -> fooRemoteSource.get(key, pageSize) },
            initialPageKey = "",
            invalidator = dataSourceInvalidator
        )

fun fetch(){
    dataSourceInvalidator()
}
```

##### Modifications in the ViewModel
```kotlin
fun forceRefresh(){
    repository.fetch()
}
```

##### Modifications in the Activity/Fragment
```kotlin
val adapter = FooAdapter()
binding.recyclerView.adapter = adapter
binding.recyclerView.layoutManager = LinearLayoutManager(this)
viewModel.observeList(this, adapter)
viewModel.observerLoadingAndUpdateSwipeRefreshLayout(this, binding.swipeRefreshLayout)
binding.swipeRefreshLayout.setOnRefreshListener { viewModel.onForceRefresh() }
viewModel.observeInitialErrorAndShowIndicator(this, binding.errorState) {
    binding.errorState.retryView.setOnClickListener{ it.invoke() }
}
viewModel.observeEmptyStateAndShowIndicator(this, binding.emptyState)
```

And that's it, now on swipe-to-refresh you should see your data is discarded and the pages start to load again.

#### That's not at all useful, I need a paginated search, and I don't see how I can implement it with this

Very well, that's a bit more tricky, but nothing too out of the ordinary. So what should we change?
First you will need to implement your search api in your remote source with a plus argument, query, then you can modify the repository.

##### Modifications in the repository

```kotlin
fun get(coroutineScope: CoroutineScope, query: String): PagedResult<String, Foo, NetworkError> =
        createPagedResultFromRequest(
            coroutineScope = coroutineScope,
            request = { key: String, pageSize : Int -> fooRemoteSource.get(key, pageSize, query) },
            initialPageKey = "",
            invalidator = dataSourceInvalidator
        )
```

Okay now you have a paginated list list defined by a query keyword, what now?

##### Modifications in the ViewModel

You will need the first option mentioned under force-refresh

```kotlin
private var searchJob: Job? = null
private fun startRequests(query: String){
    searchJob?.cancel()
    searchJob = viewModelScope.launch{
        delegate.setupPagedListByRequest {
            repository.get(viewModelScope)
        }
    }
}
```

Now when your text change or a search button is clicked, you notify your viewModel about it and call startRequests with the query, and that's it.

#### So it works with immutable lists, but I need to delete / modify / add element to the paginated list, now what?

Now you gonna write code.
First things first, do NOT call notifyItemChanged/Deleted/Inserted on the adapter, you gonna shoot yourself in the foot with IndexOutOfBounds exceptions or inconsistent UI.
So what should you do then?

Now you gonna need a cache, which stores all the values which is stored in the PagedList also, modify this cache however you wish and then invalidate the datasource.
This way the datasource will be recreated, but it will get the values from the cache first and diffutil will call the appropiate notifyItemChanged/Deleted/Inserted calls.
Cool, how you do this?

Here is an example for Delete.

##### Modifications in core

Introducing the caching class.
```kotlin
class FooPagedMemoryCache(){
    private var cachedList: List<Foo>? = null
    private var cachedKey: String? = null

    fun get() : Pair<List<Foo>, String>?{
        val key = cachedKey ?: return null
        val list = cachedList ?: return null
        return list to key
    }

    fun clear(){
        cachedKey = null
        cachedList = null
    }

    fun cache(pageToCache: List<Foo>, keyOfPageToCache: String){
        cachedKey = keyOfPageToCache
        list = list?.plus(pageToCache) ?: pageToCache
    }

    fun deleteFoo(foo: Foo){
        cachedList = cachedList?.filterNot{ it == foo }
    }
}
```

```kotlin
class FooRepository(fooPagedMemoryCache: FooPagedMemoryCache, fooRemoteSource: FooRemoteSource){

    private val dataSourceInvalidator = DataSourceInvalidator<String, Foo>()

    fun get(coroutineScope: CoroutineScope): PagedResult<String, Foo, NetworkError> {
        fooPagedMemoryCache.clear() // this you only need if when the list is recreated you want a fetch
        createPagedResultFromRequest(
            coroutineScope = coroutineScope,
            request = { key: String, pageSize : Int -> getFromNetworkAndCacheIt(key, pageSize) },
            initialPageKey = ""
            invalidator = dataSourceInvalidator,
            cache = { key : String, pageSize: Int -> fooPagedMemoryCache.get() }
        )
    }


    private suspend fun getFromNetworkAndCacheIt(key: String, pageSize: Int) : Pair<List<Foo>, String>{
        val data = fooRemoteSource.get(key, pageSize)
        fooPagedMemoryCache.cache(data.first, data.second)
        return data
    }

    // the order is important, if the request failed, you may not want to remove the element
    // also you have to change the cache Before you invalidate
    // if you don't want to wait for the request, and reinsert if it failed, well that can be done by modifying the cache in similar fashion, but that's on you.
    @Throws(NetorkException::class)
    suspend fun deleteFoo(foo: Foo){
        fooRemoteSource.deleteFoo(foo)
        cache.deleteFoo(foo)
        dataSourceInvalidator()
    }
}
```

##### Modifications in viewModel

```kotlin

// add the following method
fun deleteFoo(foo: Foo){
    viewModelScope.launch{
        try {
            repository.deleteFoo(foo)
        } catch(networkError: NetworkError){
            // todo show snackbar or however you handle error
            return@launch
        }
        // todo show confirmation message or whatever is needed.
    }
}
```

And that's it, now you can modify / delete / add elements.

#### I don't need anything fancy, I just need to add a header to the top of the list, how can I do this?

In this case what you should do is to modify the list returned from the network, example modification in the repository

```kotlin
class FooRepository(private val fooRemoteSource: FooRemoteSource) {

    fun get(coroutineScope: CoroutineScope): PagedResult<String, Foo, NetworkError> =
        createPagedResultFromRequest(
            coroutineScope = coroutineScope,
            request = { key: String, pageSize : Int -> fooRemoteSource.get(key, pageSize) },
            initialPageKey = ""
        )

    @Throws(NetworkError::class)
    private suspend fun getDataFromNetworkAndMapIt(key: String, pageSize: Int) : Pair<List<Foo?>, String>{
        val (list, key) = fooRemoteSource.get(key, pageSize)
        return if (key.isInitial()){
            listOf<Foo?>(null).plus(list) to key
        } else {
            list to key
        }
    }
}
```

Now to represent the header a null is added add the start of the list, you may introduce a sealed class or something similar for more complex use-cases.
As you can see you are responsibil what data is paginated. The library only helps to handle the states and add a general guideline to avoid pitfalls.

## Structure and contributions

You found a common use-case which is not defined? Either write a ticket or implement it and we can add it to the library then.

### page module
That's the basic module containing the building blocks of the pagination and Interfaces so the it can be more specific.

### page-coroutine module
And extension of the page module, which implements the interfaces in a way so the user of the module can use suspend lambdas instead of interfaces.

### pageui module
Contains the interface for the delegate, the Adapter and the helper observers

### pageui-coroutine module
Contains a delegate which uses corutines

If you use RxJava, consider writing a ticket, if enough people requests it I will write a pageui-rxJava / page-rxJava module. If you don't want to wait that much, simply write your own based on the example of page-coroutine / pageui-coroutine