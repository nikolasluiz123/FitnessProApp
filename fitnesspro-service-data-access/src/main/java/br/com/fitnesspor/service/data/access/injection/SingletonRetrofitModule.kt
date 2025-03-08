package br.com.fitnesspor.service.data.access.injection

import br.com.fitnesspro.core.adapters.LocalDateTimeTypeAdapter
import br.com.fitnesspro.core.adapters.LocalDateTypeAdapter
import br.com.fitnesspro.core.adapters.LocalTimeTypeAdapter
import br.com.fitnesspro.shared.communication.constants.Timeouts
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonRetrofitModule {

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
            .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
            .create()

        return Retrofit
            .Builder()
            .baseUrl("http://192.168.0.41:8082/api/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Provides
    @Singleton
    fun provideHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .callTimeout(Timeouts.OPERATION_HIGH_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(Timeouts.CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Timeouts.OPERATION_MEDIUM_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Timeouts.OPERATION_HIGH_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }
}