package com.bgautoradio.ui.screens.settings;

import com.bgautoradio.data.preferences.AppPreferences;
import com.bgautoradio.data.repository.RadioRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<AppPreferences> prefsProvider;

  private final Provider<RadioRepository> repositoryProvider;

  public SettingsViewModel_Factory(Provider<AppPreferences> prefsProvider,
      Provider<RadioRepository> repositoryProvider) {
    this.prefsProvider = prefsProvider;
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(prefsProvider.get(), repositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<AppPreferences> prefsProvider,
      Provider<RadioRepository> repositoryProvider) {
    return new SettingsViewModel_Factory(prefsProvider, repositoryProvider);
  }

  public static SettingsViewModel newInstance(AppPreferences prefs, RadioRepository repository) {
    return new SettingsViewModel(prefs, repository);
  }
}
