package com.bgautoradio.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.bgautoradio.BuildConfig
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bgautoradio.ui.screens.stations.ScreenHeader
import com.bgautoradio.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel:    SettingsViewModel = hiltViewModel(),
    otaViewModel: OtaViewModel      = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title    = "Настройки",
            subtitle = "Car-Radio v${BuildConfig.VERSION_NAME} · оптимизирано за Android head units"
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingsCard(title = "Стартиране", titleEn = "Startup") {
                    SettingsToggleRow(
                        label   = "Автоматично пусни последната станция",
                        checked = state.autoPlayOnStart,
                        onToggle = viewModel::setAutoPlayOnStart
                    )
                    SettingsToggleRow(
                        label   = "Стартирай при зареждане",
                        checked = state.autoPlayOnBoot,
                        onToggle = viewModel::setAutoPlayOnBoot
                    )
                }
            }

            item {
                SettingsCard(title = "Дисплей", titleEn = "Display") {
                    SettingsToggleRow(
                        label   = "Пази екрана активен",
                        checked = state.keepScreenAwake,
                        onToggle = viewModel::setKeepScreenAwake
                    )
                    SettingsToggleRow(
                        label   = "Предупреди при мобилни данни",
                        checked = state.mobileDataWarning,
                        onToggle = viewModel::setMobileDataWarning
                    )
                    Spacer(Modifier.height(6.dp))
                    ThemeRow(current = state.themeMode, onSelect = viewModel::setThemeMode)
                }
            }

            item {
                SettingsCard(title = "Каталог", titleEn = "Catalog") {
                    Text(
                        text  = "Версия: v${state.catalogVersion}",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    if (!state.catalogUpdatedAt.isNullOrBlank()) {
                        Text(
                            text  = "Обновен: ${state.catalogUpdatedAt}",
                            color = TextTertiary,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick  = viewModel::refreshCatalog,
                        enabled  = !state.isRefreshing,
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape    = RoundedCornerShape(8.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = Accent)
                    ) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(18.dp),
                                color       = TextOnAccent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, null,
                                tint = TextOnAccent, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Обнови", color = TextOnAccent, fontSize = 14.sp)
                        }
                    }
                    state.refreshMessage?.let { msg ->
                        Text(
                            text  = msg,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (msg.startsWith("✓")) StatusOk else Danger
                            )
                        )
                    }
                }
            }

            item {
                SettingsCard(title = "За приложението", titleEn = "About") {
                    Text("Bulgarian Auto Radio", color = TextPrimary, fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold)
                    Text("v${BuildConfig.VERSION_NAME} — производствена версия", color = TextSecondary, fontSize = 12.sp)
                    Text("Оптимизирано за Android head units", color = TextTertiary, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Stream: Radio Browser API + BNR",
                        color = TextDisabled,
                        fontSize = 11.sp
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                PermissionsCard()
            }

            item(span = { GridItemSpan(2) }) {
                OtaCard(vm = otaViewModel)
            }

            item(span = { GridItemSpan(2) }) {
                SettingsCard(title = "Шрифт", titleEn = "Typography") {
                    FontSliderRow(
                        label         = "Горен панел",
                        value         = state.fontScaleTopBar,
                        onValueChange = viewModel::setFontScaleTopBar
                    )
                    FontSliderRow(
                        label         = "Страничен панел",
                        value         = state.fontScaleRail,
                        onValueChange = viewModel::setFontScaleRail
                    )
                    FontSliderRow(
                        label         = "Централен панел",
                        value         = state.fontScaleCarousel,
                        onValueChange = viewModel::setFontScaleCarousel
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title:   String,
    titleEn: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Panel)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text       = title,
                color      = TextPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text       = titleEn,
                color      = TextDisabled,
                fontSize   = 11.sp,
                letterSpacing = 1.sp
            )
        }
        HorizontalDivider(color = Border, thickness = 0.5.dp)
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    label:   String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text     = label,
            color    = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )
        Switch(
            checked         = checked,
            onCheckedChange = onToggle,
            modifier        = Modifier.height(24.dp),
            colors          = SwitchDefaults.colors(
                checkedThumbColor   = TextOnAccent,
                checkedTrackColor   = Accent,
                uncheckedTrackColor = Border2
            )
        )
    }
}

@Composable
private fun ThemeRow(current: String, onSelect: (String) -> Unit) {
    val options = listOf("auto" to "Авто", "day" to "Дневен", "night" to "Нощен")
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { (key, label) ->
            val isSelected = current == key
            FilterChip(
                selected = isSelected,
                onClick  = { onSelect(key) },
                label    = { Text(label, fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                colors   = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentGlow,
                    selectedLabelColor     = Accent,
                    containerColor         = Bg2,
                    labelColor             = TextSecondary
                )
            )
        }
    }
}

