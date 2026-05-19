package com.bgautoradio.data.repository;

import android.content.Context;
import com.bgautoradio.data.preferences.AppPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class SpotifyRepository_Factory implements Factory<SpotifyRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<AppPreferences> prefsProvider;

  private final Provider<OkHttpClient> httpClientProvider;

  private final Provider<ExternalMediaRepository> externalMediaRepoProvider;

  public SpotifyRepository_Factory(Provider<Context> contextProvider,
      Provider<AppPreferences> prefsProvider, Provider<OkHttpClient> httpClientProvider,
      Provider<ExternalMediaRepository> externalMediaRepoProvider) {
    this.contextProvider = contextProvider;
    this.prefsProvider = prefsProvider;
    this.httpClientProvider = httpClientProvider;
    this.externalMediaRepoProvider = externalMediaRepoProvider;
  }

  @Override
  public SpotifyRepository get() {
    return newInstance(contextProvider.get(), prefsProvider.get(), httpClientProvider.get(), externalMediaRepoProvider.get());
  }

  public static SpotifyRepository_Factory create(Provider<Context> contextProvider,
      Provider<AppPreferences> prefsProvider, Provider<OkHttpClient> httpClientProvider,
      Provider<ExternalMediaRepository> externalMediaRepoProvider) {
    return new SpotifyRepository_Factory(contextProvider, prefsProvider, httpClientProvider, externalMediaRepoProvider);
  }

  public static SpotifyRepository newInstance(Context context, AppPreferences prefs,
      OkHttpClient httpClient, ExternalMediaRepository externalMediaRepo) {
    return new SpotifyRepository(context, prefs, httpClient, externalMediaRepo);
  }
}
