package com.bgautoradio.data.repository;

import android.content.Context;
import com.bgautoradio.data.local.RadioStationDao;
import com.bgautoradio.data.preferences.AppPreferences;
import com.bgautoradio.data.remote.StationApiService;
import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class RadioRepository_Factory implements Factory<RadioRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<RadioStationDao> daoProvider;

  private final Provider<StationApiService> apiProvider;

  private final Provider<AppPreferences> prefsProvider;

  private final Provider<Gson> gsonProvider;

  public RadioRepository_Factory(Provider<Context> contextProvider,
      Provider<RadioStationDao> daoProvider, Provider<StationApiService> apiProvider,
      Provider<AppPreferences> prefsProvider, Provider<Gson> gsonProvider) {
    this.contextProvider = contextProvider;
    this.daoProvider = daoProvider;
    this.apiProvider = apiProvider;
    this.prefsProvider = prefsProvider;
    this.gsonProvider = gsonProvider;
  }

  @Override
  public RadioRepository get() {
    return newInstance(contextProvider.get(), daoProvider.get(), apiProvider.get(), prefsProvider.get(), gsonProvider.get());
  }

  public static RadioRepository_Factory create(Provider<Context> contextProvider,
      Provider<RadioStationDao> daoProvider, Provider<StationApiService> apiProvider,
      Provider<AppPreferences> prefsProvider, Provider<Gson> gsonProvider) {
    return new RadioRepository_Factory(contextProvider, daoProvider, apiProvider, prefsProvider, gsonProvider);
  }

  public static RadioRepository newInstance(Context context, RadioStationDao dao,
      StationApiService api, AppPreferences prefs, Gson gson) {
    return new RadioRepository(context, dao, api, prefs, gson);
  }
}
