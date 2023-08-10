package via.android.roadquality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.os.Environment;

import via.android.roadquality.models.Journey;
import via.android.roadquality.models.Journeys;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.util.ArrayList;

@PowerMockIgnore("jdk.internal.reflect.*")
@RunWith(PowerMockRunner.class)
// this static methods to be mocked are on Environment so that must be 'prepared'
@PrepareForTest({Environment.class})
public class JourneyTest {


    @Rule
    public TemporaryFolder storageDirectory = new TemporaryFolder();

    private File nonExistentDirectory;
    private File existentDirectory;

    @Before
    public void setup() {
        nonExistentDirectory = Mockito.mock(File.class);
        Mockito.when(nonExistentDirectory.exists()).thenReturn(false);

        existentDirectory = storageDirectory.getRoot();

        PowerMockito.mockStatic(Environment.class);
    }

    String testJourney = "{\"minutes_to_cut\":5,\"metres_to_cut\":500,\"send_relative_time\":true,\"data\":[{\"acc\":1.1,\"time\":0,\"gps\":{\"elevation\":null,\"lng\":-6.2530891,\"lat\":53.3588887}},{\"acc\":1.1,\"time\":10,\"gps\":{\"elevation\":null,\"lng\":-6.2533216,\"lat\":53.3584649}},{\"acc\":1.1,\"time\":20,\"gps\":{\"elevation\":null,\"lng\":-6.253461,\"lat\":53.358193}},{\"acc\":1.1,\"time\":30,\"gps\":{\"elevation\":null,\"lng\":-6.2534842,\"lat\":53.3581476}},{\"acc\":1.1,\"time\":40,\"gps\":{\"elevation\":null,\"lng\":-6.2526218,\"lat\":53.3579793}},{\"acc\":1.1,\"time\":50,\"gps\":{\"elevation\":null,\"lng\":-6.2523619,\"lat\":53.3579255}},{\"acc\":1.1,\"time\":60,\"gps\":{\"elevation\":null,\"lng\":-6.2525459,\"lat\":53.3577423}},{\"acc\":1.1,\"time\":70,\"gps\":{\"elevation\":null,\"lng\":-6.2526408,\"lat\":53.357707}},{\"acc\":1.1,\"time\":80,\"gps\":{\"elevation\":null,\"lng\":-6.2536058,\"lat\":53.3573489}},{\"acc\":1.1,\"time\":90,\"gps\":{\"elevation\":null,\"lng\":-6.2540083,\"lat\":53.3572005}},{\"acc\":1.1,\"time\":100,\"gps\":{\"elevation\":null,\"lng\":-6.2541836,\"lat\":53.3571312}},{\"acc\":1.1,\"time\":110,\"gps\":{\"elevation\":null,\"lng\":-6.2542868,\"lat\":53.3570936}},{\"acc\":1.1,\"time\":120,\"gps\":{\"elevation\":null,\"lng\":-6.2545955,\"lat\":53.3569809}},{\"acc\":1.1,\"time\":130,\"gps\":{\"elevation\":null,\"lng\":-6.2546764,\"lat\":53.3569514}},{\"acc\":1.1,\"time\":140,\"gps\":{\"elevation\":null,\"lng\":-6.2549334,\"lat\":53.3568576}},{\"acc\":1.1,\"time\":150,\"gps\":{\"elevation\":null,\"lng\":-6.2559997,\"lat\":53.3564663}},{\"acc\":1.1,\"time\":160,\"gps\":{\"elevation\":null,\"lng\":-6.2558978,\"lat\":53.3563727}},{\"acc\":1.1,\"time\":170,\"gps\":{\"elevation\":null,\"lng\":-6.257938,\"lat\":53.3556449}},{\"acc\":1.1,\"time\":180,\"gps\":{\"elevation\":null,\"lng\":-6.2574727,\"lat\":53.3550156}},{\"acc\":1.1,\"time\":190,\"gps\":{\"elevation\":null,\"lng\":-6.2574232,\"lat\":53.3549528}},{\"acc\":1.1,\"time\":200,\"gps\":{\"elevation\":null,\"lng\":-6.257203,\"lat\":53.35465}},{\"acc\":1.1,\"time\":210,\"gps\":{\"elevation\":null,\"lng\":-6.2568679,\"lat\":53.3541819}},{\"acc\":1.1,\"time\":220,\"gps\":{\"elevation\":null,\"lng\":-6.2577681,\"lat\":53.3539818}},{\"acc\":1.1,\"time\":230,\"gps\":{\"elevation\":null,\"lng\":-6.2581941,\"lat\":53.3538836}},{\"acc\":1.1,\"time\":240,\"gps\":{\"elevation\":null,\"lng\":-6.2584321,\"lat\":53.3538163}},{\"acc\":1.1,\"time\":250,\"gps\":{\"elevation\":null,\"lng\":-6.2591722,\"lat\":53.3535786}},{\"acc\":1.1,\"time\":260,\"gps\":{\"elevation\":null,\"lng\":-6.2594117,\"lat\":53.3534972}},{\"acc\":1.1,\"time\":270,\"gps\":{\"elevation\":null,\"lng\":-6.259676,\"lat\":53.3533742}},{\"acc\":1.1,\"time\":280,\"gps\":{\"elevation\":null,\"lng\":-6.2600954,\"lat\":53.3531925}},{\"acc\":1.1,\"time\":290,\"gps\":{\"elevation\":null,\"lng\":-6.2608812,\"lat\":53.3528433}},{\"acc\":1.1,\"time\":300,\"gps\":{\"elevation\":null,\"lng\":-6.2610318,\"lat\":53.3527764}},{\"acc\":1.1,\"time\":310,\"gps\":{\"elevation\":null,\"lng\":-6.2613445,\"lat\":53.352555}},{\"acc\":1.1,\"time\":320,\"gps\":{\"elevation\":null,\"lng\":-6.2610744,\"lat\":53.3519419}},{\"acc\":1.1,\"time\":330,\"gps\":{\"elevation\":null,\"lng\":-6.2603792,\"lat\":53.3503787}},{\"acc\":1.1,\"time\":340,\"gps\":{\"elevation\":null,\"lng\":-6.260128,\"lat\":53.349819}},{\"acc\":1.1,\"time\":350,\"gps\":{\"elevation\":null,\"lng\":-6.2597226,\"lat\":53.3488709}},{\"acc\":1.1,\"time\":360,\"gps\":{\"elevation\":null,\"lng\":-6.2595282,\"lat\":53.3484455}},{\"acc\":1.1,\"time\":370,\"gps\":{\"elevation\":null,\"lng\":-6.2595044,\"lat\":53.3483991}},{\"acc\":1.1,\"time\":380,\"gps\":{\"elevation\":null,\"lng\":-6.2591013,\"lat\":53.3475905}},{\"acc\":1.1,\"time\":390,\"gps\":{\"elevation\":null,\"lng\":-6.258792,\"lat\":53.3470086}},{\"acc\":1.1,\"time\":400,\"gps\":{\"elevation\":null,\"lng\":-6.25911,\"lat\":53.3469351}},{\"acc\":1.1,\"time\":410,\"gps\":{\"elevation\":null,\"lng\":-6.2593428,\"lat\":53.3468825}},{\"acc\":1.1,\"time\":420,\"gps\":{\"elevation\":null,\"lng\":-6.2600776,\"lat\":53.3467107}},{\"acc\":1.1,\"time\":430,\"gps\":{\"elevation\":null,\"lng\":-6.2607335,\"lat\":53.3465574}},{\"acc\":1.1,\"time\":440,\"gps\":{\"elevation\":null,\"lng\":-6.2618852,\"lat\":53.3462881}},{\"acc\":1.1,\"time\":450,\"gps\":{\"elevation\":null,\"lng\":-6.2626132,\"lat\":53.3461284}},{\"acc\":1.1,\"time\":460,\"gps\":{\"elevation\":null,\"lng\":-6.2625161,\"lat\":53.3456213}},{\"acc\":1.1,\"time\":470,\"gps\":{\"elevation\":null,\"lng\":-6.2628751,\"lat\":53.3456018}},{\"acc\":1.1,\"time\":480,\"gps\":{\"elevation\":null,\"lng\":-6.2627671,\"lat\":53.3448972}},{\"acc\":1.1,\"time\":490,\"gps\":{\"elevation\":null,\"lng\":-6.2633611,\"lat\":53.3448847}},{\"acc\":1.1,\"time\":500,\"gps\":{\"elevation\":null,\"lng\":-6.2633394,\"lat\":53.3442105}},{\"acc\":1.1,\"time\":510,\"gps\":{\"elevation\":null,\"lng\":-6.26387,\"lat\":53.3441912}},{\"acc\":1.1,\"time\":520,\"gps\":{\"elevation\":null,\"lng\":-6.2642788,\"lat\":53.3441799}},{\"acc\":1.1,\"time\":530,\"gps\":{\"elevation\":null,\"lng\":-6.2644509,\"lat\":53.3441775}},{\"acc\":1.1,\"time\":540,\"gps\":{\"elevation\":null,\"lng\":-6.26444,\"lat\":53.3438399}},{\"acc\":1.1,\"time\":550,\"gps\":{\"elevation\":null,\"lng\":-6.2644216,\"lat\":53.3429968}},{\"acc\":1.1,\"time\":560,\"gps\":{\"elevation\":null,\"lng\":-6.2646944,\"lat\":53.3421317}},{\"acc\":1.1,\"time\":570,\"gps\":{\"elevation\":null,\"lng\":-6.265404,\"lat\":53.3414662}},{\"acc\":1.1,\"time\":580,\"gps\":{\"elevation\":null,\"lng\":-6.2656141,\"lat\":53.3406712}},{\"acc\":1.1,\"time\":590,\"gps\":{\"elevation\":null,\"lng\":-6.2656749,\"lat\":53.3404613}},{\"acc\":1.1,\"time\":600,\"gps\":{\"elevation\":null,\"lng\":-6.2657997,\"lat\":53.3398737}},{\"acc\":1.1,\"time\":610,\"gps\":{\"elevation\":null,\"lng\":-6.2658639,\"lat\":53.3395894}},{\"acc\":1.1,\"time\":620,\"gps\":{\"elevation\":null,\"lng\":-6.2659588,\"lat\":53.3391577}},{\"acc\":1.1,\"time\":630,\"gps\":{\"elevation\":null,\"lng\":-6.2661022,\"lat\":53.338532}},{\"acc\":1.1,\"time\":640,\"gps\":{\"elevation\":null,\"lng\":-6.2660752,\"lat\":53.3383892}},{\"acc\":1.1,\"time\":650,\"gps\":{\"elevation\":null,\"lng\":-6.2658551,\"lat\":53.3377832}},{\"acc\":1.1,\"time\":660,\"gps\":{\"elevation\":null,\"lng\":-6.2657971,\"lat\":53.3376138}},{\"acc\":1.1,\"time\":670,\"gps\":{\"elevation\":null,\"lng\":-6.2657367,\"lat\":53.3374278}},{\"acc\":1.1,\"time\":680,\"gps\":{\"elevation\":null,\"lng\":-6.2656096,\"lat\":53.3369777}},{\"acc\":1.1,\"time\":690,\"gps\":{\"elevation\":null,\"lng\":-6.2654495,\"lat\":53.3364281}},{\"acc\":1.1,\"time\":700,\"gps\":{\"elevation\":null,\"lng\":-6.2653568,\"lat\":53.336174}},{\"acc\":1.1,\"time\":710,\"gps\":{\"elevation\":null,\"lng\":-6.2652056,\"lat\":53.3354833}},{\"acc\":1.1,\"time\":720,\"gps\":{\"elevation\":null,\"lng\":-6.2652546,\"lat\":53.3347258}},{\"acc\":1.1,\"time\":730,\"gps\":{\"elevation\":null,\"lng\":-6.2652779,\"lat\":53.3341412}},{\"acc\":1.1,\"time\":740,\"gps\":{\"elevation\":null,\"lng\":-6.2651742,\"lat\":53.333871}},{\"acc\":1.1,\"time\":750,\"gps\":{\"elevation\":null,\"lng\":-6.265012,\"lat\":53.3337555}},{\"acc\":1.1,\"time\":760,\"gps\":{\"elevation\":null,\"lng\":-6.2650038,\"lat\":53.3335892}},{\"acc\":1.1,\"time\":770,\"gps\":{\"elevation\":null,\"lng\":-6.2647978,\"lat\":53.332599}}],\"transport_type\":\"mountain\",\"uuid\":\"abc46949-7f23-474f-bf7c-e313f3c74e5d\",\"is_partial\":false,\"is_culled\":false,\"suspension\":true}";

