package com.example.edupayapp.di

import android.content.Context
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.status.SessionSource

import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object AppModule {

    lateinit var supabase: io.github.jan.supabase.SupabaseClient
        private set

    fun initSupabase(context: Context) {
        supabase = createSupabaseClient(
            supabaseUrl = "https://erkqutbpbyqtnzasmdhj.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVya3F1dGJwYnlxdG56YXNtZGhqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjE4MTU4MTIsImV4cCI6MjA3NzM5MTgxMn0.R4p4-mesQFz2pBkETDCsQixet0Ks1MMjkpC5dZ66RVQ"
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
