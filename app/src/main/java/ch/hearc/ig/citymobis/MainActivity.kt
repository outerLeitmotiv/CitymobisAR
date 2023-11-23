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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CitymobisTheme {
                ARScreen()
            }
        }
    }
}

@Composable
fun ARScreen() {
    val nodes = rememberNodes()

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier,
            childNodes = nodes,
            /*onTapAR = { motionEvent, hitResult ->
            onTap currently causing issues, TODO: fix
                // Fetch your 3D model from Firebase
                // For simplicity, assuming you have a function getModelFromFirebase() that returns a ModelInstance
                val modelInstance = getModelFromFirebase()

                // Create an AnchorNode with the hit result and add it to the nodes
                val anchorNode = AnchorNode(hitResult.createAnchor()).apply {
                    addChildNode(ModelNode(modelInstance))
                }
                nodes += anchorNode
            },
            // Other ARScene parameters as needed
            // ...*/
        )
    }
}

// Dummy function for fetching model - Replace with actual Firebase fetching logic
fun getModelFromFirebase(): ModelInstance? {
    // Implement logic to fetch and return a ModelInstance from Firebase
    // This is just a placeholder
    // TODO: add 3D models to firebase
    return null
}


