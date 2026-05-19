package com.bgautoradio.ui.components;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class SpeedViewModel_Factory implements Factory<SpeedViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<OkHttpClient> okHttpClientProvider;

  public SpeedViewModel_Factory(Provider<Context> contextProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    this.contextProvider = contextProvider;
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public SpeedViewModel get() {
    return newInstance(contextProvider.get(), okHttpClientProvider.get());
  }

  public static SpeedViewModel_Factory create(Provider<Context> contextProvider,
      Provider<OkHttpClient> okHttpClientProvider) {
    return new SpeedViewModel_Factory(contextProvider, okHttpClientProvider);
  }

  public static SpeedViewModel newInstance(Context context, OkHttpClient okHttpClient) {
    return new SpeedViewModel(context, okHttpClient);
  }
}
