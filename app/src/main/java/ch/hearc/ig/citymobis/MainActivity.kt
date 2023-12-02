package ch.hearc.ig.citymobis

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import io.github.sceneview.Scene
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File



class MainActivity : ComponentActivity() {

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        signInStaticUser()

        setContent {
            val modelLoader = rememberModelLoader(rememberEngine())
            val nodes = rememberNodes()
            val localFilePath = "${filesDir}/eglise_rouge3.glb"

            ModelScreen()
            LaunchedEffect(true) {
                withContext(Dispatchers.IO) {
                    downloadFile()
                    loadModel(nodes, modelLoader, localFilePath)
                }
            }
        }
    }
    @Composable
    fun ModelScreen() {
        val nodes = rememberNodes()

        Box(modifier = Modifier.fillMaxSize()) {
            Scene(
                activity = LocalContext.current as? ComponentActivity,
                lifecycle = LocalLifecycleOwner.current.lifecycle,
                childNodes = nodes,
                engine = rememberEngine(),
            )
        }
    }

    private fun loadModel(nodes: SnapshotStateList<Node>, modelLoader: ModelLoader, modelPath: String) {
        val modelNode = ModelNode(
            modelInstance = modelLoader.createModelInstance(modelPath),
            autoAnimate = true
        )
        nodes.add(modelNode)
    }

    private fun downloadFile() {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://citymobis-89b20.appspot.com")
        val islandRef = storageRef.child("ImageToStl.com_eglise_rouge3.glb")

        val rootPath = File(Environment.getExternalStorageDirectory(), "file_name")
        if (!rootPath.exists()) {
            rootPath.mkdirs()
        }

        val localFile = File(rootPath, "eglise_rouge3.glb")

        islandRef.getFile(localFile)
            .addOnSuccessListener { taskSnapshot ->
                Log.e("firebase", ";local temp file created: ${localFile.absolutePath}")
                // updateDb(timestamp, localFile.toString(), position)
            }
            .addOnFailureListener { exception ->
                Log.e("firebase", ";local temp file not created: ${exception.message}")
            }
    }

    private fun signInStaticUser() {
        val email = "user@name.com"
        val password = "password"

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d("FirebaseAuth", "signInWithEmail:success")
                    val user = auth.currentUser
                    // Proceed with your app logic here...
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("FirebaseAuth", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

}
