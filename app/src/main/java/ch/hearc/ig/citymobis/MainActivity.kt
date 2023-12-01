package ch.hearc.ig.citymobis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.firebase.storage.FirebaseStorage
import io.github.sceneview.ar.ARScene
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import kotlinx.coroutines.tasks.await


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val storagePath = "gs://citymobis-89b20.appspot.com/eglise_rouge3.gltf"
            var modelUrl by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(storagePath) {
                modelUrl = FirebaseStorage.getInstance()
                    .getReferenceFromUrl(storagePath)
                    .downloadUrl
                    .await()
                    .toString()
            }

            Box(modifier = Modifier.fillMaxSize()) {
                modelUrl?.let { url ->
                    ARScene(
                        modifier = Modifier.fillMaxSize(),
                        engine = rememberEngine(),
                    ) {
                       val modelNode = ModelNode(
                         modelInstance = modelLoader.createModelInstance(url)
                       )

                    }
                }
            }
        }
    }
}
