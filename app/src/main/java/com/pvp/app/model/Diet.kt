package com.pvp.app.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pvp.app.R

enum class Diet(@StringRes val titleId: Int) {

    Carbohydrates(R.string.diet_carbohydrates),
    Fat(R.string.diet_fat),
    Protein(R.string.diet_protein);

    val title: String
        @Composable get() = stringResource(titleId)
}