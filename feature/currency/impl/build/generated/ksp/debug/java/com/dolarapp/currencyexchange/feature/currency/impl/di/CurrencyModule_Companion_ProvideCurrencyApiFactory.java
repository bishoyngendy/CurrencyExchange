package com.dolarapp.currencyexchange.feature.currency.impl.di;

import com.dolarapp.currencyexchange.feature.currency.impl.data.api.CurrencyApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class CurrencyModule_Companion_ProvideCurrencyApiFactory implements Factory<CurrencyApi> {
  private final Provider<Retrofit> retrofitProvider;

  public CurrencyModule_Companion_ProvideCurrencyApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CurrencyApi get() {
    return provideCurrencyApi(retrofitProvider.get());
  }

  public static CurrencyModule_Companion_ProvideCurrencyApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new CurrencyModule_Companion_ProvideCurrencyApiFactory(retrofitProvider);
  }

  public static CurrencyApi provideCurrencyApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(CurrencyModule.Companion.provideCurrencyApi(retrofit));
  }
}
