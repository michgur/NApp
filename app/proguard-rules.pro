# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.klmn.napp.model.*
-keep class com.klmn.napp.data.cache.entities.CacheEntities$Product
-keep class com.klmn.napp.data.cache.entities.CacheEntities$Category
-keep class com.klmn.napp.data.cache.entities.CacheEntities$Label
-keep class com.klmn.napp.data.cache.entities.CacheEntities$LabeledProduct
-keep class com.klmn.napp.data.cache.entities.CacheEntities$Nutrient
-keep class com.klmn.napp.data.cache.DAO
-keep class com.klmn.napp.data.cache.DAO_Impl
-keep class com.klmn.napp.data.cache.Database
-keep class com.klmn.napp.data.cache.Database_Impl
-keep class com.klmn.napp.data.network.entities.NetworkEntities$Nutriments
-keep class com.klmn.napp.data.network.entities.NetworkEntities$PixabayImage
-keep class com.klmn.napp.data.network.entities.NetworkEntities$PixabaySearch
-keep class com.klmn.napp.data.network.entities.NetworkEntities$Product
-keep class com.klmn.napp.data.network.entities.NetworkEntities$ProductWrapper
-keep class com.klmn.napp.data.network.entities.NetworkEntities$Search
-keep class com.klmn.napp.data.network.entities.ProductNetworkMapper
-keep class com.klmn.napp.data.network.OpenFoodFactsAPI
-keep class com.klmn.napp.data.network.OpenFoodFactsAPI$Criteria
-keep class com.klmn.napp.data.network.OpenFoodFactsAPI$OrderBy
-keep class com.klmn.napp.data.network.PixabayAPI
-keep class com.klmn.napp.data.Repository
-keep class com.klmn.napp.data.RepositoryImpl
-keep class com.klmn.napp.di.NAppModule

-dontobfuscate