package com.example.edupayapp.di

import android.content.Context
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.session.SharedPreferencesSessionManager
import io.github.jan.supabase.postgrest.Postgrest

object AppModule {

    lateinit var supabase: io.github.jan.supabase.SupabaseClient
        private set

    fun initSupabase(context: Context) {
        supabase = createSupabaseClient(
            supabaseUrl = "https://erkqutbpbyqtnzasmdhj.supabase.co",
            supabaseKey = "your_anon_key_here"
        ) {
            install(Auth) {
                sessionManager = SharedPreferencesSessionManager(context)
            }
            install(Postgrest)
        }
    }
}
