package com.ericho.cashcalculator.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ericho.cashcalculator.R
import com.ericho.cashcalculator.data.model.CashRecord
import com.ericho.cashcalculator.ui.components.SwipeToDeleteContainer
import com.ericho.cashcalculator.ui.theme.CashCalculatorTheme
import com.ericho.cashcalculator.util.toHKCurrencyString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRecordScreen(
    modifier: Modifier = Modifier,

    saveRecords: StateFlow<List<CashRecord>>,
    onBackClick: () -> Unit = {},
    onReverseClick: () -> Unit = {},
    onDeleteClick: (CashRecord) -> Unit = {},
    onUndoDelete: (CashRecord) -> Unit = {}
) {
    val savedRecords by saveRecords.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var deletedRecord: CashRecord? by remember { mutableStateOf(null) }

    Scaffold(modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saved Records") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onReverseClick,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 24.dp, color = Color.Gray),
                            onClick = {}
                        ))
                    {
                        Icon(
                            painterResource(id = R.drawable.ic_sort_24),
                            contentDescription = "Share"
                        )
                    }
                }
            )
        }) { innerpadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerpadding)
                .background(color = MaterialTheme.colorScheme.surfaceContainer),
            contentPadding = PaddingValues(/*horizontal = 16.dp,*/ vertical = 8.dp)
        ) {

            items(savedRecords, key = { it.id }) { record ->
                SwipeToDeleteContainer(
                    modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                    item = record,
                    onDelete = {
                        deletedRecord = record
                        onDeleteClick(record)
                        showSnackbar = true
                    }) {
                    SavedRecordListItem(record = record)
                }
            }
        }

        if (showSnackbar) {
            LaunchedEffect(Unit) {
                val snackbarResult = snackbarHostState.showSnackbar(
                    "Record deleted!",
                    duration = SnackbarDuration.Long,
                    actionLabel = "Undo"
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    deletedRecord?.let {
                        onUndoDelete(it)
                    }
                    deletedRecord = null // Reset deletedRecord
                }
                showSnackbar = false // Reset show Snack bar
            }
        }

    }
}

@Composable
fun SavedRecordListItem(modifier: Modifier = Modifier, record: CashRecord) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = Shapes().large,
        modifier = modifier.padding(vertical = 6.dp, horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Notes: ${record.totalNotes}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = record.formattedDate,
                    Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = record.total.toHKCurrencyString(),
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = getNoteDescriptionString(record.noteDescription),
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(text = "Message", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.size(0.dp))
            Text(
                text = if (record.message.isNullOrBlank()) "-" else record.message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
        }

    }
}

@Composable
fun DeleteBackground(
    modifier: Modifier = Modifier,
    swipeDismissState: SwipeToDismissBoxState
) {
    val color = if (swipeDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
        MaterialTheme.colorScheme.errorContainer
    } else Color.Transparent

    Box(
        modifier = modifier
            .fillMaxSize()
            //.padding(16.dp)
            .background(color, shape = Shapes().large),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            modifier = Modifier.minimumInteractiveComponentSize(),
            imageVector = Icons.Outlined.Delete,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer
        )
    }

}

fun getNoteDescriptionString(noteDescription: Map<Int, String>): String {
    val result = StringBuilder()
    for ((denom, count) in noteDescription) {
        if (count.isNotEmpty() && (count.toLongOrNull() ?: 0) > 0) {
            result.append("HK$${denom}x$count")

            if (denom != noteDescription.keys.last()) {
                result.append(" | ")
            }
        }
    }
    return result.toString()
}


@Preview
@Composable
private fun RecordListItemPreview() {
    CashCalculatorTheme {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            SavedRecordListItem(
                record = CashRecord(
                    id = 1,
                    total = 1600,
                    noteDescription = mapOf(1000 to "1", 500 to "1", 100 to "1"),
                    totalNotes = 3,
                    message = "1 st withdraw",
                    date = System.currentTimeMillis()
                )
            )
        }
    }
}


@Preview
@Composable
private fun SavedRecordScreenPreview() {
    CashCalculatorTheme {
        SavedRecordScreen(
            saveRecords = MutableStateFlow(
                listOf(
                    CashRecord(
                        id = 1,
                        total = 1600,
                        noteDescription = mapOf(1000 to "1", 500 to "1", 100 to "1"),
                        totalNotes = 3,
                        message = "1 st withdraw",
                        date = System.currentTimeMillis()
                    ),
                    CashRecord(
                        id = 2,
                        total = 1600,
                        noteDescription = mapOf(1000 to "2", 500 to "1", 100 to "1"),
                        totalNotes = 3,
                        message = "1 st withdraw",
                        date = System.currentTimeMillis()
                    ),
                    CashRecord(
                        id = 3,
                        total = 1600,
                        noteDescription = mapOf(1000 to "3", 500 to "1", 100 to "1"),
                        totalNotes = 3,
                        message = "1 st withdraw",
                        date = System.currentTimeMillis()
                    ),
                    CashRecord(
                        id = 4,
                        total = 1600,
                        noteDescription = mapOf(1000 to "4", 500 to "1", 100 to "1"),
                        totalNotes = 3,
                        message = "1 st withdraw",
                        date = System.currentTimeMillis()
                    ),
                    CashRecord(
                        id = 5,
                        total = 1600,
                        noteDescription = mapOf(1000 to "5", 500 to "1", 100 to "1"),
                        totalNotes = 3,
                        message = "1 st withdraw",
                        date = System.currentTimeMillis()
                    ),
                )
            )
        )
    }
}
