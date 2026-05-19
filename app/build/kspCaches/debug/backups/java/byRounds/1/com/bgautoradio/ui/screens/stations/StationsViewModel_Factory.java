package com.bgautoradio.ui.screens.stations;

import com.bgautoradio.data.repository.RadioRepository;
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
public final class StationsViewModel_Factory implements Factory<StationsViewModel> {
  private final Provider<RadioRepository> repositoryProvider;

  private final Provider<PlaybackManager> playbackManagerProvider;

  public StationsViewModel_Factory(Provider<RadioRepository> repositoryProvider,
      Provider<PlaybackManager> playbackManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.playbackManagerProvider = playbackManagerProvider;
  }

  @Override
  public StationsViewModel get() {
    return newInstance(repositoryProvider.get(), playbackManagerProvider.get());
  }

  public static StationsViewModel_Factory create(Provider<RadioRepository> repositoryProvider,
      Provider<PlaybackManager> playbackManagerProvider) {
    return new StationsViewModel_Factory(repositoryProvider, playbackManagerProvider);
  }

  public static StationsViewModel newInstance(RadioRepository repository,
      PlaybackManager playbackManager) {
    return new StationsViewModel(repository, playbackManager);
  }
}
