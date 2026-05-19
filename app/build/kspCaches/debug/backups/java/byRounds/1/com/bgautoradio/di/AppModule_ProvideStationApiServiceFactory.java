package com.bgautoradio.di;

import com.bgautoradio.data.remote.StationApiService;
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
public final class AppModule_ProvideStationApiServiceFactory implements Factory<StationApiService> {
  private final Provider<Retrofit> retrofitProvider;

  public AppModule_ProvideStationApiServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public StationApiService get() {
    return provideStationApiService(retrofitProvider.get());
  }

  public static AppModule_ProvideStationApiServiceFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvideStationApiServiceFactory(retrofitProvider);
  }

  public static StationApiService provideStationApiService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideStationApiService(retrofit));
  }
}
