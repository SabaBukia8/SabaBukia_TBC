package com.example.mtgcollectionmanager.domain.model

data class CardPricing(
    val markets: List<MarketPrice>
)

data class MarketPrice(
    val marketName: String,
    val marketId: String,
    val normalPrice: Double?,
    val foilPrice: Double?,
    val purchaseUrl: String?
) {
    companion object {
        const val MARKET_TCGPLAYER = "tcgplayer"
        const val MARKET_CARDMARKET = "cardmarket"
        const val MARKET_CARD_KINGDOM = "cardkingdom"
        const val MARKET_STAR_CITY_GAMES = "starcitygames"
    }
}
