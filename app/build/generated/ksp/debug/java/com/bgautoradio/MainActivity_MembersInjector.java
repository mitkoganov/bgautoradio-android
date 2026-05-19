package com.bgautoradio;

import com.bgautoradio.data.preferences.AppPreferences;
import com.bgautoradio.data.repository.SpotifyRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<AppPreferences> prefsProvider;

  private final Provider<SpotifyRepository> spotifyRepositoryProvider;

  public MainActivity_MembersInjector(Provider<AppPreferences> prefsProvider,
      Provider<SpotifyRepository> spotifyRepositoryProvider) {
    this.prefsProvider = prefsProvider;
    this.spotifyRepositoryProvider = spotifyRepositoryProvider;
  }

  public static MembersInjector<MainActivity> create(Provider<AppPreferences> prefsProvider,
      Provider<SpotifyRepository> spotifyRepositoryProvider) {
    return new MainActivity_MembersInjector(prefsProvider, spotifyRepositoryProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPrefs(instance, prefsProvider.get());
    injectSpotifyRepository(instance, spotifyRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.bgautoradio.MainActivity.prefs")
  public static void injectPrefs(MainActivity instance, AppPreferences prefs) {
    instance.prefs = prefs;
  }

  @InjectedFieldSignature("com.bgautoradio.MainActivity.spotifyRepository")
  public static void injectSpotifyRepository(MainActivity instance,
      SpotifyRepository spotifyRepository) {
    instance.spotifyRepository = spotifyRepository;
  }
}
