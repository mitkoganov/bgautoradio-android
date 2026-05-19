package com.bgautoradio.ui;

import com.bgautoradio.data.preferences.AppPreferences;
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
public final class FontScaleViewModel_Factory implements Factory<FontScaleViewModel> {
  private final Provider<AppPreferences> prefsProvider;

  public FontScaleViewModel_Factory(Provider<AppPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public FontScaleViewModel get() {
    return newInstance(prefsProvider.get());
  }

  public static FontScaleViewModel_Factory create(Provider<AppPreferences> prefsProvider) {
    return new FontScaleViewModel_Factory(prefsProvider);
  }

  public static FontScaleViewModel newInstance(AppPreferences prefs) {
    return new FontScaleViewModel(prefs);
  }
}
