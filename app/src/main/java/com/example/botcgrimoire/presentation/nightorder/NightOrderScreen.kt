package com.example.botcgrimoire.presentation.nightorder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.botcgrimoire.R
import com.example.botcgrimoire.domain.NightOrderModel

/**
 * @author Valeriy Minnulin
 */
@Composable
fun NightOrderScreen() {
    val viewModel: NightOrderViewModel = viewModel(factory = nightOrderVMFactory())
    val state = viewModel.state.collectAsState()
    val onChangeNight: (Boolean) -> Unit = remember { { viewModel.onChangeNight(it) } }
    LaunchedEffect(key1 = Unit) {
        viewModel.updateState()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NightSwitcher(state.value.isForFirstNight, onChangeNight)
        Card(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(modifier = Modifier.padding(16.dp), text = stringResource(id = R.string.travellers_night_info))
        }
        if (state.value.isForFirstNight) {
            FirstNightInfo()
        }
        state.value.currentOrder.forEach {
            NightOrderElement(it)
        }
    }
}

@Composable
private fun FirstNightInfo() {
    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                val color = MaterialTheme.colorScheme.onBackground
                Text(stringResource(id = R.string.minions_fn_info_title), fontWeight = FontWeight.Bold)
                Icon(
                    painter = painterResource(id = R.drawable.ic_question_24),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            TextWithIcons(stringResource(id = R.string.minions_fn_info_content))
        }
    }
    Card(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row {
                Text(stringResource(id = R.string.demon_fn_info_title), fontWeight = FontWeight.Bold)
                val color = MaterialTheme.colorScheme.onBackground
                Icon(
                    painter = painterResource(id = R.drawable.ic_question_24),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            TextWithIcons(stringResource(id = R.string.demon_fn_info_content))
        }
    }
}

@Composable
private fun NightOrderElement(model: NightOrderModel) {
    val alpha = if (model.isDead) 0.5f else 1f
    Card(
        modifier = Modifier
            .padding(16.dp)
            .alpha(alpha)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(id = model.role.icon), contentDescription = null, modifier = Modifier.size(36.dp))
                Text(stringResource(id = model.role.roleName), fontWeight = FontWeight.Bold)
                if (model.isConditional) {
                    val color = MaterialTheme.colorScheme.onBackground
                    Icon(
                        painter = painterResource(id = R.drawable.ic_question_24),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                if (model.isDrunk) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(stringResource(id = R.string.poisoned), fontWeight = FontWeight.Bold)
                }
            }
            TextWithIcons(stringResource(id = model.description))
        }
    }
}

@Composable
private fun NightSwitcher(isFirstNight: Boolean, onChangeNight: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        SwitcherButton(enabled = !isFirstNight, stringResource(id = R.string.first_night)) { onChangeNight(true) }
        SwitcherButton(enabled = isFirstNight, stringResource(id = R.string.other_nights)) { onChangeNight(false) }
    }
}

@Composable
private fun RowScope.SwitcherButton(enabled: Boolean, text: String, onClick: () -> Unit) {
    val background = if (!enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background
    val textColor =
        if (!enabled) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    Box(
        modifier = Modifier
            .weight(1f)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge.copy(color = textColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .align(Alignment.BottomCenter)
                .background(background)
        )
    }
}