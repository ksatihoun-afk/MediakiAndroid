package com.mediaki.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

private val Ink = Color(0xFF11131B)
private val Panel = Color(0xFF1B1F2B)
private val Accent = Color(0xFF8B7CFF)
private val Mint = Color(0xFF53D6B5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MediakiApp() }
    }
}

data class VideoEntry(val uri: Uri, val title: String, val subtitle: String = "Fichier local")

@Composable
fun MediakiApp() {
    var dark by remember { mutableStateOf(true) }
    var selected by remember { mutableStateOf<VideoEntry?>(null) }
    var videos by remember { mutableStateOf(listOf<VideoEntry>()) }
    var privateMode by remember { mutableStateOf(false) }
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        val added = uris.map { uri ->
            VideoEntry(uri, uri.lastPathSegment?.substringAfterLast('/') ?: "Vidéo")
        }
        videos = (videos + added).distinctBy { it.uri }
    }

    val scheme = if (dark) darkColorScheme(
        primary = Accent, secondary = Mint, background = Ink, surface = Panel,
        onBackground = Color.White, onSurface = Color.White
    ) else lightColorScheme(primary = Color(0xFF5548C8), secondary = Color(0xFF087D68))

    MaterialTheme(colorScheme = scheme) {
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            if (selected != null) {
                VideoScreen(video = selected!!, onBack = { selected = null })
            } else {
                Column(Modifier.fillMaxSize()) {
                    Header(dark = dark, onTheme = { dark = !dark })
                    Row(Modifier.padding(horizontal = 18.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = !privateMode, onClick = { privateMode = false }, label = { Text("Bibliothèque") })
                        FilterChip(selected = privateMode, onClick = { privateMode = true }, label = { Text("Espace privé") })
                    }
                    if (privateMode) {
                        PrivateVaultInfo()
                    } else {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("Votre cinéma, partout.", fontSize = 23.sp, fontWeight = FontWeight.Bold)
                                Text("${videos.size} vidéo(s) importée(s)", color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
                            }
                            FilledTonalButton(onClick = { picker.launch(arrayOf("video/*")) }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(6.dp)); Text("Ajouter")
                            }
                        }
                        if (videos.isEmpty()) {
                            Box(Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.VideoLibrary, null, tint = Accent, modifier = Modifier.size(58.dp))
                                    Spacer(Modifier.height(12.dp))
                                    Text("Votre bibliothèque est vide", fontWeight = FontWeight.SemiBold)
                                    Text("Ajoutez des vidéos depuis votre téléphone.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f))
                                    Spacer(Modifier.height(16.dp))
                                    Button(onClick = { picker.launch(arrayOf("video/*")) }) { Text("Choisir des vidéos") }
                                }
                            }
                        } else {
                            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(videos) { video ->
                                    VideoRow(video) { selected = video }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(dark: Boolean, onTheme: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF25213F), Color(0xFF171B2A))))
            .padding(horizontal = 20.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(44.dp).background(Accent, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(30.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("MÉDIAKI", color = Color.White, fontWeight = FontWeight.Black, fontSize = 21.sp, letterSpacing = 1.5.sp)
            Text("Toutes tes vidéos, une expérience sans limites.", color = Color(0xFFB8B6CC), fontSize = 11.sp)
        }
        IconButton(onClick = onTheme) {
            Icon(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = "Changer le thème", tint = Color.White)
        }
    }
}

@Composable
private fun VideoRow(video: VideoEntry, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(width = 76.dp, height = 58.dp).background(Color(0xFF34304F), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayCircle, null, tint = Accent, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(video.title, fontWeight = FontWeight.SemiBold, maxLines = 2)
                Text(video.subtitle, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f), fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, null)
        }
    }
}

@Composable
private fun VideoScreen(video: VideoEntry, onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(video.uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(video.uri))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Retour") }
            Text(video.title, maxLines = 1, fontWeight = FontWeight.SemiBold)
        }
        AndroidView(
            factory = { ctx -> PlayerView(ctx).apply { this.player = player; useController = true } },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )
    }
}

@Composable
private fun PrivateVaultInfo() {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Lock, null, tint = Accent, modifier = Modifier.size(54.dp))
        Spacer(Modifier.height(12.dp))
        Text("Coffre-fort privé", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Le coffre-fort chiffré sera activé dans une prochaine étape. Cette version ne déplace ni ne protège encore vos fichiers privés.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f))
    }
}
