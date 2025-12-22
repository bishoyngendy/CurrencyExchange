package com.dolarapp.currencyexchange.feature.currency.impl.presentation;

import com.dolarapp.currencyexchange.core.dispatcher.DispatcherProvider;
import com.dolarapp.currencyexchange.feature.currency.api.repository.CurrencyRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class CurrencyExchangeViewModel_Factory implements Factory<CurrencyExchangeViewModel> {
  private final Provider<DispatcherProvider> dispatcherProvider;

  private final Provider<CurrencyRepository> currencyRepositoryProvider;

  public CurrencyExchangeViewModel_Factory(Provider<DispatcherProvider> dispatcherProvider,
      Provider<CurrencyRepository> currencyRepositoryProvider) {
    this.dispatcherProvider = dispatcherProvider;
    this.currencyRepositoryProvider = currencyRepositoryProvider;
  }

  @Override
  public CurrencyExchangeViewModel get() {
    return newInstance(dispatcherProvider.get(), currencyRepositoryProvider.get());
  }

  public static CurrencyExchangeViewModel_Factory create(
      Provider<DispatcherProvider> dispatcherProvider,
      Provider<CurrencyRepository> currencyRepositoryProvider) {
    return new CurrencyExchangeViewModel_Factory(dispatcherProvider, currencyRepositoryProvider);
  }

  public static CurrencyExchangeViewModel newInstance(DispatcherProvider dispatcherProvider,
      CurrencyRepository currencyRepository) {
    return new CurrencyExchangeViewModel(dispatcherProvider, currencyRepository);
  }
}
