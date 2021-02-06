package com.klmn.napp.data.network

import com.klmn.napp.data.network.entities.NetworkEntities
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

    enum class Criteria(private val criterion: String) {
        BRANDS("brands"),
        CATEGORIES("categories"),
        PACKAGING("packaging"),
        LABELS("labels"),
        ORIGINS("origins"),
        MANUFACTURING_PLACES("manufacturing_places"),
        EMB_CODES("emb_codes"),
        PURCHASE_PLACES("purchase_places"),
        STORES("stores"),
        COUNTRIES("countries"),
        ADDITIVES("additives"),
        ALLERGENS("allergens"),
        TRACES("traces"),
        NUTRITION_GRADES("nutrition_grades"),
        STATE("states");

        operator fun invoke(value: String, contains: Boolean = true) =
            Triple(criterion, value, contains)
    }

    companion object {
        fun searchOptions(
            order: String? = null,
            vararg criteria: Triple<String, String, Boolean>
        ) = hashMapOf("fields" to fields).also { query ->
            order?.let { query["sort_by"] = order }
            criteria.forEachIndexed { i, crit ->
                query["tagtype_$i"] = crit.first
                query["tag_$i"] = crit.second
                query["tag_contains_$i"] = if (crit.third) "contains" else "does_not_contain"
            }
        }
    }
}