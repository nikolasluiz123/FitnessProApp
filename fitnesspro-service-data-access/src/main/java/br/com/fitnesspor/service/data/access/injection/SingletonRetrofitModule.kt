package br.com.fitnesspor.service.data.access.injection

import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.service.data.access.BuildConfig
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
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonRetrofitModule {

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val gson = GsonBuilder().defaultGSon()

        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.FITNESS_PRO_SERVICE_HOST)
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
            .callTimeout(Timeouts.OPERATION_VERY_LOW_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .connectTimeout(Timeouts.OPERATION_VERY_LOW_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(Timeouts.OPERATION_LOW_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(Timeouts.OPERATION_LOW_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .build()
    }
}