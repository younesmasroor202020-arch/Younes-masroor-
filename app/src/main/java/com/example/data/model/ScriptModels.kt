package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

data class CharacterProfile(
    val id: String,
    val name: String,
    val roleTitle: String,
    val description: String,
    val defaultVoice: String // For gemini-3.8-flash-tts
)

data class CameraShot(
    val type: String, // e.g. "لقطة واسعة جداً (Extreme Long Shot)"
    val tag: String,  // "ELS", "MS", "CU", "OTS", "Macro", "Crane", "360°"
    val description: String
)

data class DialogueLine(
    val speaker: String,
    val characterId: String,
    val stageDirection: String = "",
    val text: String,
    val suggestedVoice: String = "Puck"
)

data class CinematicScene(
    val id: Int,
    val title: String,
    val subtitle: String,
    val timeAndPlace: String,
    @DrawableRes val defaultImageRes: Int,
    val decorAndLocation: String,
    val lightingAndColors: String,
    val cameraShots: List<CameraShot>,
    val soundAndMusic: String,
    val musicPrompt: String,
    val dialogues: List<DialogueLine>,
    val thematicCore: String
)

object CinematicScriptRepository {
    val characters = listOf(
        CharacterProfile(
            id = "younes",
            name = "العم يونس مسرور",
            roleTitle = "الأب الحكيم وحارس إرث الروضة",
            description = "رجل وقور، عركته السنين، يحمل في يديه المجعدتين تجربة عقود في رعاية النخيل وتجارة الحلوى الروضية، ينبض قلبه بالوفاء لأرض شبوة ولعائلته.",
            defaultVoice = "Charon" // Deep, warm, mature voice
        ),
        CharacterProfile(
            id = "aseel",
            name = "أصيل يونس مسرور",
            roleTitle = "الابن الطموح وروح المستقبل",
            description = "شاب يافع طموح وذكي، مشتت بين فرص المستقبل والتدريب في الخارج، وبين تعلقه العميق بأرض الروضة وتجارة والده التاريخية.",
            defaultVoice = "Puck" // Youthful, vibrant voice
        )
    )

