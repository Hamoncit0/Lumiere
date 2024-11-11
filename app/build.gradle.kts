plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("kotlin-parcelize")
}

android {
    namespace = "com.example.lumiere"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lumiere"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true;
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("io.coil-kt:coil:2.2.2") //para cargar imagenes de una url
    //Se agregar librerias para  manejo  consumir la web Api  y manejo de json
    implementation ("com.squareup.retrofit2:retrofit:2.8.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.8.1")
    implementation ("com.google.code.gson:gson:2.8.6")
    //Esto para generar un interseptor que nos va a permitir desplegar la respuesta del  servicio en el logcat
    implementation ("com.squareup.okhttp3:okhttp:4.6.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.6.0")
    //para pasar un array a json y guardarlo en sharedPreferences
    implementation ("com.google.code.gson:gson:2.8.8")

}