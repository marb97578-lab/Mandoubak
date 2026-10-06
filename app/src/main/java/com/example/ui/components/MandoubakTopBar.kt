package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MandoubakNavy
import com.example.ui.theme.NotificationRed

@Composable
fun MandoubakTopBar(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    hasNotifications: Boolean = true,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Button: Hamburger Menu (rounded rect)
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), clip = false)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White)
                .border(width = 1.dp, color = Color(0xFFF1F5F9), shape = RoundedCornerShape(14.dp))
                .clickable { onMenuClick() }
                .testTag("top_menu_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "القائمة الرئيسية",
                tint = MandoubakNavy,
                modifier = Modifier.size(24.dp)
            )
        }

        // Center: Brand Logo and Title
        MandoubakBrandHeader(
            modifier = Modifier.weight(1f)
        )

        // Right Action Buttons: Search and Notifications
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            // Search button (circular)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(elevation = 2.dp, shape = CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(width = 1.dp, color = Color(0xFFF1F5F9), shape = CircleShape)
                    .clickable { onSearchClick() }
                    .testTag("top_search_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "بحث",
                    tint = MandoubakNavy,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Notification button (circular with red badge)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(elevation = 2.dp, shape = CircleShape, clip = false)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(width = 1.dp, color = Color(0xFFF1F5F9), shape = CircleShape)
                    .clickable { onNotificationClick() }
                    .testTag("top_notification_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "الإشعارات",
                    tint = MandoubakNavy,
                    modifier = Modifier.size(22.dp)
                )

                if (hasNotifications) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 9.dp, end = 9.dp)
                            .size(8.dp)
                            .background(NotificationRed, CircleShape)
                    )
                }
            }
        }
    }
}
