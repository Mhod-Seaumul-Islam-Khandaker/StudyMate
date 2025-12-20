package com.example.studymate.utils

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.auth.Auth // ✅ This is the import for v3.x

object SupabaseClient {
    private const val SUPABASE_URL = "https://drdewbueptwhxipcoaem.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_y_8D3I6fPDL5LpSmpFxQpw_VB8InwQE"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)      // ✅ Use 'Auth' module for v3.x
            install(Postgrest)
        }
    }
}