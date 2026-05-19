package com.bgautoradio.playback;

import android.content.Context;
import com.bgautoradio.data.preferences.AppPreferences;
import com.bgautoradio.data.repository.RadioRepository;
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
public final class PlaybackManager_Factory implements Factory<PlaybackManager> {
  private final Provider<Context> contextProvider;

  private final Provider<RadioRepository> repositoryProvider;

  private final Provider<AppPreferences> prefsProvider;

  public PlaybackManager_Factory(Provider<Context> contextProvider,
      Provider<RadioRepository> repositoryProvider, Provider<AppPreferences> prefsProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public PlaybackManager get() {
    return newInstance(contextProvider.get(), repositoryProvider.get(), prefsProvider.get());
  }

  public static PlaybackManager_Factory create(Provider<Context> contextProvider,
      Provider<RadioRepository> repositoryProvider, Provider<AppPreferences> prefsProvider) {
    return new PlaybackManager_Factory(contextProvider, repositoryProvider, prefsProvider);
  }

  public static PlaybackManager newInstance(Context context, RadioRepository repository,
      AppPreferences prefs) {
    return new PlaybackManager(context, repository, prefs);
  }
}
