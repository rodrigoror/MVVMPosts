package br.com.rrlabs.apps.mvvmposts.ui.post

import android.arch.lifecycle.MutableLiveData
import android.telephony.euicc.DownloadableSubscription
import android.view.View
import br.com.rrlabs.apps.mvvmposts.R
import br.com.rrlabs.apps.mvvmposts.base.BaseViewModel
import br.com.rrlabs.apps.mvvmposts.model.Post
import br.com.rrlabs.apps.mvvmposts.model.PostDao
import br.com.rrlabs.apps.mvvmposts.network.PostApi
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PostListViewModel(private val postDao:PostDao):BaseViewModel() {
    @Inject
    lateinit var postApi: PostApi

    private lateinit var subscription: Disposable

    val loadingVisibility : MutableLiveData<Int> = MutableLiveData()

    val errorMessage: MutableLiveData<Int> = MutableLiveData()

    val errorClickListener = View.OnClickListener { loadPosts() }

    val postListAdapter: PostListAdapter = PostListAdapter()

    init {
        loadPosts()
    }

    private fun loadPosts() {
        //subscription = postApi.getPosts()
        subscription = Observable.fromCallable { postDao.all }
            .concatMap {
                dbPostList ->
                if (dbPostList.isEmpty())
                    postApi.getPosts().concatMap {
                        apiPostList -> postDao.insertAll(*apiPostList.toTypedArray())
                        Observable.just(apiPostList)
                    } else Observable.just(dbPostList)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrievePostListStart() }
            .doOnTerminate { onRetrievePostListFinish() }
            .subscribe(
                {result -> onRetrievePostListSuccess(result)},
                {onRetrievePostListError()}
            )
    }

    private fun onRetrievePostListError() {
        errorMessage.value = R.string.post_error
    }

    private fun onRetrievePostListSuccess(postList:List<Post>) {
        postListAdapter.updatePostList(postList)
    }

    private fun onRetrievePostListFinish() {
        loadingVisibility.value = View.GONE
    }

    private fun onRetrievePostListStart() {
        loadingVisibility.value = View.VISIBLE
        errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }
}