    @Test
    public void parse() throws JSONException {
        Journey journey = Journey.parse(testJourney);

        JSONObject actual = journey.getJSON(true, false);
        actual.remove("uuid");
        actual.remove("version");

        assertEquals(
                actual.toString(),
                "{\"data\":[{\"acc\":1.1,\"gps\":[53.3588887,-6.2530891],\"time\":0},{\"acc\":1.1,\"gps\":[53.3584649,-6.2533216],\"time\":10},{\"acc\":1.1,\"gps\":[53.358193,-6.253461],\"time\":20},{\"acc\":1.1,\"gps\":[53.3581476,-6.2534842],\"time\":30},{\"acc\":1.1,\"gps\":[53.3579793,-6.2526218],\"time\":40},{\"acc\":1.1,\"gps\":[53.3579255,-6.2523619],\"time\":50},{\"acc\":1.1,\"gps\":[53.3577423,-6.2525459],\"time\":60},{\"acc\":1.1,\"gps\":[53.357707,-6.2526408],\"time\":70},{\"acc\":1.1,\"gps\":[53.3573489,-6.2536058],\"time\":80},{\"acc\":1.1,\"gps\":[53.3572005,-6.2540083],\"time\":90},{\"acc\":1.1,\"gps\":[53.3571312,-6.2541836],\"time\":100},{\"acc\":1.1,\"gps\":[53.3570936,-6.2542868],\"time\":110},{\"acc\":1.1,\"gps\":[53.3569809,-6.2545955],\"time\":120},{\"acc\":1.1,\"gps\":[53.3569514,-6.2546764],\"time\":130},{\"acc\":1.1,\"gps\":[53.3568576,-6.2549334],\"time\":140},{\"acc\":1.1,\"gps\":[53.3564663,-6.2559997],\"time\":150},{\"acc\":1.1,\"gps\":[53.3563727,-6.2558978],\"time\":160},{\"acc\":1.1,\"gps\":[53.3556449,-6.257938],\"time\":170},{\"acc\":1.1,\"gps\":[53.3550156,-6.2574727],\"time\":180},{\"acc\":1.1,\"gps\":[53.3549528,-6.2574232],\"time\":190},{\"acc\":1.1,\"gps\":[53.35465,-6.257203],\"time\":200},{\"acc\":1.1,\"gps\":[53.3541819,-6.2568679],\"time\":210},{\"acc\":1.1,\"gps\":[53.3539818,-6.2577681],\"time\":220},{\"acc\":1.1,\"gps\":[53.3538836,-6.2581941],\"time\":230},{\"acc\":1.1,\"gps\":[53.3538163,-6.2584321],\"time\":240},{\"acc\":1.1,\"gps\":[53.3535786,-6.2591722],\"time\":250},{\"acc\":1.1,\"gps\":[53.3534972,-6.2594117],\"time\":260},{\"acc\":1.1,\"gps\":[53.3533742,-6.259676],\"time\":270},{\"acc\":1.1,\"gps\":[53.3531925,-6.2600954],\"time\":280},{\"acc\":1.1,\"gps\":[53.3528433,-6.2608812],\"time\":290},{\"acc\":1.1,\"gps\":[53.3527764,-6.2610318],\"time\":300},{\"acc\":1.1,\"gps\":[53.352555,-6.2613445],\"time\":310},{\"acc\":1.1,\"gps\":[53.3519419,-6.2610744],\"time\":320},{\"acc\":1.1,\"gps\":[53.3503787,-6.2603792],\"time\":330},{\"acc\":1.1,\"gps\":[53.349819,-6.260128],\"time\":340},{\"acc\":1.1,\"gps\":[53.3488709,-6.2597226],\"time\":350},{\"acc\":1.1,\"gps\":[53.3484455,-6.2595282],\"time\":360},{\"acc\":1.1,\"gps\":[53.3483991,-6.2595044],\"time\":370},{\"acc\":1.1,\"gps\":[53.3475905,-6.2591013],\"time\":380},{\"acc\":1.1,\"gps\":[53.3470086,-6.258792],\"time\":390},{\"acc\":1.1,\"gps\":[53.3469351,-6.25911],\"time\":400},{\"acc\":1.1,\"gps\":[53.3468825,-6.2593428],\"time\":410},{\"acc\":1.1,\"gps\":[53.3467107,-6.2600776],\"time\":420},{\"acc\":1.1,\"gps\":[53.3465574,-6.2607335],\"time\":430},{\"acc\":1.1,\"gps\":[53.3462881,-6.2618852],\"time\":440},{\"acc\":1.1,\"gps\":[53.3461284,-6.2626132],\"time\":450},{\"acc\":1.1,\"gps\":[53.3456213,-6.2625161],\"time\":460},{\"acc\":1.1,\"gps\":[53.3456018,-6.2628751],\"time\":470},{\"acc\":1.1,\"gps\":[53.3448972,-6.2627671],\"time\":480},{\"acc\":1.1,\"gps\":[53.3448847,-6.2633611],\"time\":490},{\"acc\":1.1,\"gps\":[53.3442105,-6.2633394],\"time\":500},{\"acc\":1.1,\"gps\":[53.3441912,-6.26387],\"time\":510},{\"acc\":1.1,\"gps\":[53.3441799,-6.2642788],\"time\":520},{\"acc\":1.1,\"gps\":[53.3441775,-6.2644509],\"time\":530},{\"acc\":1.1,\"gps\":[53.3438399,-6.26444],\"time\":540},{\"acc\":1.1,\"gps\":[53.3429968,-6.2644216],\"time\":550},{\"acc\":1.1,\"gps\":[53.3421317,-6.2646944],\"time\":560},{\"acc\":1.1,\"gps\":[53.3414662,-6.265404],\"time\":570},{\"acc\":1.1,\"gps\":[53.3406712,-6.2656141],\"time\":580},{\"acc\":1.1,\"gps\":[53.3404613,-6.2656749],\"time\":590},{\"acc\":1.1,\"gps\":[53.3398737,-6.2657997],\"time\":600},{\"acc\":1.1,\"gps\":[53.3395894,-6.2658639],\"time\":610},{\"acc\":1.1,\"gps\":[53.3391577,-6.2659588],\"time\":620},{\"acc\":1.1,\"gps\":[53.338532,-6.2661022],\"time\":630},{\"acc\":1.1,\"gps\":[53.3383892,-6.2660752],\"time\":640},{\"acc\":1.1,\"gps\":[53.3377832,-6.2658551],\"time\":650},{\"acc\":1.1,\"gps\":[53.3376138,-6.2657971],\"time\":660},{\"acc\":1.1,\"gps\":[53.3374278,-6.2657367],\"time\":670},{\"acc\":1.1,\"gps\":[53.3369777,-6.2656096],\"time\":680},{\"acc\":1.1,\"gps\":[53.3364281,-6.2654495],\"time\":690},{\"acc\":1.1,\"gps\":[53.336174,-6.2653568],\"time\":700},{\"acc\":1.1,\"gps\":[53.3354833,-6.2652056],\"time\":710},{\"acc\":1.1,\"gps\":[53.3347258,-6.2652546],\"time\":720},{\"acc\":1.1,\"gps\":[53.3341412,-6.2652779],\"time\":730},{\"acc\":1.1,\"gps\":[53.333871,-6.2651742],\"time\":740},{\"acc\":1.1,\"gps\":[53.3337555,-6.265012],\"time\":750},{\"acc\":1.1,\"gps\":[53.3335892,-6.2650038],\"time\":760},{\"acc\":1.1,\"gps\":[53.332599,-6.2647978],\"time\":770}],\"is_partial\":false,\"transport_type\":\"mountain\",\"send_relative_time\":true,\"device\":\"phone\",\"is_culled\":false,\"suspension\":true,\"minutes_to_cut\":5,\"metres_to_cut\":500}"
        );
    }

