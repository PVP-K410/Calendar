package com.pvp.app.ui.router

import MonthlyCalendarScreen
import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.pvp.app.R
import com.pvp.app.ui.screen.authentication.SignInScreen
import com.pvp.app.ui.screen.authentication.SignUpScreen
import com.pvp.app.ui.screen.calendar.CalendarScreen
import com.pvp.app.ui.screen.filter.ActivitiesFilter
import com.pvp.app.ui.screen.filter.IngredientsFilter
import com.pvp.app.ui.screen.profile.ProfileScreen
import com.pvp.app.ui.screen.steps.StepScreen
import com.pvp.app.ui.screen.survey.SurveyScreen
import kotlinx.coroutines.CoroutineScope

sealed class Route(
    val icon: ImageVector? = null,
    val iconDescription: String? = null,
    val resourceTitleId: Int,
    val route: String,
    val screen: @Composable (NavHostController, CoroutineScope) -> Unit
) {

    companion object {

        /**
         * These routes are used when user state is set to **authenticated** and all required
         * surveys are filled.
         *
         * Routes are used within [com.pvp.app.ui.screen.layout.LayoutScreenAuthenticated] layout.
         */
        val routesAuthenticated = listOf(
            ActivitiesFilter,
            Calendar,
            IngredientsFilter,
            Profile,
            Steps
        )

        /**
         * These routes are used for simple navigation drawer implementation. Routes that are
         * provided here, will be displayed in the navigation drawer. Navigation drawer is only
         * available for authenticated users, hence all routes that are under this list should
         * also be under [routesAuthenticated] list.
         *
         * Routes are used within [com.pvp.app.ui.screen.layout.LayoutScreenAuthenticated] layout.
         */
        val routesDrawer = listOf(
            ActivitiesFilter,
            Calendar,
            IngredientsFilter,
            Profile,
            Steps
        )

        /**
         * These routes are used when user state is set to **unauthenticated** or when user has
         * any surveys that are not filled yet, but **must** be.
         *
         * Routes are used within [com.pvp.app.ui.screen.layout.LayoutScreenUnauthenticated] layout.
         */
        val routesUnauthenticated = listOf(
            SignIn,
            SignUp,
            Survey
        )
    }

    data object Calendar : Route(
        icon = Icons.Outlined.CalendarMonth,
        iconDescription = "Calendar page button icon",
        resourceTitleId = R.string.route_calendar,
        route = "calendar",
        screen = { _, _ -> CalendarScreen() }
    )

    data object MonthlyCalendar : Route(
        icon = Icons.Outlined.CalendarMonth,
        iconDescription = "Calendar page button icon",
        resourceTitleId = R.string.route_calendar_monthly,
        route = "calendar/monthly",
        screen = { _, _ -> MonthlyCalendarScreen() }
    )

    data object ActivitiesFilter : Route(
        icon = Icons.Outlined.FilterList,
        iconDescription = "Activities filter edit page button icon",
        resourceTitleId = R.string.route_filters_edit_activities,
        route = "filters/edit/activities",
        screen = { _, _ -> ActivitiesFilter() }
    )

    data object IngredientsFilter : Route(
        icon = Icons.Outlined.FilterList,
        iconDescription = "Ingredients filter edit page button icon",
        resourceTitleId = R.string.route_filters_edit_ingredients,
        route = "filters/edit/ingredients",
        screen = { _, _ -> IngredientsFilter() }
    )

    data object Profile : Route(
        icon = Icons.Outlined.EditNote,
        iconDescription = "Profile page button icon",
        resourceTitleId = R.string.route_profile,
        route = "profile",
        screen = { _, _ -> ProfileScreen() }
    )

    @SuppressLint("NewApi")
    data object Steps : Route(
        icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
        iconDescription = "Step counter page button icon",
        resourceTitleId = R.string.route_steps,
        route = "steps",
        screen = { _, _ -> StepScreen() }
    )

    data object SignIn : Route(
        route = "authentication/sign-in",
        resourceTitleId = R.string.route_authentication_sign_in,
        screen = { c, s -> SignInScreen(c, s) }
    )

    data object SignUp : Route(
        route = "authentication/sign-up",
        resourceTitleId = R.string.route_authentication_sign_up,
        screen = { c, s -> SignUpScreen(c, s) }
    )

    data object Survey : Route(
        route = "survey",
        resourceTitleId = R.string.route_survey,
        screen = { _, _ -> SurveyScreen() }
    )
}