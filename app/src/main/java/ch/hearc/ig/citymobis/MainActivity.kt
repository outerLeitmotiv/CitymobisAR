package ch.hearc.ig.citymobis

import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ch.hearc.ig.citymobis.ui.theme.*
import io.github.sceneview.*
import io.github.sceneview.ar.*
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import com.google.ar.core.Anchor
import com.google.ar.core.Config.CloudAnchorMode;
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.firebase.*
import io.github.sceneview.model.Model


class MainActivity : ComponentActivity() {
    private lateinit var sceneView: SceneView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sceneView = findViewById(R.id.sceneView)

        val modelUrl = "gs://citymobis-89b20.appspot.com/eglise_rouge3.gltf"

        Model.loadAsync(modelUrl).thenAccept { model ->
            val node = Node().apply {
                setModel(model)
                // Set other properties like position, rotation, etc.
            }
            sceneView.scene.addChild(node)
        }.exceptionally { throwable ->
            // Handle exceptions
        }


    }



}


