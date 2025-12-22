package com.dolarapp.currencyexchange.core.network;

import com.dolarapp.currencyexchange.core.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<AppConfig> appConfigProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<AppConfig> appConfigProvider) {
    this.appConfigProvider = appConfigProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(appConfigProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<AppConfig> appConfigProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(appConfigProvider);
  }

  public static OkHttpClient provideOkHttpClient(AppConfig appConfig) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(appConfig));
  }
}
