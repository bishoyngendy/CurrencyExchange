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
public final class CurrencyRepository_Factory implements Factory<CurrencyRepository> {
  private final Provider<CurrencyApi> currencyApiProvider;

  public CurrencyRepository_Factory(Provider<CurrencyApi> currencyApiProvider) {
    this.currencyApiProvider = currencyApiProvider;
  }

  @Override
  public CurrencyRepository get() {
    return newInstance(currencyApiProvider.get());
  }

  public static CurrencyRepository_Factory create(Provider<CurrencyApi> currencyApiProvider) {
    return new CurrencyRepository_Factory(currencyApiProvider);
  }

  public static CurrencyRepository newInstance(CurrencyApi currencyApi) {
    return new CurrencyRepository(currencyApi);
  }
}
