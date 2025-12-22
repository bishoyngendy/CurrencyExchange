package com.dolarapp.currencyexchange.feature.currency.impl.di;

import com.dolarapp.currencyexchange.feature.currency.impl.data.api.CurrencyApi;
import com.dolarapp.currencyexchange.feature.currency.impl.data.repository.CurrencyRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class CurrencyModule_ProvideCurrencyRepositoryFactory implements Factory<CurrencyRepository> {
  private final Provider<CurrencyApi> currencyApiProvider;

  public CurrencyModule_ProvideCurrencyRepositoryFactory(
      Provider<CurrencyApi> currencyApiProvider) {
    this.currencyApiProvider = currencyApiProvider;
  }

  @Override
  public CurrencyRepository get() {
    return provideCurrencyRepository(currencyApiProvider.get());
  }

  public static CurrencyModule_ProvideCurrencyRepositoryFactory create(
      Provider<CurrencyApi> currencyApiProvider) {
    return new CurrencyModule_ProvideCurrencyRepositoryFactory(currencyApiProvider);
  }

  public static CurrencyRepository provideCurrencyRepository(CurrencyApi currencyApi) {
    return Preconditions.checkNotNullFromProvides(CurrencyModule.INSTANCE.provideCurrencyRepository(currencyApi));
  }
}
