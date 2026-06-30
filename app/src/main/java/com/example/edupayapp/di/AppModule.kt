package com.example.edupayapp.di

import com.example.edupayapp.Supabase

import io.github.jan.supabase.SupabaseClient

object AppModule {
    fun provideSupabaseClient(): SupabaseClient = Supabase.client
}