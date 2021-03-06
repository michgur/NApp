package com.klmn.napp.data.network.entities

object NetworkEntities {
    data class Product(
        val id: String? = null,
        val product_name: String? = null,
        val quantity: String? = null,
        val ingredients_analysis_tags: List<String>? = null,
        val image_url: String = "",
        val nutriments: Nutriments? = null,
        @ProductLabel val brands: String? = null,
        @ProductLabel val categories: String? = null,
        @ProductLabel val packaging: String? = null,
        @ProductLabel val labels: String? = null,
        @ProductLabel val origins: String? = null,
        @ProductLabel val manufacturing_places: String? = null,
        @ProductLabel val emb_codes: String? = null,
        @ProductLabel val purchase_places: String? = null,
        @ProductLabel val stores: String? = null,
        @ProductLabel val countries: String? = null,
        @ProductLabel val additives: String? = null,
        @ProductLabel val allergens: String? = null,
        @ProductLabel val traces: String? = null,
        @ProductLabel val nutrition_grades: String? = null,
        @ProductLabel val states: String? = null
    )

    data class Nutriments(
        val energy: String = "0",
        val energy_unit: String = "kcal",
        val carbohydrates_100g: String = "0",
        val carbohydrates_unit: String = "g",
        val proteins_100g: String = "0",
        val proteins_unit: String = "g",
        val fat_100g: String = "0",
        val fat_unit: String = "g",
        val casein: String? = null,
        val serum_proteins: String? = null,
        val nucleotides: String? = null,
        val sugars: String? = null,
        val sucrose: String? = null,
        val glucose: String? = null,
        val fructose: String? = null,
        val lactose: String? = null,
        val maltose: String? = null,
        val maltodextrins: String? = null,
        val starch: String? = null,
        val polyols: String? = null,
        val saturated_fat: String? = null,
        val butyric_acid: String? = null,
        val caproic_acid: String? = null,
        val caprylic_acid: String? = null,
        val capric_acid: String? = null,
        val lauric_acid: String? = null,
        val myristic_acid: String? = null,
        val palmitic_acid: String? = null,
        val stearic_acid: String? = null,
        val arachidic_acid: String? = null,
        val behenic_acid: String? = null,
        val lignoceric_acid: String? = null,
        val cerotic_acid: String? = null,
        val montanic_acid: String? = null,
        val melissic_acid: String? = null,
        val monounsaturated_fat: String? = null,
        val polyunsaturated_fat: String? = null,
        val omega_3_fat: String? = null,
        val alpha_linolenic_acid: String? = null,
        val eicosapentaenoic_acid: String? = null,
        val docosahexaenoic_acid: String? = null,
        val omega_6_fat: String? = null,
        val linoleic_acid: String? = null,
        val arachidonic_acid: String? = null,
        val gamma_linolenic_acid: String? = null,
        val dihomo_gamma_linolenic_acid: String? = null,
        val omega_9_fat: String? = null,
        val oleic_acid: String? = null,
        val elaidic_acid: String? = null,
        val gondoic_acid: String? = null,
        val mead_acid: String? = null,
        val erucic_acid: String? = null,
        val nervonic_acid: String? = null,
        val trans_fat: String? = null,
        val cholesterol: String? = null,
        val fiber: String? = null,
        val sodium: String? = null,
        val alcohol: String? = null,
        val vitamin_a: String? = null,
        val vitamin_d: String? = null,
        val vitamin_e: String? = null,
        val vitamin_k: String? = null,
        val vitamin_c: String? = null,
        val vitamin_b1: String? = null,
        val vitamin_b2: String? = null,
        val vitamin_pp: String? = null,
        val vitamin_b6: String? = null,
        val vitamin_b9: String? = null,
        val vitamin_b12: String? = null,
        val biotin: String? = null,
        val pantothenic_acid: String? = null,
        val silica: String? = null,
        val bicarbonate: String? = null,
        val potassium: String? = null,
        val chloride: String? = null,
        val calcium: String? = null,
        val phosphorus: String? = null,
        val iron: String? = null,
        val magnesium: String? = null,
        val zinc: String? = null,
        val copper: String? = null,
        val manganese: String? = null,
        val fluoride: String? = null,
        val selenium: String? = null,
        val chromium: String? = null,
        val molybdenum: String? = null,
        val iodine: String? = null,
        val caffeine: String? = null,
        val taurine: String? = null
    )

    /* a wrapper class for single product responses */
    data class ProductWrapper(
        val product: Product? = null,
        val status: Int = 0
    )

    /* a wrapper class for 0 or more product search results */
    data class Search(
        val page: Int = 0,
        val page_size: Int = 0,
        val page_count: Int = 0,
        val products: List<Product> = listOf()
    )

    data class PixabaySearch(
        val hits: List<PixabayImage>
    )

    data class PixabayImage(
        val webformatURL: String
    )
}