    @Test
    public void cullDistance() throws JSONException {
        Journey journey = Journey.parse(testJourney);
        assertEquals(journey.frames.size(), 78);
        journey.cullDistance();
        assertEquals(journey.frames.size(), 49);
    }

    @Test
    public void cullTime() throws JSONException {
        Journey journey = Journey.parse(testJourney);
        assertEquals(journey.frames.size(), 78);

        double originTime = journey.frames.get(0).time;
        double destinationTime = journey.frames.get(journey.frames.size() - 1).time;

        journey.cullTime(originTime, destinationTime);
        assertEquals(journey.frames.size(), 18);
    }

    @Test
    public void cull() throws JSONException {
        Journey journey = Journey.parse(testJourney);
        assertEquals(journey.frames.size(), 78);
        journey.cull();
        assertEquals(journey.frames.size(), 18);
    }

    @Test
    public void getPartials() throws Exception {
        Journey journey = Journey.parse(testJourney);

        Journeys partials = journey.getPartials();

        assertEquals(partials.journeys.size(), 12);

        ArrayList distances = new ArrayList<>();
        for (Journey j : partials.journeys) {
            distances.add(j.getIndirectDistance());
        }
        ArrayList dists = new ArrayList();
        for (Object thing : distances) {
            dists.add(Math.round((double) thing * 100.0) / 100.0);
        }
        assertEquals(dists.toString(), "[302.93, 0.0, 260.63, 180.17, 54.43, 115.66, 142.06, 142.73, 89.55, 125.22, 161.75, 129.45]");
    }

    @Test
    public void getPartialsReversed() throws Exception {
        // Some partials should reverse themselves so getting the direction of the journey can't be gotten

        Journey journey = Journey.parse(testJourney);

        Journeys partials1 = journey.getPartials();
        ArrayList first = new ArrayList<>();
        for (Journey j : partials1.journeys) {
            first.add(j.getJSON(true, false));
        }

        Journeys partials2 = journey.getPartials();
        ArrayList second = new ArrayList<>();
        for (Journey j : partials2.journeys) {
            second.add(j.getJSON(true, false));
        }
        assertNotEquals(first, second);

    }
}
