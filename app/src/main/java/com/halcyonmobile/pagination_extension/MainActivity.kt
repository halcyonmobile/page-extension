/*
 * Copyright (c) 2019 Halcyon Mobile
 * https://www.halcyonmobile.com
 * All rights reserved.
 */
package com.halcyonmobile.pagination_extension

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.halcyonmobile.pagination_extension.databinding.ActivityMainBinding
import com.halcyonmobile.pagination_extension.simple.SimplePagedBarActivity
import com.halcyonmobile.pagination_extension.simple.SimpleRepositoryFactory
import com.halcyonmobile.pagination_extension.withitemcount.WithItemCountPagedBarActivity
import com.halcyonmobile.pagination_extension.withitemcount.WithItemCountRepositoryFactory

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.inMemoryExampleButton.setOnClickListener {
            startExampleActivity(SimpleRepositoryFactory.Type.IN_MEMORY)
        }
        binding.inDbExampleButton.setOnClickListener {
            startExampleActivity(SimpleRepositoryFactory.Type.DB)
        }
        binding.inMemoryItemCountExampleButton.setOnClickListener {
            startExampleActivity(WithItemCountRepositoryFactory.Type.IN_MEMORY)
        }
        binding.inDbItemCountExampleButton.setOnClickListener {
            startExampleActivity(WithItemCountRepositoryFactory.Type.DB)
        }
    }

    private fun startExampleActivity(type: SimpleRepositoryFactory.Type){
        startActivity(SimplePagedBarActivity.getStartActivityIntent(this, type))
    }

    private fun startExampleActivity(type: WithItemCountRepositoryFactory.Type){
        startActivity(WithItemCountPagedBarActivity.getStartActivityIntent(this, type))
    }
}