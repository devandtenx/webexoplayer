import android.os.Build
import android.text.Html
import android.text.Html.fromHtml
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun HtmlText(html: String) {
    AndroidView(factory = { context ->
        TextView(context).apply {
            text = fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        }
    })
}