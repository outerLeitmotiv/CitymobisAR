package ch.hearc.ig.citymobis

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.*
import io.github.sceneview.SceneView
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class MainActivity : ComponentActivity() {
    private lateinit var sceneView: SceneView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppContent()
        }
    }

    @Composable
    fun AppContent() {
        val isAuthenticated = remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }

        if (isAuthenticated.value) {
            MainContent()
        } else {
            AuthenticationUI { email, password ->
                signInOrSignUp(email, password, isAuthenticated)
            }
        }
    }

    @Composable
    fun MainContent() {
        val context = LocalContext.current
        sceneView = SceneView(context)
        val storage = FirebaseStorage.getInstance()
        val modelRef = storage.reference.child("futuristic_building.glb")

        Box(modifier = Modifier.fillMaxSize()) {
            SceneViewContainer(modelRef)
        }
    }

    @Composable
    fun AuthenticationUI(onAuthentication: (String, String) -> Unit) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val context = LocalContext.current

        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.padding(bottom = 8.dp),
                label = { Text("Email") }
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.padding(bottom = 16.dp),
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onAuthentication(email, password)
                    } else {
                        Toast.makeText(context, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Sign In/Sign Up")
            }
        }
    }


    private fun signInOrSignUp(email: String, password: String, isAuthenticated: MutableState<Boolean>) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isAuthenticated.value = true
            } else {
                // If sign in fails, try sign up
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { signUpTask ->
                    if (signUpTask.isSuccessful) {
                        isAuthenticated.value = true
                    } else {
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun loadScene() {
        val context = this
        sceneView = SceneView(context)
        val storage = FirebaseStorage.getInstance()
        val modelRef = storage.reference.child("futuristic_building.glb")

        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                SceneViewContainer(modelRef)
            }
        }
    }

    @Composable
    fun SceneViewContainer(modelRef: StorageReference) {
        val context = LocalContext.current
        val sceneView = remember { SceneView(context) }
        val modelLoader = ModelLoader(sceneView.engine, context)

        LaunchedEffect(modelRef) {
            launch(Dispatchers.IO) {
                Log.d("MainActivity", "Starting model download")
                val byteBuffer = downloadModelFromFirebase(modelRef)
                if (byteBuffer == null) {
                    Log.e("MainActivity", "Model download failed or returned null")
                } else {
                    Log.d("MainActivity", "Model downloaded, size: ${byteBuffer.remaining()} bytes")
                    withContext(Dispatchers.Main) {
                        val modelNode = ModelNode(modelInstance = modelLoader.createModelInstance(byteBuffer))
                        Log.d("MainActivity", "ModelNode created, details: ${modelNode.toString()}")
                        sceneView.addChildNode(modelNode)
                        Log.d("MainActivity", "ModelNode added to SceneView, child count: ${sceneView.childNodes.size}")
                    }
                }
            }
        }

        AndroidView({ sceneView })
    }

    private suspend fun downloadModelFromFirebase(modelRef: StorageReference): ByteBuffer? {
        return try {
            val bytes = modelRef.getBytes(Long.MAX_VALUE).await()
            ByteBuffer.wrap(bytes)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error downloading model: ${e.message}")
            null
        }
    }
}
