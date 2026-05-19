package com.bgautoradio.service;

import com.bgautoradio.data.repository.ExternalMediaRepository;
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
public final class WazeNotificationService_MembersInjector implements MembersInjector<WazeNotificationService> {
  private final Provider<ExternalMediaRepository> externalMediaRepositoryProvider;

  public WazeNotificationService_MembersInjector(
      Provider<ExternalMediaRepository> externalMediaRepositoryProvider) {
    this.externalMediaRepositoryProvider = externalMediaRepositoryProvider;
  }

  public static MembersInjector<WazeNotificationService> create(
      Provider<ExternalMediaRepository> externalMediaRepositoryProvider) {
    return new WazeNotificationService_MembersInjector(externalMediaRepositoryProvider);
  }

  @Override
  public void injectMembers(WazeNotificationService instance) {
    injectExternalMediaRepository(instance, externalMediaRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.bgautoradio.service.WazeNotificationService.externalMediaRepository")
  public static void injectExternalMediaRepository(WazeNotificationService instance,
      ExternalMediaRepository externalMediaRepository) {
    instance.externalMediaRepository = externalMediaRepository;
  }
}
