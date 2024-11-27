//package tb.oss.tafsir.service
//
//import cats.effect.IO
//import org.scalatest.funsuite.AnyFunSuite
//import org.scalatest.matchers.should.Matchers
//import tb.oss.tafsir.service.Client.*
//import cats.effect.unsafe.implicits.global
//
//class TafsirServiceSpec extends AnyFunSuite with Matchers {
//
//  val tafsirList: List[Tafsir] = List(
//    Tafsir(1, "Tafsir Ibn Kathir", "Arabic", "Ibn Kathir", "Tafsir Al-Quran Al-Azim"),
//    Tafsir(2, "Tafsir Al-Jalalayn", "Arabic", "Al-Mahalli and Al-Suyuti", "Tafsir Al-Jalalayn")
//  )
//
//  val surahs: List[Surah] = List(Surah(1, "Al-Fatihah"), Surah(2, "Al-Baqarah"), Surah(3, "Aal-Imrane"))
//
//  val ayah: Ayah = Ayah(1, "الفاتحة", 1, "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ")
//
//  val ayahInterpretation: AyahInterpretation = AyahInterpretation(
//    1,
//    "التفسير الميسر",
//    "/quran/1/2/",
//    2,
//    "(الحَمْدُ للهِ رَبِّ العَالَمِينَ) الثناء على الله بصفاته التي كلُّها أوصاف كمال، وبنعمه الظاهرة والباطنة، الدينية والدنيوية، وفي ضمنه أَمْرٌ لعباده أن يحمدوه، فهو المستحق له وحده، وهو سبحانه المنشئ للخلق، القائم بأمورهم، المربي لجميع خلقه بنعمه، ولأوليائه بالإيمان والعمل الصالح."
//  )
//
//  def client(
//    getTafsirListFn: => IO[List[Tafsir]] = IO(tafsirList),
//    getAyahFn: => IO[Ayah] = IO(ayah),
//    getAyahInterpretationFn: => IO[AyahInterpretation] = IO(ayahInterpretation),
//    getSurahsFn: => IO[List[Surah]] = IO(surahs)
//  ): Client[IO] = new Client[IO] {
//    override def getTafsirList: IO[List[Tafsir]] = getTafsirListFn
//
//    override def getAyah(surahNumber: Int, ayahNumber: Int): IO[Ayah] = getAyahFn
//
//    override def getSurahs: IO[List[Client.Surah]] = getSurahsFn
//
//    override def getAyahInterpretation(tafsirId: Int, surahNumber: Int, ayahNumber: Int): IO[AyahInterpretation] =
//      getAyahInterpretationFn
//  }
//
//  test("It should return the tafsir list") {
//    val service = TafsirService.impl[IO](client(getTafsirListFn = IO(tafsirList)))
//    service.getTafsirList.unsafeRunAsync {
//      case Right(result) => result should be(tafsirList)
//      case Left(error)   => fail("Should not happen")
//    }
//  }
//
//  test("It should return an error when the client fails while trying to retrieve the tafsir list") {
//    val service =
//      TafsirService.impl[IO](client(getTafsirListFn = IO.raiseError(new Throwable("Cannot get tafsir list"))))
//    service.getTafsirList.unsafeRunAsync {
//      case Right(result) => fail("Should not happen")
//      case Left(error)   => error.getMessage should be("Cannot get tafsir list")
//    }
//  }
//
//  test("It should return all surahs") {
//    val service = TafsirService.impl[IO](client(getSurahsFn = IO(surahs)))
//    service.getSurahs.unsafeRunAsync {
//      case Right(result) => result should be(surahs)
//      case Left(error)   => fail("Should not happen")
//    }
//  }
//
//  test("It should return an error when the client fails while trying to retrieve all surahs") {
//    val service =
//      TafsirService.impl[IO](client(getSurahsFn = IO.raiseError(new Throwable("Cannot get surahs"))))
//    service.getSurahs.unsafeRunAsync {
//      case Right(result) => fail("Should not happen")
//      case Left(error)   => error.getMessage should be("Cannot get surahs")
//    }
//  }
//
//  test("It should return an ayah") {
//    val service = TafsirService.impl[IO](client(getAyahFn = IO(ayah)))
//    service.getAyah(1, 2).unsafeRunAsync {
//      case Right(result) => result should be(ayah)
//      case Left(error)   => fail("Should not happen")
//    }
//  }
//
//  test("It should return an error when the client fails while trying to retrieve an ayah") {
//    val service = TafsirService.impl[IO](client(getAyahFn = IO.raiseError(new Throwable("Cannot get ayah"))))
//    service.getAyah(1, 2).unsafeRunAsync {
//      case Right(result) => fail("Should not happen")
//      case Left(error)   => error.getMessage should be("Cannot get ayah")
//    }
//  }
//
//  test("It should return an ayah interpretation") {
//    val service = TafsirService.impl[IO](client(getAyahInterpretationFn = IO(ayahInterpretation)))
//    service.getAyahInterpretation(1, 1, 2).unsafeRunAsync {
//      case Right(result) => result should be(ayahInterpretation)
//      case Left(error)   => fail("Should not happen")
//    }
//  }
//
//  test("It should return an error when the client fails while trying to retrieve an ayah interpretation") {
//    val service = TafsirService.impl[IO](client(getAyahInterpretationFn = IO.raiseError(new Throwable("Cannot get ayah interpretation"))))
//    service.getAyahInterpretation(1, 1, 2).unsafeRunAsync {
//      case Right(result) => fail("Should not happen")
//      case Left(error)   => error.getMessage should be("Cannot get ayah interpretation")
//    }
//  }
//}
