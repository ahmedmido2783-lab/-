package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ai.configureAppCheck
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.data.model.SettingDetailType
import com.example.ui.navigation.QuizHomeRoute
import com.example.ui.navigation.QuizSessionRoute
import com.example.ui.navigation.QuranListRoute
import com.example.ui.navigation.RuqyahListRoute
import com.example.ui.navigation.RuqyahPlayerRoute
import com.example.ui.navigation.SettingDetailRoute
import com.example.ui.navigation.SettingsHomeRoute
import com.example.ui.navigation.SurahReaderRoute
import com.example.ui.screens.QuizHomeScreen
import com.example.ui.screens.QuizSessionScreen
import com.example.ui.screens.QuranListScreen
import com.example.ui.screens.RuqyahListScreen
import com.example.ui.screens.RuqyahPlayerScreen
import com.example.ui.screens.SettingDetailScreen
import com.example.ui.screens.SettingsHomeScreen
import com.example.ui.screens.SurahReaderScreen
import com.example.ui.theme.LocalNoorColors
import com.example.ui.theme.NoorAlQuranTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.QuizViewModel
import com.example.viewmodel.RuqyahPlayerViewModel
import com.example.viewmodel.SurahReaderViewModel

class MainActivity : ComponentActivity() {

    private val appContainer: AppContainer by lazy { AppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory(appContainer))
            val settings by mainViewModel.settings.collectAsStateWithLifecycle()

            NoorAlQuranTheme(themeMode = settings.themeMode) {
                NoorAlQuranApp(
                    container = appContainer,
                    mainViewModel = mainViewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            appContainer.release()
        }
    }
}

