package com.supervital.usersdb

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.metanitdatabase.UserViewModel
import com.example.metanitdatabase.UserViewModelFactory
import com.supervital.usersdb.ui.theme.UsersDBTheme
import kotlin.coroutines.coroutineContext
import kotlin.toString

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
        /*setContent {
            UsersDBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }*/
    }
}

@Composable
fun MainScreen() {
    val owner = LocalViewModelStoreOwner.current

    owner?.let {
        val viewModel: UserViewModel = viewModel(
            it,
            "UserViewModel",
            UserViewModelFactory(LocalContext.current.applicationContext as Application)
        )

        UsersDBTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Main(viewModel)
                }
            }
        }
    }
}

@Composable
fun Main(vm: UserViewModel) {
    val userList by vm.userList.observeAsState(listOf())
    val isUserNameExists by vm.foundUsers.observeAsState( false)
    var name by remember { vm.userName }
    var resultCheck by remember { vm.resultCheck }

    var isSelected by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf(0) }

    Column {
        OutlinedTextField(
            value = name,
            label = { Text("Name") },
            modifier = Modifier
                .padding(8.dp),
            onValueChange = { vm.changeName(it) },
            isError = isUserNameExists,
            supportingText = {
                if (isUserNameExists) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = vm.getStringUserNameExists,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value =  vm.userAge.value,
            label = { Text("Age") },
            modifier = Modifier
                .padding(8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            onValueChange = { vm.changeAge(it) }
        )

        Button(
            onClick = {
                vm.apply {
                    addUser()
                    userName.value = ""
                    userAge.value = ""
                    checkData()
                }
                      },
            enabled = resultCheck is ResultCheck.ResultOk,
            modifier = Modifier
                .padding(8.dp),
        ) { Text(
            text = "Add",
            fontSize = 24.sp
        ) }

        UserList(
            users = userList,
            delete = { vm.deleteUser(it) }
        )
    }
}

@Composable
fun UserList(users: List<User>,
             delete: (Int) -> Unit) {
    LazyColumn(
        modifier =  Modifier.fillMaxWidth()
    ) {
        item {UserTitleRow() }
        items(users) {
                user -> UserRow(user) { delete(user.id) }
        }
    }
}

@Composable
fun UserRow(user: User, onDelete: (Int) -> Unit) {
    // val onItemClick = { id: Int -> selectedId = id }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .background( if (user.id % 2 == 0)
                    Color.Green
                else
                    Color.Transparent
            )
            .clickable{
                // onItemClick.invoke(user.id)
            }
    ) {
        Text(
            text = user.id.toString(),
            modifier = Modifier.weight(0.1f),
            fontSize = 24.sp
        )
        Text(
            text = user.name,
            modifier = Modifier.weight(0.2f),
            fontSize = 24.sp
        )
        Text(
            text = user.age.toString(),
            modifier = Modifier.weight(0.2f),
            fontSize = 24.sp
        )
        Text(
            text = "Delete",
            color = Color(0xFF6650A4),
            fontSize = 24.sp,
            modifier = Modifier
                .weight(0.2f)
                .clickable { onDelete(user.id) }
        )
    }
}

@Composable
fun UserTitleRow() {
    Row(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .padding(8.dp)
            //.clickable()
    ) {
        Text(
            text = "ID",
            color = Color.White,
            modifier = Modifier.weight(0.1f),
            fontSize = 24.sp
        )
        Text(
            text = "Name",
            color = Color.White,
            modifier = Modifier.weight(0.2f),
            fontSize = 24.sp
        )
        Text(
            text = "Age",
            color = Color.White,
            modifier = Modifier.weight(0.2f),
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.weight(0.2f))
    }
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MainScreen()
}
*/

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello, $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UsersDBTheme {
        Greeting("Android")
    }
}