package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

data class DemonSlayerCharacter(
    val id: String,
    val name: String,
    val japaneseName: String,
    val title: String,
    val breathingStyle: String,
    val quote: String,
    val themeColor: Color,
    val accentColor: Color,
    val unlockLevel: Int,
    val iconSymbol: String,
    val formNames: List<String>
)

object DemonSlayerDatabase {
    val characters = listOf(
        DemonSlayerCharacter(
            id = "tanjiro",
            name = "Tanjiro Kamado",
            japaneseName = "竈門 炭治郎",
            title = "Kamado Heir & Sun Inheritor",
            breathingStyle = "Sun & Water Breathing",
            quote = "No matter how many people you lose, you have no choice but to go on living.",
            themeColor = CrimsonSun,
            accentColor = FlameGold,
            unlockLevel = 1,
            iconSymbol = "☀️",
            formNames = listOf(
                "First Form: Water Surface Slash",
                "Tenth Form: Constant Flux",
                "Hinokami Kagura: Clear Blue Sky",
                "Sun Breathing: Dragon Sun Halo Head Dance"
            )
        ),
        DemonSlayerCharacter(
            id = "zenitsu",
            name = "Zenitsu Agatsuma",
            japaneseName = "我妻 善逸",
            title = "Thunder Successor",
            breathingStyle = "Thunder Breathing",
            quote = "If you can only do one thing, hone it to supreme perfection.",
            themeColor = ThunderYellow,
            accentColor = FlameAmber,
            unlockLevel = 2,
            iconSymbol = "⚡",
            formNames = listOf(
                "First Form: Thunderclap and Flash",
                "Sixfold",
                "Eightfold",
                "Seventh Form: Honoikazuchi no Kami"
            )
        ),
        DemonSlayerCharacter(
            id = "inosuke",
            name = "Inosuke Hashibira",
            japaneseName = "嘴平 伊之助",
            title = "King of the Mountains",
            breathingStyle = "Beast Breathing",
            quote = "We're charging in! Keep your head up and push forward!",
            themeColor = BeastTeal,
            accentColor = WaterBlueGlow,
            unlockLevel = 3,
            iconSymbol = "🐗",
            formNames = listOf(
                "First Fang: Pierce",
                "Fifth Fang: Crazy Cutting",
                "Seventh Form: Spatial Awareness",
                "Eighth Fang: Explosive Rush"
            )
        ),
        DemonSlayerCharacter(
            id = "giyu",
            name = "Giyu Tomioka",
            japaneseName = "冨岡 義勇",
            title = "Water Hashira",
            breathingStyle = "Water Breathing",
            quote = "Feel the rage. The pure, unyielding drive to never surrender.",
            themeColor = WaterBlue,
            accentColor = WaterBlueGlow,
            unlockLevel = 5,
            iconSymbol = "🌊",
            formNames = listOf(
                "First Form: Water Surface Slash",
                "Fourth Form: Striking Tide",
                "Eighth Form: Waterfall Basin",
                "Eleventh Form: Dead Calm (凪)"
            )
        ),
        DemonSlayerCharacter(
            id = "rengoku",
            name = "Kyojuro Rengoku",
            japaneseName = "煉獄 杏寿郎",
            title = "Flame Hashira",
            breathingStyle = "Flame Breathing",
            quote = "Set your heart ablaze. Go beyond your limits!",
            themeColor = FlameGold,
            accentColor = CrimsonSun,
            unlockLevel = 10,
            iconSymbol = "🔥",
            formNames = listOf(
                "First Form: Unknowing Fire",
                "Third Form: Blazing Universe",
                "Fifth Form: Flame Tiger",
                "Ninth Form: Rengoku (煉獄)"
            )
        ),
        DemonSlayerCharacter(
            id = "shinobu",
            name = "Shinobu Kocho",
            japaneseName = "胡蝶 しのぶ",
            title = "Insect Hashira",
            breathingStyle = "Insect Breathing",
            quote = "Gentle grace concealing lethal discipline and chemical mastery.",
            themeColor = InsectPurple,
            accentColor = MistCyan,
            unlockLevel = 15,
            iconSymbol = "🦋",
            formNames = listOf(
                "Butterfly Dance: Caprice",
                "Dance of the Bee Sting",
                "Dance of the Dragonfly",
                "Dance of the Centipede"
            )
        ),
        DemonSlayerCharacter(
            id = "muichiro",
            name = "Muichiro Tokito",
            japaneseName = "時透 無一郎",
            title = "Mist Hashira",
            breathingStyle = "Mist Breathing",
            quote = "Cut through all doubt as pure clear mist.",
            themeColor = MistCyan,
            accentColor = WaterBlue,
            unlockLevel = 20,
            iconSymbol = "🌫️",
            formNames = listOf(
                "First Form: Low Clouds, Distant Haze",
                "Fourth Form: Shifting Flow Slash",
                "Fifth Form: Sea of Clouds and Haze",
                "Seventh Form: Obscuring Clouds"
            )
        ),
        DemonSlayerCharacter(
            id = "mitsuri",
            name = "Mitsuri Kanroji",
            japaneseName = "甘露寺 蜜璃",
            title = "Love Hashira",
            breathingStyle = "Love Breathing",
            quote = "Channel boundless passion and heart into supreme strength.",
            themeColor = LovePink,
            accentColor = FlameGold,
            unlockLevel = 25,
            iconSymbol = "🌸",
            formNames = listOf(
                "First Form: Shivers of First Love",
                "Second Form: Anguish Inducing Love",
                "Fifth Form: Swaying Love, Wildclaw",
                "Sixth Form: Cat-Legged Winds of Love"
            )
        ),
        DemonSlayerCharacter(
            id = "sanemi",
            name = "Sanemi Shinazugawa",
            japaneseName = "不死川 実弥",
            title = "Wind Hashira",
            breathingStyle = "Wind Breathing",
            quote = "A whirlwind tearing through all weakness and hesitation.",
            themeColor = WindEmerald,
            accentColor = ThunderYellow,
            unlockLevel = 30,
            iconSymbol = "🌪️",
            formNames = listOf(
                "First Form: Dust Whirlwind Cutter",
                "Third Form: Clean Storm Wind Trees",
                "Seventh Form: Gale, Sudden Gusts",
                "Ninth Form: Idaten Typhoon"
            )
        ),
        DemonSlayerCharacter(
            id = "obanai",
            name = "Obanai Iguro",
            japaneseName = "伊黒 小芭内",
            title = "Serpent Hashira",
            breathingStyle = "Serpent Breathing",
            quote = "Striking with razor-sharp slithering accuracy.",
            themeColor = SerpentGreen,
            accentColor = WaterBlue,
            unlockLevel = 35,
            iconSymbol = "🐍",
            formNames = listOf(
                "First Form: Winding Serpent Slash",
                "Second Form: Venom Fangs of the Narrow Head",
                "Third Form: Coil Choke",
                "Fifth Form: Slithering Serpent"
            )
        ),
        DemonSlayerCharacter(
            id = "gyomei",
            name = "Gyomei Himejima",
            japaneseName = "悲鳴嶼 行冥",
            title = "Stone Hashira",
            breathingStyle = "Stone Breathing",
            quote = "Unshakable boulder. We mourn weakness and forge unbreakable spirit.",
            themeColor = StoneGray,
            accentColor = FlameGold,
            unlockLevel = 40,
            iconSymbol = "🪨",
            formNames = listOf(
                "First Form: Serpentine Bipolar",
                "Second Form: Upper Smash",
                "Third Form: Stone Skin",
                "Fifth Form: Arcs of Justice"
            )
        )
    )

    fun getCharacter(id: String): DemonSlayerCharacter {
        return characters.find { it.id == id } ?: characters.first()
    }
}

data class BreathingPath(
    val name: String,
    val description: String,
    val icon: String,
    val color: Color
)

object BreathingPathsData {
    val paths = listOf(
        BreathingPath("Discipline", "Routine, focus, daily habit consistency", "⚔️", CrimsonSun),
        BreathingPath("Fitness", "Physical strength, calisthenics, power & endurance", "🏋️", FlameGold),
        BreathingPath("Mind", "Deep reflection, journaling, mental clarity", "🧘", WaterBlue),
        BreathingPath("Academics", "Knowledge acquisition, study hours, mastery", "📚", MistCyan),
        BreathingPath("Health", "Sleep, recovery, hydration, nourishment", "🌿", WindEmerald),
        BreathingPath("Creator", "Projects, creation, artistic output", "🎨", LovePink)
    )
}
