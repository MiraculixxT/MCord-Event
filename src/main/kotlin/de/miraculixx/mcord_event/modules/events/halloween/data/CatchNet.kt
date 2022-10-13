package de.miraculixx.mcord_event.modules.events.halloween.data

// "pronoun" because name cannot be overridden, bec enum implementation is shit
enum class CatchNet(val pronoun: String, val emote: String, val url: String) {
    BASIC("Basic", "<:basic_net:1023594136655372419>", "https://cdn.discordapp.com/emojis/1023594136655372419.webp?size=240&quality=lossless"),
    SILVER("Silber", "<:silver_net:1023682513970925699>", "https://cdn.discordapp.com/emojis/1023682513970925699.webp?size=240&quality=lossless"),
    GOLD("Gold", "<:gold_net:1023594135153807392>", "https://cdn.discordapp.com/emojis/1023594135153807392.webp?size=240&quality=lossless")
}