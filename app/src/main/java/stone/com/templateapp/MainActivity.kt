package stone.com.templateapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import stone.com.templateapp.util.Logs

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnHello.setOnClickListener { startActivity<MainActivity>() }
    }

    override fun onResume() {
        super.onResume()
//        AppUtil.isAppAlive(act, BuildConfig.APPLICATION_ID)
    }

    override fun onDestroy() {
        Logs.i("MainActivity1")
        super.onDestroy()

        Logs.i("MainActivity2")
    }

}
