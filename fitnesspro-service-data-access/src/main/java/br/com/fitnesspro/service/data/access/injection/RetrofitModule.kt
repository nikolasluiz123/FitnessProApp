package br.com.fitnesspro.service.data.access.injection

import br.com.fitnesspro.service.data.access.adapters.LocalDateTimeTypeAdapter
import br.com.fitnesspro.service.data.access.adapters.LocalDateTypeAdapter
import br.com.fitnesspro.service.data.access.services.IUserService
import br.com.fitnesspro.service.data.access.webclients.UserWebClient
import com.google.gson.*
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
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Módulo de injeção de dependências do Retrofit
 *
 * @author Nikolas Luiz Schmitt
 */
@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    /**
     * Função para fornecer uma instância única do retrofit.
     *
     * @author Nikolas Luiz Schmitt
     */
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
            .create()

        return Retrofit
            .Builder()
            .baseUrl("http://192.168.0.41:8000/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserServices(retrofit: Retrofit): IUserService {
        return retrofit.create(IUserService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserWebClient(userService: IUserService): UserWebClient {
        return UserWebClient(userService)
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
            .callTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .build()
    }

}