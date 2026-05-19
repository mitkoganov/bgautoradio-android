package com.bgautoradio.receiver;

import com.bgautoradio.data.preferences.AppPreferences;
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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<AppPreferences> prefsProvider;

  public BootReceiver_MembersInjector(Provider<AppPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  public static MembersInjector<BootReceiver> create(Provider<AppPreferences> prefsProvider) {
    return new BootReceiver_MembersInjector(prefsProvider);
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectPrefs(instance, prefsProvider.get());
  }

  @InjectedFieldSignature("com.bgautoradio.receiver.BootReceiver.prefs")
  public static void injectPrefs(BootReceiver instance, AppPreferences prefs) {
    instance.prefs = prefs;
  }
}
