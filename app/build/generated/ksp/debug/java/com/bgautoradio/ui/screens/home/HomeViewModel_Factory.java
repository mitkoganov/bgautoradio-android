package com.bgautoradio.ui.screens.home;

import com.bgautoradio.data.preferences.AppPreferences;
import com.bgautoradio.data.repository.ExternalMediaRepository;
import com.bgautoradio.data.repository.RadioRepository;
import com.bgautoradio.data.repository.SpotifyRepository;
import com.bgautoradio.playback.PlaybackManager;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<RadioRepository> repositoryProvider;

  private final Provider<PlaybackManager> playbackManagerProvider;

  private final Provider<AppPreferences> prefsProvider;

  private final Provider<ExternalMediaRepository> externalMediaRepositoryProvider;

  private final Provider<SpotifyRepository> spotifyRepositoryProvider;

  public HomeViewModel_Factory(Provider<RadioRepository> repositoryProvider,
      Provider<PlaybackManager> playbackManagerProvider, Provider<AppPreferences> prefsProvider,
      Provider<ExternalMediaRepository> externalMediaRepositoryProvider,
      Provider<SpotifyRepository> spotifyRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.playbackManagerProvider = playbackManagerProvider;
    this.prefsProvider = prefsProvider;
    this.externalMediaRepositoryProvider = externalMediaRepositoryProvider;
    this.spotifyRepositoryProvider = spotifyRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), playbackManagerProvider.get(), prefsProvider.get(), externalMediaRepositoryProvider.get(), spotifyRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<RadioRepository> repositoryProvider,
      Provider<PlaybackManager> playbackManagerProvider, Provider<AppPreferences> prefsProvider,
      Provider<ExternalMediaRepository> externalMediaRepositoryProvider,
      Provider<SpotifyRepository> spotifyRepositoryProvider) {
    return new HomeViewModel_Factory(repositoryProvider, playbackManagerProvider, prefsProvider, externalMediaRepositoryProvider, spotifyRepositoryProvider);
  }

  public static HomeViewModel newInstance(RadioRepository repository,
      PlaybackManager playbackManager, AppPreferences prefs,
      ExternalMediaRepository externalMediaRepository, SpotifyRepository spotifyRepository) {
    return new HomeViewModel(repository, playbackManager, prefs, externalMediaRepository, spotifyRepository);
  }
}
