package org.thasafarian.mynotescaretaker.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.thasafarian.mynotescaretaker.ui.MainViewModel
import org.thasafarian.mynotescaretaker.feature.home.HomeViewModel

val appModule: Module = module {
    single { MainViewModel() }
    factory { HomeViewModel() }
}
