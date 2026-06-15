package com.rekernel.manager.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rekernel.manager.kernel.KernelStatus
import com.rekernel.manager.ui.LocalUiMode
import com.rekernel.manager.ui.UiMode
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeScreen(
    bottomPadding: Dp = Dp.Unspecified,
    kernelStatus: KernelStatus = KernelStatus(),
    onRefresh: () -> Unit = {}
) {
    val uiMode = LocalUiMode.current
    val scrollState = rememberScrollState()

    when (uiMode) {
        UiMode.Material -> {
            MaterialHomeScreen(
                bottomPadding = bottomPadding,
                kernelStatus = kernelStatus,
                onRefresh = onRefresh,
                scrollState = scrollState
            )
        }
        UiMode.Miuix -> {
            MiuixHomeScreen(
                bottomPadding = bottomPadding,
                kernelStatus = kernelStatus,
                onRefresh = onRefresh,
                scrollState = scrollState
            )
        }
    }
}

@Composable
private fun MaterialHomeScreen(
    bottomPadding: Dp,
    kernelStatus: KernelStatus,
    onRefresh: () -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = bottomPadding)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Kernel Status",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            if (kernelStatus.isChecking) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Kernel Version",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = kernelStatus.kernelVersion.ifEmpty { "Unknown" },
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Rekernel Module",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusIcon(kernelStatus.rekernelLoaded)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (kernelStatus.rekernelLoaded) "Loaded" else "Not Loaded",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                if (kernelStatus.rekernelLoaded) {
                    Text(
                        text = "v${kernelStatus.rekernelVersion}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (kernelStatus.rekernelAuthor.isNotEmpty()) {
                        Text(
                            text = "by ${kernelStatus.rekernelAuthor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "FollKernel Module",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusIcon(kernelStatus.follkernelLoaded)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = if (kernelStatus.follkernelLoaded) "Loaded" else "Not Loaded",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = if (kernelStatus.rootAvailable) "Root Access: Available"
                    else "Root Access: Not Available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (kernelStatus.rootAvailable) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (kernelStatus.checkError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error: ${kernelStatus.checkError}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (kernelStatus.rootAvailable) "Root management is available"
            else "Install Rekernel/FollKernel to enable root access",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MiuixHomeScreen(
    bottomPadding: Dp,
    kernelStatus: KernelStatus,
    onRefresh: () -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = bottomPadding)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Kernel Status",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onBackground,
            )
            if (kernelStatus.isChecking) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = "Refresh",
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Kernel Version",
                    fontSize = 14.sp,
                    color = MiuixTheme.colorScheme.onBackgroundVariant,
                )
                Text(
                    text = kernelStatus.kernelVersion.ifEmpty { "Unknown" },
                    fontSize = 16.sp,
                    color = MiuixTheme.colorScheme.onBackground,
                )
            }
        }

        Text(
            text = "Modules",
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
            fontSize = 14.sp,
            color = MiuixTheme.colorScheme.onBackgroundVariant,
        )

        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rekernel",
                        fontSize = 16.sp,
                        color = MiuixTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = if (kernelStatus.rekernelLoaded) "Loaded" else "Not Loaded",
                        fontSize = 14.sp,
                        color = if (kernelStatus.rekernelLoaded)
                            MiuixTheme.colorScheme.primary
                        else
                            MiuixTheme.colorScheme.onBackgroundVariant,
                    )
                }
                if (kernelStatus.rekernelLoaded) {
                    Text(
                        text = "v${kernelStatus.rekernelVersion}" +
                                if (kernelStatus.rekernelAuthor.isNotEmpty()) " · ${kernelStatus.rekernelAuthor}" else "",
                        fontSize = 13.sp,
                        color = MiuixTheme.colorScheme.onBackgroundVariant,
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "FollKernel",
                        fontSize = 16.sp,
                        color = MiuixTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = if (kernelStatus.follkernelLoaded) "Loaded" else "Not Loaded",
                        fontSize = 14.sp,
                        color = if (kernelStatus.follkernelLoaded)
                            MiuixTheme.colorScheme.primary
                        else
                            MiuixTheme.colorScheme.onBackgroundVariant,
                    )
                }
                Text(
                    text = if (kernelStatus.rootAvailable) "Root: Available"
                    else "Root: Not Available",
                    fontSize = 13.sp,
                    color = if (kernelStatus.rootAvailable)
                        MiuixTheme.colorScheme.primary
                    else
                        MiuixTheme.colorScheme.onBackgroundVariant,
                )
            }
        }

        if (kernelStatus.checkError != null) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Error",
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.onBackgroundVariant,
                    )
                    Text(
                        text = kernelStatus.checkError ?: "",
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusIcon(loaded: Boolean) {
    Icon(
        imageVector = if (loaded) Icons.Outlined.CheckCircle else Icons.Outlined.Error,
        contentDescription = if (loaded) "Loaded" else "Not Loaded",
        modifier = Modifier.size(24.dp),
        tint = if (loaded) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
