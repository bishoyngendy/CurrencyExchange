package com.dolarapp.currencyexchange.feature.currency.impl.data.repository;

import com.dolarapp.currencyexchange.feature.currency.impl.data.api.CurrencyApi;
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
public final class CurrencyRepositoryImpl_Factory implements Factory<CurrencyRepositoryImpl> {
  private final Provider<CurrencyApi> currencyApiProvider;

  public CurrencyRepositoryImpl_Factory(Provider<CurrencyApi> currencyApiProvider) {
    this.currencyApiProvider = currencyApiProvider;
  }

  @Override
  public CurrencyRepositoryImpl get() {
    return newInstance(currencyApiProvider.get());
  }

  public static CurrencyRepositoryImpl_Factory create(Provider<CurrencyApi> currencyApiProvider) {
    return new CurrencyRepositoryImpl_Factory(currencyApiProvider);
  }

  public static CurrencyRepositoryImpl newInstance(CurrencyApi currencyApi) {
    return new CurrencyRepositoryImpl(currencyApi);
  }
}
