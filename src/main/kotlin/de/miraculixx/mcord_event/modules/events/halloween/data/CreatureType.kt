package de.miraculixx.mcord_event.modules.events.halloween.data

enum class CreatureType(val rarity: Rarity, val spawnMessage: String) {
    BAT(Rarity.COMMON, "Eine wilde Fledermaus kommt angeflogen"),
    SKELETON(Rarity.COMMON, "Du hast ein einsames Skelett gefunden"),
    SPIDER(Rarity.COMMON, "Eine wilde Spinne krabbelt auf dich zu"),
    GHOST(Rarity.COMMON, "Ein einsamer Geist ist erschienen"),

    ZOMBIE(Rarity.RARE, "Ein hungriger Zombie kriecht um die Ecke"),
    WITCH(Rarity.RARE, "Du hast eine lachende Hexe angelockt"),
    WOLF(Rarity.RARE, "Ein lechzender grauer Wolf setzt zum Sprung an"),

    PUMPKIN(Rarity.EPIC, "Du findest einen leuchtenden, gruseligen Kürbis"),
    MUMMY(Rarity.EPIC, "Eine uralte Mumie erwacht vor dir zum Leben"),
    VAMPIRE(Rarity.EPIC, "Du hast einen durstigen Vampir angelockt"),
    DEMON(Rarity.EPIC, "Ein heimtückischer Dämon schleicht sich an"),

    HEADLESS_HORSEMAN(Rarity.LEGENDARY, "Ein kopfloser Reiter galoppiert mit Gelächter auf dich zu"),
    REAPER(Rarity.LEGENDARY, "Du hast einen mystischen Sensenmann beschworen")
}