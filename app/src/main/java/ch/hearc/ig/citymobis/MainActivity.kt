package ch.hearc.ig.citymobis

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.esri.arcgisruntime.mapping.ArcGISScene
import com.esri.arcgisruntime.mapping.BasemapStyle
import com.esri.arcgisruntime.mapping.NavigationConstraint
import com.esri.arcgisruntime.mapping.Surface
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource
import com.esri.arcgisruntime.toolkit.ar.ArcGISArView
import com.esri.arcgisruntime.toolkit.ar.ArLocationDataSource

class ARNavigateActivity : AppCompatActivity() {

    private lateinit var mArView: ArcGISArView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions()
    }

    private fun navigateInAr() {
        // get a reference to the AR view
        mArView = findViewById(R.id.arView)
        lifecycle.addObserver(mArView)

        // create a scene and add it to the scene view
        val scene = ArcGISScene(BasemapStyle.ARCGIS_IMAGERY)
        mArView.sceneView.scene = scene

        // create and add an elevation source to the scene
        val elevationSource = ArcGISTiledElevationSource(getString(R.string.elevation_url))
        val elevationSurface = Surface().apply {
            elevationSources.add(elevationSource)
            navigationConstraint = NavigationConstraint.NONE
            opacity = 0f
        }
        scene.baseSurface = elevationSurface

        // disable plane visualization
        mArView.arSceneView?.planeRenderer?.isEnabled ?:   false
        mArView.arSceneView?.planeRenderer?.isVisible ?:   false

        // add an AR location data source
        mArView.locationDataSource = ArLocationDataSource(this)

        // start tracking
        mArView.startTracking(ArcGISArView.ARLocationTrackingMode.CONTINUOUS)
    }

    /**
     * Request camera permission.
     */
    private fun requestPermissions() {
        val reqPermissions = arrayOf(Manifest.permission.CAMERA)
        val requestCode = 2
        if (ContextCompat.checkSelfPermission(this, reqPermissions[0]) == PackageManager.PERMISSION_GRANTED) {
            navigateInAr()
        } else {
            ActivityCompat.requestPermissions(this, reqPermissions, requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            navigateInAr()
        } else {
            Toast.makeText(this, getString(R.string.navigate_ar_permission_denied), Toast.LENGTH_SHORT).show()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        super.onPause()
        mArView.stopTracking()
    }

    override fun onResume() {
        super.onResume()
        mArView.startTracking(ArcGISArView.ARLocationTrackingMode.CONTINUOUS)
    }
}
