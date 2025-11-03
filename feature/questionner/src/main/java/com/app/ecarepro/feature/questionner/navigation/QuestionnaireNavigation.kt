package com.app.ecarepro.feature.questionner.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.app.ecarepro.feature.questionner.QuestionnaireScreen
import com.app.ecarepro.feature.questionner.view.AnswerListScreen

const val questionnaireGraphRoute = "questionnaire_graph"
const val questionnaireRoute = "questionnaire_route"
const val answerListRoute = "answer_list_route"

fun NavController.navigateToQuestionnaireGraph(navOptions: NavOptions? = null) {
    this.navigate(questionnaireGraphRoute, navOptions)
}

fun NavController.navigateToAnswerList(qid: Int) {
    this.navigate("$answerListRoute/$qid")
}

fun NavGraphBuilder.questionnaireGraph(
    onBackClick: () -> Unit,
    navController: NavController
) {
    navigation(
        route = questionnaireGraphRoute,
        startDestination = questionnaireRoute
    ) {
        composable(route = questionnaireRoute) {
            QuestionnaireScreen(
                onBackClick = onBackClick,
                onQuestionClick = {
                    navController.navigateToAnswerList(it)
                }
            )
        }
        composable(route = "$answerListRoute/{qid}") {
            AnswerListScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
