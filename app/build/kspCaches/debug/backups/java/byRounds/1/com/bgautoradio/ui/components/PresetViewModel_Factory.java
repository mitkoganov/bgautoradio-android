package com.bgautoradio.ui.components;

import android.content.Context;
import com.bgautoradio.data.preferences.AppPreferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class PresetViewModel_Factory implements Factory<PresetViewModel> {
  private final Provider<AppPreferences> prefsProvider;

  private final Provider<Context> contextProvider;

  public PresetViewModel_Factory(Provider<AppPreferences> prefsProvider,
      Provider<Context> contextProvider) {
    this.prefsProvider = prefsProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public PresetViewModel get() {
    return newInstance(prefsProvider.get(), contextProvider.get());
  }

  public static PresetViewModel_Factory create(Provider<AppPreferences> prefsProvider,
      Provider<Context> contextProvider) {
    return new PresetViewModel_Factory(prefsProvider, contextProvider);
  }

  public static PresetViewModel newInstance(AppPreferences prefs, Context context) {
    return new PresetViewModel(prefs, context);
  }
}
