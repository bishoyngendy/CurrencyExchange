package com.dolarapp.currencyexchange.core.network;

import com.dolarapp.currencyexchange.core.config.AppConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class ConfigModule_ProvideAppConfigFactory implements Factory<AppConfig> {
  @Override
  public AppConfig get() {
    return provideAppConfig();
  }

  public static ConfigModule_ProvideAppConfigFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static AppConfig provideAppConfig() {
    return Preconditions.checkNotNullFromProvides(ConfigModule.INSTANCE.provideAppConfig());
  }

  private static final class InstanceHolder {
    private static final ConfigModule_ProvideAppConfigFactory INSTANCE = new ConfigModule_ProvideAppConfigFactory();
  }
}
