package com.bgautoradio.playback;

import com.bgautoradio.data.repository.RadioRepository;
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
public final class RadioPlaybackService_MembersInjector implements MembersInjector<RadioPlaybackService> {
  private final Provider<PlaybackManager> playbackManagerProvider;

  private final Provider<RadioRepository> radioRepositoryProvider;

  public RadioPlaybackService_MembersInjector(Provider<PlaybackManager> playbackManagerProvider,
      Provider<RadioRepository> radioRepositoryProvider) {
    this.playbackManagerProvider = playbackManagerProvider;
    this.radioRepositoryProvider = radioRepositoryProvider;
  }

  public static MembersInjector<RadioPlaybackService> create(
      Provider<PlaybackManager> playbackManagerProvider,
      Provider<RadioRepository> radioRepositoryProvider) {
    return new RadioPlaybackService_MembersInjector(playbackManagerProvider, radioRepositoryProvider);
  }

  @Override
  public void injectMembers(RadioPlaybackService instance) {
    injectPlaybackManager(instance, playbackManagerProvider.get());
    injectRadioRepository(instance, radioRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.bgautoradio.playback.RadioPlaybackService.playbackManager")
  public static void injectPlaybackManager(RadioPlaybackService instance,
      PlaybackManager playbackManager) {
    instance.playbackManager = playbackManager;
  }

  @InjectedFieldSignature("com.bgautoradio.playback.RadioPlaybackService.radioRepository")
  public static void injectRadioRepository(RadioPlaybackService instance,
      RadioRepository radioRepository) {
    instance.radioRepository = radioRepository;
  }
}
