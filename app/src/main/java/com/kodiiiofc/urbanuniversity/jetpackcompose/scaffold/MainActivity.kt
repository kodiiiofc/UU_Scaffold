package com.kodiiiofc.urbanuniversity.jetpackcompose.scaffold

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kodiiiofc.urbanuniversity.jetpackcompose.scaffold.ui.theme.ScaffoldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScaffoldTheme {
                Screen()
            }
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun Screen() {

        var currentItem by rememberSaveable() {
            mutableStateOf<Int?>(null)
        }

        val inputField = rememberSaveable() {
            mutableStateOf<String>("")
        }

        val contactList = rememberSaveable(
            saver = listSaver(
                save = {
                    if (it.isNotEmpty()) it.toList()
                    else listOf("")
                },
                restore = { restored ->
                    val list = mutableStateListOf<String>()
                    restored.forEach {
                        list.add(it)
                    }
                    list
                }
            )
        ) {
            mutableStateListOf<String>(
            )
        }

        Scaffold(
            topBar = { TopBar(currentItem, contactList) },
            bottomBar = { BottomBar(currentItem, contactList, inputField) }
        )
        { innerPaddings ->
            Column(
                Modifier
                    .padding(innerPaddings)
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(contactList) { index, item ->
                        Item(
                            item,
                            onDeleteIconClick = {
                                contactList.removeAt(index)
                                currentItem = null
                            },
                            onItemClick = {
                                currentItem = index
                            },
                            selected = currentItem == index
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                OutlinedTextField(
                    value = inputField.value,
                    onValueChange = { inputField.value = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun TopBar(
        currentItem: Int?,
        contactList: SnapshotStateList<String>,
    ) {
        TopAppBar(
            title = {
                Text(text = stringResource(R.string.app_name))
            },
            navigationIcon = {
                NavigationIcon()
            },
            actions = {
                IconButton(onClick = {

                    val callText = if (currentItem != null)
                        "Звонок совершен ${contactList[currentItem]}"
                    else "Выберите контакт"

                    Toast.makeText(
                        this@MainActivity,
                        callText,
                        Toast.LENGTH_LONG
                    ).show()

                }) {
                    Icon(Icons.Filled.Call, "Вызов")
                }

                IconButton(onClick = {
                    finishAffinity()
                }) {
                    Icon(Icons.Filled.Close, "Выход")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
    }

    @Composable
    private fun BottomBar(
        currentItem: Int?,
        contactList: SnapshotStateList<String>,
        inputField: MutableState<String>
    ) {
        BottomAppBar(
            actions = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            val sendText = if (currentItem != null)
                                "Сообщение отправлено ${contactList[currentItem!!]}"
                            else "Выберите контакт"

                            Toast.makeText(
                                this@MainActivity,
                                sendText,
                                Toast.LENGTH_LONG
                            ).show()
                        }) {
                        Icon(Icons.Filled.Send, "Отправить")
                    }
                    IconButton(
                        onClick = {
                            val editedText = if (currentItem != null) {
                                contactList[currentItem!!] = inputField.value
                                inputField.value = ""
                                "Контакт ${contactList[currentItem!!]} отредактирован"
                            } else "Выберите контакт"

                            Toast.makeText(
                                this@MainActivity,
                                editedText,
                                Toast.LENGTH_LONG
                            ).show()
                        }) {
                        Icon(Icons.Filled.Edit, "Редактировать")
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        contactList.add(inputField.value)
                        inputField.value = ""
                    }
                ) {
                    Icon(Icons.Filled.Add, "Добавить контакт")
                }

            }
        )
    }

    @Composable
    private fun NavigationIcon() {
        IconButton(
            onClick = {
                Toast.makeText(
                    this@MainActivity,
                    "Боковое меню",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        { Icon(Icons.Default.Menu, "Боковое меню") }
    }
}

@Composable
fun Item(
    item: String,
    onDeleteIconClick: () -> Unit,
    onItemClick: () -> Unit,
    selected: Boolean
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.White)
            .padding(16.dp, 0.dp)
            .clickable {
                onItemClick()
            }
    ) {
        Text(
            item, fontSize = 16.sp,
            color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else Color.Black
        )
        Spacer(Modifier.width(8.dp))
        IconButton(onDeleteIconClick) {
            Icon(
                Icons.Filled.Delete,
                "Удалить",
                tint = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else Color.Black
            )
        }
    }

}