@Composable
private fun FontSliderRow(
    label:         String,
    value:         Float,
    onValueChange: (Float) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(label, color = TextSecondary, fontSize = 13.sp)
            Text(
                text       = "${(value * 100).toInt()}%",
                color      = Accent,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Slider(
            value          = value,
            onValueChange  = onValueChange,
            valueRange     = 0.7f..1.5f,
            steps          = 6,
            modifier       = Modifier.fillMaxWidth(),
            colors         = SliderDefaults.colors(
                thumbColor         = Accent,
                activeTrackColor   = Accent,
                inactiveTrackColor = Border2
            )
        )
    }
}

// ─── Permissions Card ────────────────────────────────────────────────────────

private data class PermItem(
    val label:   String,
    val granted: Boolean,
    val action:  () -> Unit,
)

@Composable
private fun PermissionsCard() {
    val ctx = LocalContext.current
    var tick by remember { mutableIntStateOf(0) }

    val items = remember(tick) {
        val pkg = ctx.packageName
        listOf(
            PermItem(
                label   = "GPS местоположение",
                granted = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) ==
                          android.content.pm.PackageManager.PERMISSION_GRANTED,
                action  = { ctx.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            ),
            PermItem(
                label   = "Известия (notifications)",
                granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                              ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) ==
                              android.content.pm.PackageManager.PERMISSION_GRANTED
                          else true,
                action  = {
                    ctx.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, pkg)
                    )
                }
            ),
            PermItem(
                label   = "Overlay (плаваща лента)",
                granted = Settings.canDrawOverlays(ctx),
                action  = {
                    ctx.startActivity(
                        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$pkg"))
                    )
                }
            ),
            PermItem(
                label   = "Waze предупреждения (Accessibility)",
                granted = isWazeAccessibilityEnabled(ctx),
                action  = { ctx.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
            ),
        )
    }

    SettingsCard(title = "Привилегии", titleEn = "Permissions") {
        items.forEach { item ->
            Row(
                modifier          = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (item.granted) Icons.Default.CheckCircle else Icons.Default.Error,
                        contentDescription = null,
                        tint     = if (item.granted) StatusOk else Danger,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(item.label, color = TextSecondary, fontSize = 13.sp)
                }
                if (!item.granted) {
                    TextButton(
                        onClick = item.action,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text("Разреши", color = Accent, fontSize = 12.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        TextButton(
            onClick = { tick++ },
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)
        ) {
            Icon(Icons.Default.Refresh, null, tint = TextDisabled, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Провери отново", color = TextDisabled, fontSize = 12.sp)
        }
    }
}

private fun isWazeAccessibilityEnabled(ctx: android.content.Context): Boolean {
    val service = "${ctx.packageName}/com.bgautoradio.service.WazeAccessibilityService"
    val enabled = Settings.Secure.getString(
        ctx.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    return enabled.split(":").any { it.equals(service, ignoreCase = true) }
}

// ─── OTA Card ────────────────────────────────────────────────────────────────

@Composable
private fun OtaCard(vm: OtaViewModel) {
    val s by vm.state.collectAsStateWithLifecycle()

    SettingsCard(title = "Обновления", titleEn = "Updates") {
        s.latestVersion?.let { ver ->
            Text(
                text     = "Последна версия: $ver",
                color    = TextSecondary,
                fontSize = 13.sp
            )
        }
        s.changelog?.takeIf { it.isNotBlank() }?.let { log ->
            Text(log, color = TextTertiary, fontSize = 12.sp)
        }

        when {
            s.readyToInstall -> {
                Button(
                    onClick  = vm::installApk,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = StatusOk)
                ) {
                    Icon(Icons.Default.SystemUpdateAlt, null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Инсталирай сега", color = Color.Black, fontSize = 14.sp)
                }
            }
            s.downloading -> {
                Text(
                    text  = "Изтегляне… ${s.progress}%",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                LinearProgressIndicator(
                    progress = { s.progress / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color    = Accent,
                    trackColor = Border2,
                )
            }
            s.updateAvailable -> {
                Text(
                    text  = "Налична е нова версия!",
                    color = StatusOk,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Button(
                    onClick  = vm::downloadAndInstall,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    Icon(Icons.Default.Download, null, tint = TextOnAccent, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Изтегли и инсталирай", color = TextOnAccent, fontSize = 14.sp)
                }
            }
            else -> {
                val noUpdate = s.latestVersion != null && !s.updateAvailable && !s.checking
                if (noUpdate) {
                    Text("Имаш последната версия.", color = TextSecondary, fontSize = 13.sp)
                }
                Button(
                    onClick  = vm::checkForUpdate,
                    enabled  = !s.checking,
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape    = RoundedCornerShape(8.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Accent)
                ) {
                    if (s.checking) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(18.dp),
                            color       = TextOnAccent,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Refresh, null, tint = TextOnAccent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Провери за обновление", color = TextOnAccent, fontSize = 14.sp)
                    }
                }
            }
        }

        s.error?.let { err ->
            Text(err, color = Danger, fontSize = 12.sp)
        }
    }
}
