package com.example.photogallery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.photogallery.model.Photo
import com.example.photogallery.ui.theme.PhotoGalleryTheme
import com.example.photogallery.viewmodel.PhotoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoGalleryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: PhotoViewModel = viewModel()
                    val photos by viewModel.photos.collectAsState()
                    ImageGrid(
                        photos = photos,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageGrid(photos: List<Photo>, modifier: Modifier = Modifier) {
    var activePhotoId by rememberSaveable { mutableStateOf<String?>(null) }
    val listState = rememberLazyGridState()
    val viewModel: PhotoViewModel = viewModel()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        state = listState,
        modifier = modifier.padding(8.dp)
        ) {
        items(photos, { it.ID }) { photo ->
            Thumbnail(
                photo,
                Modifier.clickable { activePhotoId = photo.ID }
            )
        }
    }
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val total = listState.layoutInfo.totalItemsCount
            lastVisible != null && lastVisible >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.fetchPhotos(24, photos.lastOrNull()?.ID ?: "")
        }
    }
    if (activePhotoId != null) {
        BackHandler {
            activePhotoId = null
        }
        FullscreenSlider(
            photos, activePhotoId!!
        )
    }
}

@Composable
fun Thumbnail(photo: Photo, modifier: Modifier = Modifier) {
    AsyncImage(
        model = "http://10.0.2.2:8080/files/${photo.ID}_thumb.jpg",
        contentDescription = "Photo ${photo.ID}",
        modifier = modifier
            .size(120.dp)
            .padding(4.dp),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun FullscreenSlider(photos: List<Photo>, activePhotoId: String, modifier: Modifier = Modifier) {
    val activePhotoIndex = photos.indexOfFirst { it.ID == activePhotoId }
    val pagerState = rememberPagerState(initialPage = activePhotoIndex, 0f, { photos.size })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        HorizontalPager(pagerState) { index ->
            val photo = photos[index]
            AsyncImage(
                model = "http://10.0.2.2:8080/files/${photo.ID}.jpg",
                contentDescription = "Photo ${photo.ID}",
                modifier = modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
fun FullScreenImage(photo: Photo, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AsyncImage(
            model = "http://10.0.2.2:8080/files/${photo.ID}.jpg",
            contentDescription = "Photo ${photo.ID}",
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
    }
}