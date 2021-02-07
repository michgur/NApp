package com.klmn.napp.data.network

import com.klmn.napp.data.network.entities.NetworkEntities
import com.klmn.napp.model.Filter
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import kotlin.reflect.full.declaredMemberProperties

private val fields = NetworkEntities.Product::class.declaredMemberProperties
    .map { it.name }
    .reduce { a, b -> "$a,$b" }

interface OpenFoodFactsAPI {
    @GET("/cgi/search.pl?json=1")
    suspend fun getProducts(
        @Query("search_terms") query: String,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int,
        @QueryMap options: Map<String, String>
    ): Response<NetworkEntities.Search>

    @GET("/categories?json=1")
    suspend fun getCategories(
        @Query("string") query: String
    ): Response<NetworkEntities.Search>

    object OrderBy {
        const val POPULARITY = "unique_scans_n"
        const val NAME = "product_name"
        const val DATE_CREATED = "created_t"
        const val DATE_MODIFIED = "last_modified_t"
    }

    object Criteria {
        const val BRANDS = "brands"
        const val CATEGORIES = "categories"
        const val PACKAGING = "packaging"
        const val LABELS = "labels"
        const val ORIGINS = "origins"
        const val MANUFACTURING_PLACES = "manufacturing_places"
        const val EMB_CODES = "emb_codes"
        const val PURCHASE_PLACES = "purchase_places"
        const val STORES = "stores"
        const val COUNTRIES = "countries"
        const val ADDITIVES = "additives"
        const val ALLERGENS = "allergens"
        const val TRACES = "traces"
        const val NUTRITION_GRADES = "nutrition_grades"
        const val STATE = "states"
    }

    companion object {
        fun searchOptions(
            order: String? = null,
            filters: Iterable<Filter>?
        ) = hashMapOf("fields" to fields).also { query ->
            order?.let { query["sort_by"] = order }
            filters?.forEachIndexed { i, filter ->
                query["tagtype_$i"] = filter.criterion
                query["tag_$i"] = filter.value
                query["tag_contains_$i"] = if (filter.contains) "contains" else "does_not_contain"
            }
        }
    }
}