package com.example.firebase

import android.app.Application
import com.example.firebase.Container.AppContainer
import com.example.firebase.Container.MahasiswaContainer

class MahasiswaApplications: Application()
{
    lateinit var container: AppContainer
    override fun onCreate()
    {
        super.onCreate()
        container = MahasiswaContainer()
    }
}