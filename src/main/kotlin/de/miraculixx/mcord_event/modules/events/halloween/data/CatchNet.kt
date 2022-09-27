package de.miraculixx.mcord_event.modules.events.halloween.data

// "pronoun" because name cannot be overridden, bec enum implementation is shit
enum class CatchNet(val pronoun: String, val emote: String) {
    BASIC("Basic", "<:basic_net:1023594136655372419>"),
    SILVER("Silber", "<:silver_net:1023682513970925699>"),
    GOLD("Gold", "<:gold_net:1023594135153807392>")
}