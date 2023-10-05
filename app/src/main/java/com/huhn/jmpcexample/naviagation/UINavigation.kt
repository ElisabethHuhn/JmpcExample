package com.huhn.jmpcexample.naviagation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.huhn.androidarchitectureexample.BuildConfig
import com.huhn.androidarchitectureexample.R
import com.huhn.androidarchitectureexample.viewmodel.DriverViewModelImpl

/*
 * A ScreenDestination keeps together all the information needed
 * for navigation to/from the screen
 * Every screen has one of these ScreenDestinations defined for it
 * Best reference https://bignerdranch.com/blog/using-the-navigation-component-in-jetpack-compose/
 */
interface ScreenDestination {
    val route: String
    val title: Int
}
object DriverDestination : ScreenDestination {
    override val route: String
        get() = "driver_screen"
    override val title: Int
        get() = R.string.driver_title
}
object RouteDestination : ScreenDestination {
    override val route: String
        get() = "route_screen"
    override val title: Int
        get() = R.string.route_title
    const val driverIdArg = "driverId"
    val routeWithArg: String = "$route/{$driverIdArg}"
    val arguments = listOf(navArgument(driverIdArg) {type = NavType.StringType})
    fun getNavigationDriverToRoute(driverId: String) = "$route/$driverId"
}

/*
 * The NavHost is single source of truth for all screen navigation in the app
 */
@ExperimentalMaterial3Api
@Composable
fun MainNavGraph(
    driverViewModel: DriverViewModelImpl,
){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination =  DriverDestination.route
    ){
        composable(DriverDestination.route){
            //pass navigation as parameter
            DriverScreen(
                screenTitle = DriverDestination.title,
                onDriverSelect = {
                    val toDes = RouteDestination.getNavigationDriverToRoute(it)
                    navController.navigate(
                        toDes
                    )
                },
                viewModel = driverViewModel,
            )
        }

        composable(
            route = RouteDestination.routeWithArg,
            arguments = RouteDestination.arguments,
        ){ backStackEntry ->
            //pass navigation as parameter
            RouteScreen(
                screenTitle = RouteDestination.title,
                driverId = backStackEntry.arguments?.getString(
                    RouteDestination.driverIdArg
                ) ?: "",
                onBack = {
                    val toDest = DriverDestination.route
                     navController.navigate(toDest)
                },
                viewModel = driverViewModel,
            )
        }
    }
}



