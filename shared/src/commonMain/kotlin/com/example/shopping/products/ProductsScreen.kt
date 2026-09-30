package com.example.shopping.products

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    viewModel: ProductViewModel,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedProduct by remember {
        mutableStateOf<Product?>(null)
    }

    var showSortMenu by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Shopping",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(
                        onClick = onLogout
                    ) {
                        Text("Logout")
                    }
                },
                actions = {
                    Text(
                        text = "🛍",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            ProductsHeader(
                productCount = uiState.products.size,
                sortOrder = uiState.sortOrder,
                showSortMenu = showSortMenu,
                onSortMenuChange = {
                    showSortMenu = it
                },
                onSortSelected = {
                    showSortMenu = false
                    viewModel.sortProducts(it)
                }
            )

            when {

                uiState.isLoading -> {
                    ProductSkeletonGrid()
                }

                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error,
                        onRetry = viewModel::retry
                    )
                }

                uiState.products.isEmpty() -> {
                    EmptyProductsState()
                }

                else -> {
                    ProductGrid(
                        products = uiState.products,
                        onProductClick = {
                            selectedProduct = it
                        }
                    )
                }
            }
        }
    }

    selectedProduct?.let { product ->

        ProductDetailsBottomSheet(
            product = product,
            onDismiss = {
                selectedProduct = null
            }
        )
    }
}

@Composable
private fun ProductsHeader(
    productCount: Int,
    sortOrder: ProductSortOrder,
    showSortMenu: Boolean,
    onSortMenuChange: (Boolean) -> Unit,
    onSortSelected: (ProductSortOrder) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            )
    ) {

        Text(
            text = "Discover products",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = "Find something you'll love.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = "$productCount products",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box {

                AssistChip(
                    onClick = {
                        onSortMenuChange(true)
                    },
                    label = {
                        Text(
                            text = when (sortOrder) {
                                ProductSortOrder.NONE ->
                                    "Sort"

                                ProductSortOrder.PRICE_LOW_TO_HIGH ->
                                    "Price: Low → High"

                                ProductSortOrder.PRICE_HIGH_TO_LOW ->
                                    "Price: High → Low"
                            }
                        )
                    }
                )

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = {
                        onSortMenuChange(false)
                    }
                ) {

                    DropdownMenuItem(
                        text = {
                            Text("Price: Low to High")
                        },
                        onClick = {
                            onSortSelected(
                                ProductSortOrder.PRICE_LOW_TO_HIGH
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Price: High to Low")
                        },
                        onClick = {
                            onSortSelected(
                                ProductSortOrder.PRICE_HIGH_TO_LOW
                            )
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text("Clear Sort")
                        },
                        onClick = {
                            onSortSelected(
                                ProductSortOrder.NONE
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductGrid(
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 4.dp,
            bottom = 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(
            items = products,
            key = { it.id }
        ) { product ->

            ProductCard(
                product = product,
                onClick = {
                    onProductClick(product)
                }
            )
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {

        Column {

            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp
                        )
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = product.category
                        .replaceFirstChar {
                            it.uppercase()
                        },
                    style = MaterialTheme.typography.labelSmall,
                    color =
                        MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "★${(product.rating)}",
                    style = MaterialTheme.typography.labelMedium,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProductSkeletonGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 4.dp,
            bottom = 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(6) {

            ProductSkeletonCard()
        }
    }
}

@Composable
private fun ProductSkeletonCard() {
    val transition =
        rememberInfiniteTransition(
            label = "skeleton"
        )

    val shimmerX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    tween(
                        durationMillis = 1100,
                        easing = LinearEasing
                    ),
                repeatMode = RepeatMode.Restart
            ),
        label = "shimmer"
    )

    val shimmerBrush =
        Brush.linearGradient(
            colors = listOf(
                Color.LightGray.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.75f),
                Color.LightGray.copy(alpha = 0.35f)
            ),
            start = androidx.compose.ui.geometry.Offset(
                x = shimmerX * 500f,
                y = 0f
            ),
            end = androidx.compose.ui.geometry.Offset(
                x = shimmerX * 500f + 250f,
                y = 0f
            )
        )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(shimmerBrush)
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(shimmerBrush)
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(shimmerBrush)
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(shimmerBrush)
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(shimmerBrush)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDetailsBottomSheet(
    product: Product,
    onDismiss: () -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 8.dp
                )
        ) {

            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = product.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
//                    text = "$${"%.2f".formatPrice(product.price)}",
                    text = "$${product.price}",
                    style =
                        MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Text(
                    text =
                        "★${(product.rating)}",
                    color =
                        MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider()

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyLarge,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            ProductDetailRow(
                label = "Category",
                value = product.category
            )

            ProductDetailRow(
                label = "Brand",
                value = product.brand ?: "N/A"
            )

            ProductDetailRow(
                label = "Stock",
                value = "${product.stock} available"
            )

            if (product.discountPercentage > 0) {
                ProductDetailRow(
                    label = "Discount",
                    value =
                        "${(product.discountPercentage)}% off"
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue Shopping")
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }
}

@Composable
private fun ProductDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ErrorState(
    message: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text = "Couldn't load products",
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = message ?: "Unknown error",
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = onRetry
        ) {
            Text("Try Again")
        }
    }
}

@Composable
private fun EmptyProductsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {

        Text(
            text = "No products found",
            style =
                MaterialTheme.typography.titleLarge
        )
    }
}

private fun formatPrice(
    value: Double
): String {
    val rounded =
        kotlin.math.round(value * 100) / 100

    return rounded.toString()
}