    val scenes = listOf(
        CinematicScene(
            id = 1,
            title = "المشهد الأول: بستان النخيل",
            subtitle = "وقت الأصيل والارتباط بالتراب",
            timeAndPlace = "خارجي / نهار (وقت الأصيل) — بستان نخيل شبواني في مدينة الروضة",
            defaultImageRes = R.drawable.img_scene_palm_grove,
            decorAndLocation = "بستان نخيل شبواني أصيل في مدينة الروضة. جدران طينية منخفضة تحيط بالمكان، وأرضية ترابية مستوية. دكة حجرية مغطاة بسجادة حكايتية ذات نقوش يمنية تقليدية. أدوات تقليم نخيل قديمة ملقاة بعناية بجانب الدكة.",
            lightingAndColors = "إضاءة الطبيعة الدافئة لحظة الغروب (Golden Hour). نسيج الألوان يتنوع بين الأصفر الذهبي، البني الترابي، والخضرة الداكنة لجريد النخيل. الأشعة المخترقة لسعف النخيل تخلق ظلالاً ناعمة ومتباينة على وجوه الشخصيات.",
            cameraShots = listOf(
                CameraShot(
                    type = "لقطة واسعة جداً (Extreme Long Shot)",
                    tag = "ELS",
                    description = "تبدأ الكاميرا من ارتفاع متدرج تستعرض بساتين الروضة ومنازلها الطينية البعيدة، ثم تنزل ببطء نحو البستان."
                ),
                CameraShot(
                    type = "لقطة متوسطة (Medium Shot)",
                    tag = "MS",
                    description = "الكاميرا مثبتة على حوامل متحركة تقترب بهدوء من العم يونس وهو يجلس على الدكة يقلّم النخلة."
                ),
                CameraShot(
                    type = "لقطة قريبة (Close-up)",
                    tag = "CU",
                    description = "على يدي العم يونس المجعدتين المتمرستين وهي تلامس السعف، للتأكيد على الارتباط بالتراب."
                ),
                CameraShot(
                    type = "لقطة عبر الكتف (Over-the-Shoulder)",
                    tag = "OTS",
                    description = "مع دخول أصيل من الخلفية، تسير معه الكاميرا بخطوات بطيئة تعكس حيرته، حيث تظهر الرسالة والحقيبة في يده بتركيز بؤري متغير من وجه أصيل إلى الورقة."
                )
            ),
            soundAndMusic = "صوت حفيف أوراق النخيل في النسيم، وزقزقة عصافير المساء. تدفق موسيقي خفيف جداً بعزف منفرد على آلة العود بألحان شجية تعكس الحنين والأصالة.",
            musicPrompt = "A gentle, nostalgic cinematic Yemeni Shabwani solo Oud soundtrack with ambient desert evening breeze and soft palm rustles, reflecting roots and filial bond.",
            dialogues = listOf(
                DialogueLine(
                    speaker = "العم يونس",
                    characterId = "younes",
                    stageDirection = "يمسح يديه ببطء، وتعبيرات وجهه تبث الطمأنينة والأبوة",
                    text = "...",
                    suggestedVoice = "Charon"
                ),
                DialogueLine(
                    speaker = "أصيل",
                    characterId = "aseel",
                    stageDirection = "بنبرة هادئة يُخالطها التردد",
                    text = "مساء الخير يا أبتاه... هل أنت مشغول؟",
                    suggestedVoice = "Puck"
                ),
                DialogueLine(
                    speaker = "العم يونس",
                    characterId = "younes",
                    stageDirection = "يلتفت بابتسامة دافئة، الكاميرا تنتقل إلى لقطة قريبة على وجهه",
                    text = "أهلاً بـ أصيل.. ولد مسرور الغالي. لم أكن مشغولاً، كنت أرد السلام على هذه النخلة الصغرى التي غرسناها معاً قبل سنوات. تعالَ واجلس بجانبي.",
                    suggestedVoice = "Charon"
                ),
                DialogueLine(
                    speaker = "أصيل",
                    characterId = "aseel",
                    stageDirection = "يجلس على الدكة، ينظر إلى الأرض، ثم ينظر إلى والده",
                    text = "يا أبتاه.. وصلتني اليوم موافقة الشركة في الخارج، والسفر بعد أسبوعين. يفترض أن أكون سعيداً، لكنني أشعر بثقل غير عادي في صدري... وكأن خطاي شديدة الثقل على تراب الروضة.",
                    suggestedVoice = "Puck"
                ),
                DialogueLine(
                    speaker = "العم يونس",
                    characterId = "younes",
                    stageDirection = "يضع يده بثبات على كتف أصيل في لقطة قريبة",
                    text = "الثقل الذي تشعر به يا بني ليس خوفاً، بل هو الوفاء... الروضة ليست مجرد بيوت من طين ونخيل، الروضة هي جذورك، والجذور دائماً تشدّ صاحبها إلى الأرض التي نمت فيها روحه.",
                    suggestedVoice = "Charon"
                )
            ),
            thematicCore = "الجذور ليست قيوداً، بل هي المنبع الذي يمنح الروح ثباتها ووفاءها."
        ),
        CinematicScene(
            id = 2,
            title = "المشهد الثاني: الدكان العتيق",
            subtitle = "سوق الروضة القديم وهوية عائلة مسرور",
            timeAndPlace = "داخلي وخارجي / نهار — دكان تراثي في سوق الروضة القديم، شبوة",
            defaultImageRes = R.drawable.img_scene_ancient_souq,
            decorAndLocation = "دكان تراثي بعوارض خشبية سقفية وأرفف محفورة في الطين. أوانٍ نحاسية كلاسيكية تحتوي على السمسم وعسل الجردان والحلوى الروضية بألوانها الذهبية الداكنة. أكياس قماشية وعلب كرتونية مصممة بطابع محلي. خارج الدكان تتراءى أزقة السوق الطينية المزدحمة بالمارة.",
            lightingAndColors = "إضاءة تباينية دافئة تدخل عبر فتحات السقف والنوافذ الخشبية (الرواشن) وتضيء تصاعد البخار والغبار الناعم في الهواء. الألوان تغلب عليها درجات العسلي، النحاسي، والبني الدافئ.",
            cameraShots = listOf(
                CameraShot(
                    type = "لقطة تأسيسية متحركة (Establishing Tracking Shot)",
                    tag = "Tracking",
                    description = "تتجول الكاميرا ببطء بين أزقة السوق، تلتقط وجوه أهالي الروضة، ثم تغوص داخل الدكان."
                ),
                CameraShot(
                    type = "لقطة تفصيلية (Macro Shot)",
                    tag = "Macro",
                    description = "لقطة مقربة ومبهرة لعملية قطع الحلوى الروضية وسكب العسل والسمسم عليها ببطء بدقة عالية."
                ),
                CameraShot(
                    type = "لقطة مزدوجة (Medium Two-Shot)",
                    tag = "Two-Shot",
                    description = "تجمع يونس وأصيل خلف المنضدة الخشبية أثناء تفاعلهما مع الزبائن، تعكس التناغم بين البساطة والعمل."
                )
            ),
            soundAndMusic = "همهمات أهالي السوق، تحايا المارة الزكية باللهجة الشبوانية، وصوت الأواني النحاسية. الموسيقى تصبح أكثر حيوية بإيقاع شبواني خفيف يمتزج بألحان العود والناي.",
            musicPrompt = "A lively traditional Shabwani rhythm blended with authentic Oud and Nay melodies, warm market ambience, and festive heritage tones.",
            dialogues = listOf(
                DialogueLine(
                    speaker = "أصيل",
                    characterId = "aseel",
                    stageDirection = "يتطلع حوله في حيرة إيجابية، يمسك بقطعة حلوى",
                    text = "يا أبتاه، طالما تساءلت... كيف استطعت طوال هذه العقود أن تظل هنا برضا تام؟ ألم يراودك الشغف يوماً للرحيل واستكشاف العالم بعيداً عن شبوة؟",
                    suggestedVoice = "Puck"
                ),
                DialogueLine(
                    speaker = "العم يونس",
                    characterId = "younes",
                    stageDirection = "يتوقف عن العمل بوقار، وتتحرك الكاميرا ببطء بتكبير تدريجي نحو عينيه",
                    text = "في شبابي، يا أصيل، كنت أظن أن العالم الكبير يكمن في المدن الصاخبة والبعيدة. لكنني اكتشفت مع الأيام أن العالم الحقيقي هو المكان الذي تستطيع فيه أن تبني أثراً، وأن تحافظ فيه على كلمة طيبة وإرث شريف. هنا في الروضة، بين أهلنا وفي شبوة العز، نحن لا نبيع مجرد حلوى أو نزرع مجرد أرض... نحن نحفظ هوية وكرامة عائلة مسرور التي عُرفت بالصدق والجود.",
                    suggestedVoice = "Charon"
                ),
                DialogueLine(
                    speaker = "أصيل",
                    characterId = "aseel",
                    stageDirection = "تتحول ملامحه من الحيرة إلى التأمل الشديد",
                    text = "لكنني أخشى أن يمر العمر دون أن أحقق طموحي العالي يا أبتاه.",
                    suggestedVoice = "Puck"
                ),
                DialogueLine(
                    speaker = "العم يونس",
                    characterId = "younes",
                    stageDirection = "يبتسم بحكمة، ويمسك يد أصيل بإحكام",
                    text = "الطموح الحقيقي يا بني ليس في المكان الذي تقف فيه فحسب، بل في ماذا تبني وتترك خلفك. إن أردت السفر لتتعلّم وتكتسب الخبرة، فقلبي يدعو لك في كل خطوة... ولكن تذكّر دائماً: الشجرة التي تتنكر لجذورها تجف مع أول هبة ريح. أمّا من يحمل أرضه في قلبه، فإنه يزهر أينما حل، ويعود دائماً ليُسقي أرضه الأولى.",
                    suggestedVoice = "Charon"
                )
            ),
            thematicCore = "الحفاظ على هوية وكرامة الأجداد، والطموح الحقيقي يزهر بخدمة الأرض الأم."
        ),
        CinematicScene(
            id = 3,
            title = "المشهد الثالث: سطوح المنزل",
            subtitle = "سماء شبوة المرصعة بالنجوم والقرار الخالد",
            timeAndPlace = "خارجي / ليل — سطح منزل طيني تقليدي ذو حواف مسننة، مدينة الروضة",
            defaultImageRes = R.drawable.img_scene_starlit_rooftop,
            decorAndLocation = "سطح منزل طيني تقليدي ذو حواف مسننة. جلسة عربية مطرزة وسراج زيتي قديم يعطي ضوءاً خافتاً متراقصاً. في الخلفية تظهر البيوت الطينية لمدينة الروضة تحت قبة السماء المليئة بالنجوم.",
            lightingAndColors = "إضاءة ليلية سينمائية بدرجات الأزرق النيلي المحيطة، مع بؤرة إضاءة دافئة جداً مصدرها السراج الزيتي على وجهي الأب والابن لخلق تباين بصري ساحر بين الأزرق والبرتقالي الدافئ.",
            cameraShots = listOf(
                CameraShot(
                    type = "لقطة واسعة رافعة (Crane Up Shot)",
                    tag = "Crane",
                    description = "تبدأ من مستوى السطح وتلتقط الأب والابن وهما ينظران إلى المنازل الطينية والنجوم، ثم تنزل لمستواهما."
                ),
                CameraShot(
                    type = "لقطة قريبة جداً (Extreme Close-up)",
                    tag = "ECU",
                    description = "لعينين أصيل وهما تعكسان ضوء النجوم وسراج الزيت، إشارة لإشراقة الفكرة والقرار."
                ),
                CameraShot(
                    type = "لقطة دائرية كاملة (360-degree Orbit Shot)",
                    tag = "Orbit 360°",
                    description = "تدور الكاميرا بحركة حالمة ومستمرة حول الأب والابن لحظة العناق والاتفاق."
                )
            ),
            soundAndMusic = "صوت النسيم العليل، مع صدى خفيف لأصوات الليل الهادئة. تصاعد تدريجي لأوركسترا موسيقية تجمع العود والآلات الوترية بنغمة ملحمية دافئة تعبر عن الانتماء والاعتزاز.",
            musicPrompt = "An epic, emotional cinematic orchestral build-up with traditional Yemeni Oud, soaring strings, desert wind, and majestic starlit night ambience.",
            dialogues = listOf(
                DialogueLine(
                    speaker = "أصيل",
                    characterId = "aseel",
                    stageDirection = "يأخذ نفساً عميقاً ويبتسم بوضوح وارتياح، يلتفت لوالده بثبات",
                    text = "لقد اتخذت قراري يا أبتاه.",
                    suggestedVoice = "Puck"
                ),
                DialogueLine(
                    speaker = "العم يونس",
                    characterId = "younes",
                    stageDirection = "الكاميرا تلتقط وجهه بنصف إضاءة دافئة ونصف أزرق ليلي",
                    text = "وماذا يملي عليك قلبك يا أصيل يونس مسرور؟",
                    suggestedVoice = "Charon"
                ),
                DialogueLine(
                    speaker = "أصيل",
                    characterId = "aseel",
                    stageDirection = "بنبرة حازمة وممتلئة بالأمل والشغف",
                    text = "سأسافر للتدريب وتطوير مهاراتي لعام واحد فقط... سأتعلّم أحدث تقنيات الإدارة والتسويق، ثم سأعود فوراً إلى هنا. سأعود لأطور أرضنا وتجارتنا، ولنضع اسم \"مسرور\" و\"الحلوى الروضية\" في كل مكان بأسلوب حديث، دون أن نفقد ذرة واحدة من أصلنا وطيب معدننا الشبواني.",
                    suggestedVoice = "Puck"
                ),
                DialogueLine(
                    speaker = "العم يونس",
                    characterId = "younes",
                    stageDirection = "يتأثر بشكل واضح، تترقرق في عينيه أضواء النجوم، ويقوم بضم أصيل بحرارة",
                    text = "هذا هو أصيل الذي عرفته ورعيته... يحلق بعيداً بفكره وطموحه، ويبقى قلبه ثابتاً في أرض الروضة. اذهب يا بني بحفظ الله ورعايته، فكل شبوة تنتظر عودتك لتزهر بك.",
                    suggestedVoice = "Charon"
                )
            ),
            thematicCore = "التحليق بالطموح مع بقاء القلب ثابتاً في أرض الروضة وشبوة."
        )
    )
}
