package com.bgautoradio.ui.weather;

import com.bgautoradio.data.repository.WeatherRepository;
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
public final class WeatherViewModel_Factory implements Factory<WeatherViewModel> {
  private final Provider<WeatherRepository> repositoryProvider;

  public WeatherViewModel_Factory(Provider<WeatherRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public WeatherViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static WeatherViewModel_Factory create(Provider<WeatherRepository> repositoryProvider) {
    return new WeatherViewModel_Factory(repositoryProvider);
  }

  public static WeatherViewModel newInstance(WeatherRepository repository) {
    return new WeatherViewModel(repository);
  }
}
