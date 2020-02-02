package com.bwillocean.jiveQuizTalk.arch

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bwillocean.jiveQuizTalk.MyApplication
import io.reactivex.disposables.CompositeDisposable

enum class BindTiming {
    WHEN_ON_ACTIVITY_CREATED,
    WHEN_ON_START
}

abstract class BaseView private constructor(protected val baseActivity: BaseActivity, protected val baseFragment: BaseFragment?, private val lifecycle: Lifecycle, private val bindTiming: BindTiming): FragmentManager.FragmentLifecycleCallbacks(), LifecycleObserver {
    //constructor(fragment: BaseFragment, bindTiming: BindTiming = BindTiming.WHEN_ON_START): this (fragment.activity as BaseFragmentActivity, fragment, fragment.lifecycle, bindTiming)
    constructor(activity: BaseActivity, bindTiming: BindTiming = BindTiming.WHEN_ON_START): this (activity, null, activity.lifecycle, bindTiming)

    companion object {
        const val TAG = "BaseView"
    }

    val disposables = CompositeDisposable()

    init {
        MyApplication.async(Runnable {
            /**
             * 나중에 addObserver 하더라도 기존의 onCreate 가 넘어온다.
             * 생성자가 끝난 시점이 아니기 때문에 view prepare 할때 fragment.getView 에 실패한다.
             */
            lifecycle.addObserver(this@BaseView)
        })

        /**
         * post block 에 넣으면 onActivityCreated 받을 수 없음
         */
        @Suppress("LeakingThis")
        if (baseFragment != null) {
            baseFragment.fragmentManager?.registerFragmentLifecycleCallbacks(this, false)
            baseFragment.addBaseView(this)
        } else {
            /**
             * mvvm 의 대상이 activity or Fragment 에 따라 분기
             */
            baseActivity.addBaseView(this)
        }
    }

    abstract fun bindViewModel(disposable: CompositeDisposable)
    abstract fun unbindViewModel()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected open fun onCreate() {
    }

    //fragment lifecycle
    override fun onFragmentActivityCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState)

        if(bindTiming == BindTiming.WHEN_ON_ACTIVITY_CREATED) {
            Log.v(TAG, "[baseView] bindViewModel#onActivityCreated")
            bindViewModel(disposables)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected open fun onStart() {
        if(bindTiming == BindTiming.WHEN_ON_START) {
            Log.v(TAG, "[baseView] bindViewModel#onStart")
            bindViewModel(disposables)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected open fun onPause() {
        Log.v(TAG, "[baseView] onPause")

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected open fun onStop() {
        if(bindTiming == BindTiming.WHEN_ON_START) {
            Log.v(TAG, "[baseView] unbindViewModel#onStop")
            unbindViewModel()
            disposables.clear()
        }
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        super.onFragmentViewDestroyed(fm, f)

        baseFragment?.fragmentManager?.unregisterFragmentLifecycleCallbacks(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected open fun onDestroy() {

        if(bindTiming == BindTiming.WHEN_ON_ACTIVITY_CREATED) {
            Log.v(TAG, "[baseView] unbindViewModel#onDestroyView")
            unbindViewModel()
            disposables.clear()
        }

        baseActivity.lifecycle.removeObserver(this)
        disposables.dispose()
    }
}