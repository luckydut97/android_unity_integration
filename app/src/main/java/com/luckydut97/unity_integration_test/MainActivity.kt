package com.luckydut97.unity_integration_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import com.luckydut97.unity_integration_test.ui.theme.Unity_integration_TestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Unity_integration_TestTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    UnityVerifyApp()
                }
            }
        }
    }
}

@Composable
fun UnityVerifyApp() {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var todoInput by rememberSaveable { mutableStateOf("") }
    var todos by rememberSaveable { mutableStateOf(listOf<String>()) }

    if (!isLoggedIn) {
        LoginScreen(
            username = username,
            password = password,
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onLogin = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    isLoggedIn = true
                }
            }
        )
    } else {
        MainScreen(
            username = username,
            todoInput = todoInput,
            todos = todos,
            onTodoChange = { todoInput = it },
            onAddTodo = {
                if (todoInput.isNotBlank()) {
                    todos = todos + todoInput.trim()
                    todoInput = ""
                }
            },
            onLogout = {
                username = ""
                password = ""
                isLoggedIn = false
                todoInput = ""
                todos = emptyList()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UnityVerifyPreview() {
    Unity_integration_TestTheme {
        Surface {
            UnityVerifyApp()
        }
    }
}

@Composable
private fun LoginScreen(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Unity Verify 로그인",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTagWithContentDescription("usernameField"),
            singleLine = true,
            label = { Text("이메일 또는 ID") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTagWithContentDescription("passwordField"),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("패스워드") }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onLogin,
            modifier = Modifier
                .fillMaxWidth()
                .testTagWithContentDescription("loginButton"),
            enabled = username.isNotBlank() && password.isNotBlank()
        ) {
            Text("로그인")
        }
    }
}

@Composable
private fun MainScreen(
    username: String,
    todoInput: String,
    todos: List<String>,
    onTodoChange: (String) -> Unit,
    onAddTodo: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "안녕하세요, $username",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "오늘 처리할 일을 입력하세요",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = todoInput,
            onValueChange = onTodoChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTagWithContentDescription("todoField"),
            label = { Text("할 일") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onAddTodo,
            modifier = Modifier
                .fillMaxWidth()
                .testTagWithContentDescription("addTodoButton"),
            enabled = todoInput.isNotBlank()
        ) {
            Text("추가하기")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "내 할 일",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTagWithContentDescription("todoList")
        ) {
            items(todos) { todo ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = todo,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .testTagWithContentDescription("logoutButton"),
            enabled = todos.isNotEmpty()
        ) {
            Text("로그아웃")
        }
    }
}

private fun Modifier.testTagWithContentDescription(tag: String): Modifier =
    this
        .testTag(tag)
        .semantics { this[SemanticsProperties.ContentDescription] = listOf(tag) }