private data class TopLevelDestination(
    val route: Any,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

private val topLevelDestinations = listOf(
    TopLevelDestination(
        route = QuranListRoute,
        label = "القرآن",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook,
        tag = "tab_quran"
    ),
    TopLevelDestination(
        route = RuqyahListRoute,
        label = "الرقية",
        selectedIcon = Icons.Filled.Shield,
        unselectedIcon = Icons.Outlined.Shield,
        tag = "tab_ruqyah"
    ),
    TopLevelDestination(
        route = QuizHomeRoute,
        label = "المسابقة",
        selectedIcon = Icons.Filled.Quiz,
        unselectedIcon = Icons.Outlined.Quiz,
        tag = "tab_quiz"
    ),
    TopLevelDestination(
        route = SettingsHomeRoute,
        label = "الإعدادات",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        tag = "tab_settings"
    )
)

@Composable
private fun NoorAlQuranApp(
    container: AppContainer,
    mainViewModel: MainViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val settings by mainViewModel.settings.collectAsStateWithLifecycle()
    val bookmarks by mainViewModel.bookmarks.collectAsStateWithLifecycle()
    val cachedSurahCount by mainViewModel.cachedSurahCount.collectAsStateWithLifecycle()
    val downloadState by mainViewModel.downloadState.collectAsStateWithLifecycle()

    val isTopLevel = currentDestination?.let { dest ->
        dest.hasRoute<QuranListRoute>() ||
            dest.hasRoute<RuqyahListRoute>() ||
            dest.hasRoute<QuizHomeRoute>() ||
            dest.hasRoute<SettingsHomeRoute>()
    } ?: true

    fun navigateTopLevel(route: Any) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp && isTopLevel

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (isTopLevel && !useNavigationRail) {
                    Column {
                        HorizontalDivider(
                            color = LocalNoorColors.current.goldAccent.copy(alpha = 0.35f),
                            thickness = 1.dp
                        )
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp
                        ) {
                            topLevelDestinations.forEach { dest ->
                                val selected = when (dest.route) {
                                    is QuranListRoute -> currentDestination?.hasRoute<QuranListRoute>() == true
                                    is RuqyahListRoute -> currentDestination?.hasRoute<RuqyahListRoute>() == true
                                    is QuizHomeRoute -> currentDestination?.hasRoute<QuizHomeRoute>() == true
                                    is SettingsHomeRoute -> currentDestination?.hasRoute<SettingsHomeRoute>() == true
                                    else -> false
                                }
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { navigateTopLevel(dest.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                            contentDescription = dest.label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = dest.label,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    modifier = Modifier.testTag(dest.tag)
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxHeight()
                            .statusBarsPadding()
                    ) {
                        topLevelDestinations.forEach { dest ->
                            val selected = when (dest.route) {
                                is QuranListRoute -> currentDestination?.hasRoute<QuranListRoute>() == true
                                is RuqyahListRoute -> currentDestination?.hasRoute<RuqyahListRoute>() == true
                                is QuizHomeRoute -> currentDestination?.hasRoute<QuizHomeRoute>() == true
                                is SettingsHomeRoute -> currentDestination?.hasRoute<SettingsHomeRoute>() == true
                                else -> false
                            }
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateTopLevel(dest.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(text = dest.label) },
                                modifier = Modifier.testTag(dest.tag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    NavHost(
                        navController = navController,
                        startDestination = QuranListRoute
                    ) {
                        composable<QuranListRoute> {
                            QuranListScreen(
                                settings = settings,
                                bookmarks = bookmarks,
                                onOpenSurah = { surahNum, initialAyah ->
                                    navController.navigate(SurahReaderRoute(surahNum, initialAyah))
                                },
                                onSelectReciter = { reciterId ->
                                    mainViewModel.setReciter(reciterId)
                                },
                                modifier = Modifier.statusBarsPadding()
                            )
                        }

                        composable<RuqyahListRoute> {
                            RuqyahListScreen(
                                onOpenProgram = { programId ->
                                    navController.navigate(RuqyahPlayerRoute(programId))
                                },
                                modifier = Modifier.statusBarsPadding()
                            )
                        }

                        composable<QuizHomeRoute> {
                            QuizHomeScreen(
                                settings = settings,
                                onStartQuiz = { categoryId, limit ->
                                    navController.navigate(QuizSessionRoute(categoryId, limit))
                                },
                                modifier = Modifier.statusBarsPadding()
                            )
                        }

                        composable<SettingsHomeRoute> {
                            SettingsHomeScreen(
                                settings = settings,
                                cachedSurahCount = cachedSurahCount,
                                onOpenSettingDetail = { type ->
                                    navController.navigate(SettingDetailRoute(type.key))
                                },
                                modifier = Modifier.statusBarsPadding()
                            )
                        }

                        // Full-screen Surah Reader + Red Verse Highlighting
                        composable<SurahReaderRoute> { backStackEntry ->
                            val args = backStackEntry.toRoute<SurahReaderRoute>()
                            val readerViewModel: SurahReaderViewModel = viewModel(
                                key = "surah_${args.surahNumber}_${args.initialAyah}",
                                factory = SurahReaderViewModel.Factory(
                                    container = container,
                                    surahNumber = args.surahNumber,
                                    initialAyah = args.initialAyah
                                )
                            )
                            SurahReaderScreen(
                                surahNumber = args.surahNumber,
                                viewModel = readerViewModel,
                                onChangeFontSize = { mainViewModel.setFontSize(it) },
                                onChangeDisplayMode = { mainViewModel.setMushafDisplayMode(it) },
                                onToggleAutoScroll = { mainViewModel.setAutoScroll(it) },
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Full-screen Ruqyah Reader + Continuous Audio
                        composable<RuqyahPlayerRoute> { backStackEntry ->
                            val args = backStackEntry.toRoute<RuqyahPlayerRoute>()
                            val ruqyahViewModel: RuqyahPlayerViewModel = viewModel(
                                key = "ruqyah_${args.programId}",
                                factory = RuqyahPlayerViewModel.Factory(
                                    container = container,
                                    programId = args.programId
                                )
                            )
                            RuqyahPlayerScreen(
                                viewModel = ruqyahViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Full-screen Religious Quiz Arena
                        composable<QuizSessionRoute> { backStackEntry ->
                            val args = backStackEntry.toRoute<QuizSessionRoute>()
                            val quizViewModel: QuizViewModel = viewModel(
                                key = "quiz_${args.categoryId}_${args.questionLimit}",
                                factory = QuizViewModel.Factory(
                                    container = container,
                                    categoryId = args.categoryId,
                                    questionLimit = args.questionLimit
                                )
                            )
                            QuizSessionScreen(
                                viewModel = quizViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // Full-screen Settings Detail Page for EVERY Setting Option
                        composable<SettingDetailRoute> { backStackEntry ->
                            val args = backStackEntry.toRoute<SettingDetailRoute>()
                            val settingType = SettingDetailType.entries.firstOrNull { it.key == args.settingKey }
                                ?: SettingDetailType.RECITER
                            SettingDetailScreen(
                                settingType = settingType,
                                settings = settings,
                                cachedSurahCount = cachedSurahCount,
                                downloadState = downloadState,
                                onSelectReciter = { mainViewModel.setReciter(it) },
                                onPlaySample = { mainViewModel.playReciterSample(it) },
                                onChangeFontSize = { mainViewModel.setFontSize(it) },
                                onChangeDisplayMode = { mainViewModel.setMushafDisplayMode(it) },
                                onChangeThemeMode = { mainViewModel.setThemeMode(it) },
                                onToggleAutoScroll = { mainViewModel.setAutoScroll(it) },
                                onToggleRepeatAyah = { mainViewModel.setRepeatSingleAyah(it) },
                                onChangePlaybackSpeed = { mainViewModel.setPlaybackSpeed(it) },
                                onChangeQuizTime = { mainViewModel.setQuizTimeSeconds(it) },
                                onToggleQuizAutoRead = { mainViewModel.setQuizAutoRead(it) },
                                onChangeTtsRate = { mainViewModel.setTtsSpeechRate(it) },
                                onTestTtsVoice = { mainViewModel.testTtsVoice() },
                                onDownloadFullQuran = { mainViewModel.downloadFullQuran() },
                                onClearOfflineCache = { mainViewModel.clearOfflineCache() },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
