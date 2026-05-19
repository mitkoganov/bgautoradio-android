package com.bgautoradio.di;

import com.bgautoradio.data.local.RadioDatabase;
import com.bgautoradio.data.local.RadioStationDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideStationDaoFactory implements Factory<RadioStationDao> {
  private final Provider<RadioDatabase> dbProvider;

  public AppModule_ProvideStationDaoFactory(Provider<RadioDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RadioStationDao get() {
    return provideStationDao(dbProvider.get());
  }

  public static AppModule_ProvideStationDaoFactory create(Provider<RadioDatabase> dbProvider) {
    return new AppModule_ProvideStationDaoFactory(dbProvider);
  }

  public static RadioStationDao provideStationDao(RadioDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideStationDao(db));
  }
}
