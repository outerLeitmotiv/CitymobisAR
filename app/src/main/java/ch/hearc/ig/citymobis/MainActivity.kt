package ch.hearc.ig.citymobis

import android.os.Bundle
import android.util.Log
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

fun loadModel(nodes: SnapshotStateList<Node>, modelLoader: ModelLoader, modelPath: String) {
    val modelNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(modelPath),
        autoAnimate = true
    )
    nodes.add(modelNode)
}

suspend fun downloadFile(storageUrl: String, localPath: String, onSuccess: (File) -> Unit, onFailure: (Exception) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(storageUrl)
    val localFile = File(localPath)

    try {
        storageRef.getFile(localFile).await()
        onSuccess(localFile)
    } catch (e: Exception) {
        onFailure(e)
        Log.e("firebase", "File download failed: ${e.message}")
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val modelLoader = rememberModelLoader(rememberEngine())
            val nodes = rememberNodes()

            ModelScreen()
            val storageUrl = "gs://citymobis-89b20.appspot.com/ImageToStl.com_eglise_rouge3.glb"
            val localFilePath = "${filesDir}/eglise_rouge3.glb"

            LaunchedEffect(Unit) {
                downloadFile(
                    storageUrl,
                    localFilePath,
                    onSuccess = { file ->
                        Log.d("firebase", "File downloaded successfully: ${file.path}")
                        // Load the model after successful download
                        loadModel(nodes, modelLoader, file.path)
                    },
                    onFailure = { exception ->
                        Log.e("firebase", "Error downloading file: ${exception.message}")
                        // Additional failure handling
                    }
                )
            }

        }
    }
}
