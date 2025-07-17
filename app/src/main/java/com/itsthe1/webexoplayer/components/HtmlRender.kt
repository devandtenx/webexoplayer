import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun HtmlText(html: String) {
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            }
        },
        update = { view ->
            view.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        }
    )
}