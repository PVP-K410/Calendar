package com.pvp.app.ui.screen.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.pvp.app.ui.router.Route

@Composable
fun DrawerBody(
    modifier: Modifier = Modifier,
    onClick: Route.() -> Unit,
    routes: List<Route>,
    screen: Route
) {
    LazyColumn(modifier = modifier) {
        items(routes) {
            DrawerBodyRow(
                modifier = Modifier
                    .height(48.dp)
                    .background(
                        color = if (screen.route == it.route) {
                            MaterialTheme.colorScheme.background
                        } else {
                            Color.Unspecified
                        },
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(8.dp)
                    .clickable(
                        enabled = screen.route != it.route,
                        onClick = {
                            onClick.invoke(it)
                        },
                        onClickLabel = "Navigate to ${stringResource(it.resourceTitleId)}",
                        role = Role.Button
                    ),
                route = it
            )
        }
    }
}

@Composable
fun DrawerBodyRow(
    modifier: Modifier = Modifier,
    route: Route
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        route.icon?.let {
            Icon(
                contentDescription = route.iconDescription,
                imageVector = it
            )

            Spacer(modifier = Modifier.width(16.dp))
        }

        Text(
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge,
            text = stringResource(id = route.resourceTitleId)
        )
    }
}

@Composable
fun DrawerHeader(
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                style = MaterialTheme.typography.titleLarge,
                text = "Calendar"
            )

            Text(
                style = MaterialTheme.typography.titleMedium,
                text = "Schedule Your Day"
            )
        }
    }
}

@Composable
fun DrawerScreen(
    modifier: Modifier = Modifier,
    onClick: Route.() -> Unit,
    routes: List<Route>,
    screen: Route
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        DrawerHeader(
            Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.1f)
        )

        HorizontalDivider()

        DrawerBody(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            onClick = onClick,
            routes = routes,
            screen = screen
        )
    }
}
