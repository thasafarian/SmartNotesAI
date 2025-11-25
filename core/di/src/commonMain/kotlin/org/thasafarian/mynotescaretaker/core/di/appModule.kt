package org.thasafarian.mynotescaretaker.core.di

import org.koin.dsl.module
import org.thasafarian.mynotescaretaker.core.data.TaskRepository

val appModule = module {
    single { TaskRepository() }
}

