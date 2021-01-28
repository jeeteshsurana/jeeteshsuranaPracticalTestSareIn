package com.example.basicstructurecoroutine.core.di

import android.content.Context
import com.example.basicstructurecoroutine.connection.HeaderInterceptor
import com.example.basicstructurecoroutine.connection.NetworkInterceptor
import com.example.basicstructurecoroutine.connection.RetrofitInterface
import com.example.basicstructurecoroutine.core.feature.repository.DashboardRepository
import com.example.basicstructurecoroutine.core.feature.viewmodel.DashboardViewModel
import com.example.basicstructurecoroutine.core.ui.BaseViewModel
import com.example.basicstructurecoroutine.core.util.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by JeeteshSurana.
 */

val appModule = module {

    // Repositories
    //shared Preference
    single {
        PreferenceManager(
            androidContext().getSharedPreferences(
                androidContext().applicationContext.packageName,
                Context.MODE_PRIVATE
            )
        )
    }
    //network call setup
    single { NetworkInterceptor(androidContext()) }
    single { HeaderInterceptor() }
    single { RetrofitInterface(androidContext(),get(),get()) }
    single { DashboardRepository(get()) }

    //ViewModels
    viewModel { BaseViewModel() }
    viewModel { DashboardViewModel(androidContext(),get()) }
}