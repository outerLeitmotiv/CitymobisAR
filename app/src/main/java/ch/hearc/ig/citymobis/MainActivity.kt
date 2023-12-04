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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        val storage = FirebaseStorage.getInstance()
        val modelRef = storage.reference.child("ImageToStl.com_eglise_rouge3.glb")

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
        val engine = sceneView.engine
        val modelLoader = ModelLoader(engine, context)

        LaunchedEffect(modelRef) {
            try {
                val buffer = downloadModel(modelRef)
                buffer?.let {
                    val modelNode = ModelNode(modelInstance = modelLoader.createModelInstance(it))
                    sceneView.addChildNode(modelNode)
                    // Optionally, force an update or redraw of the SceneView here
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error in model loading: ${e.message}")
            }
        }

        AndroidView({ sceneView })
    }


    private suspend fun downloadModel(modelRef: StorageReference): ByteBuffer? {
        return try {
            withContext(Dispatchers.IO) {
                val bytes = modelRef.getBytes(Long.MAX_VALUE).await()
                ByteBuffer.wrap(bytes)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error downloading model: ${e.message}")
            null
        }
    }
}
