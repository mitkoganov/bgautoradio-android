package com.bgautoradio.ui.screens.spotify;

import com.bgautoradio.data.repository.SpotifyRepository;
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
public final class SpotifyViewModel_Factory implements Factory<SpotifyViewModel> {
  private final Provider<SpotifyRepository> repositoryProvider;

  public SpotifyViewModel_Factory(Provider<SpotifyRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SpotifyViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static SpotifyViewModel_Factory create(Provider<SpotifyRepository> repositoryProvider) {
    return new SpotifyViewModel_Factory(repositoryProvider);
  }

  public static SpotifyViewModel newInstance(SpotifyRepository repository) {
    return new SpotifyViewModel(repository);
  }
}
