package com.example.basicstructurecoroutine.core.feature.activity

import android.os.Bundle
import com.example.basicstructurecoroutine.R
import com.example.basicstructurecoroutine.core.feature.dashboard.DashboardFragment
import com.example.basicstructurecoroutine.core.ui.BaseActivity
import com.example.basicstructurecoroutine.core.util.addReplaceFragment

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addReplaceFragment(R.id.fl_container, DashboardFragment(), addFragment = false, false)
    }
}