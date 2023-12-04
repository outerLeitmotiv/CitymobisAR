package ch.hearc.ig.citymobis

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
