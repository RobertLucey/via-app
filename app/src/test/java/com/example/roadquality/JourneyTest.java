package com.example.roadquality;

import static org.junit.Assert.assertEquals;

import com.example.roadquality.models.Journey;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JourneyTest {

    String testJourney = "{\"minutesToCut\":5,\"metresToCut\":500,\"sendRelativeTime\":true,\"data\":[{\"acc\":[],\"time\":0,\"gps\":{\"elevation\":null,\"lng\":-6.2530891,\"lat\":53.3588887}},{\"acc\":[],\"time\":10,\"gps\":{\"elevation\":null,\"lng\":-6.2533216,\"lat\":53.3584649}},{\"acc\":[],\"time\":20,\"gps\":{\"elevation\":null,\"lng\":-6.253461,\"lat\":53.358193}},{\"acc\":[],\"time\":30,\"gps\":{\"elevation\":null,\"lng\":-6.2534842,\"lat\":53.3581476}},{\"acc\":[],\"time\":40,\"gps\":{\"elevation\":null,\"lng\":-6.2526218,\"lat\":53.3579793}},{\"acc\":[],\"time\":50,\"gps\":{\"elevation\":null,\"lng\":-6.2523619,\"lat\":53.3579255}},{\"acc\":[],\"time\":60,\"gps\":{\"elevation\":null,\"lng\":-6.2525459,\"lat\":53.3577423}},{\"acc\":[],\"time\":70,\"gps\":{\"elevation\":null,\"lng\":-6.2526408,\"lat\":53.357707}},{\"acc\":[],\"time\":80,\"gps\":{\"elevation\":null,\"lng\":-6.2536058,\"lat\":53.3573489}},{\"acc\":[],\"time\":90,\"gps\":{\"elevation\":null,\"lng\":-6.2540083,\"lat\":53.3572005}},{\"acc\":[],\"time\":100,\"gps\":{\"elevation\":null,\"lng\":-6.2541836,\"lat\":53.3571312}},{\"acc\":[],\"time\":110,\"gps\":{\"elevation\":null,\"lng\":-6.2542868,\"lat\":53.3570936}},{\"acc\":[],\"time\":120,\"gps\":{\"elevation\":null,\"lng\":-6.2545955,\"lat\":53.3569809}},{\"acc\":[],\"time\":130,\"gps\":{\"elevation\":null,\"lng\":-6.2546764,\"lat\":53.3569514}},{\"acc\":[],\"time\":140,\"gps\":{\"elevation\":null,\"lng\":-6.2549334,\"lat\":53.3568576}},{\"acc\":[],\"time\":150,\"gps\":{\"elevation\":null,\"lng\":-6.2559997,\"lat\":53.3564663}},{\"acc\":[],\"time\":160,\"gps\":{\"elevation\":null,\"lng\":-6.2558978,\"lat\":53.3563727}},{\"acc\":[],\"time\":170,\"gps\":{\"elevation\":null,\"lng\":-6.257938,\"lat\":53.3556449}},{\"acc\":[],\"time\":180,\"gps\":{\"elevation\":null,\"lng\":-6.2574727,\"lat\":53.3550156}},{\"acc\":[],\"time\":190,\"gps\":{\"elevation\":null,\"lng\":-6.2574232,\"lat\":53.3549528}},{\"acc\":[],\"time\":200,\"gps\":{\"elevation\":null,\"lng\":-6.257203,\"lat\":53.35465}},{\"acc\":[],\"time\":210,\"gps\":{\"elevation\":null,\"lng\":-6.2568679,\"lat\":53.3541819}},{\"acc\":[],\"time\":220,\"gps\":{\"elevation\":null,\"lng\":-6.2577681,\"lat\":53.3539818}},{\"acc\":[],\"time\":230,\"gps\":{\"elevation\":null,\"lng\":-6.2581941,\"lat\":53.3538836}},{\"acc\":[],\"time\":240,\"gps\":{\"elevation\":null,\"lng\":-6.2584321,\"lat\":53.3538163}},{\"acc\":[],\"time\":250,\"gps\":{\"elevation\":null,\"lng\":-6.2591722,\"lat\":53.3535786}},{\"acc\":[],\"time\":260,\"gps\":{\"elevation\":null,\"lng\":-6.2594117,\"lat\":53.3534972}},{\"acc\":[],\"time\":270,\"gps\":{\"elevation\":null,\"lng\":-6.259676,\"lat\":53.3533742}},{\"acc\":[],\"time\":280,\"gps\":{\"elevation\":null,\"lng\":-6.2600954,\"lat\":53.3531925}},{\"acc\":[],\"time\":290,\"gps\":{\"elevation\":null,\"lng\":-6.2608812,\"lat\":53.3528433}},{\"acc\":[],\"time\":300,\"gps\":{\"elevation\":null,\"lng\":-6.2610318,\"lat\":53.3527764}},{\"acc\":[],\"time\":310,\"gps\":{\"elevation\":null,\"lng\":-6.2613445,\"lat\":53.352555}},{\"acc\":[],\"time\":320,\"gps\":{\"elevation\":null,\"lng\":-6.2610744,\"lat\":53.3519419}},{\"acc\":[],\"time\":330,\"gps\":{\"elevation\":null,\"lng\":-6.2603792,\"lat\":53.3503787}},{\"acc\":[],\"time\":340,\"gps\":{\"elevation\":null,\"lng\":-6.260128,\"lat\":53.349819}},{\"acc\":[],\"time\":350,\"gps\":{\"elevation\":null,\"lng\":-6.2597226,\"lat\":53.3488709}},{\"acc\":[],\"time\":360,\"gps\":{\"elevation\":null,\"lng\":-6.2595282,\"lat\":53.3484455}},{\"acc\":[],\"time\":370,\"gps\":{\"elevation\":null,\"lng\":-6.2595044,\"lat\":53.3483991}},{\"acc\":[],\"time\":380,\"gps\":{\"elevation\":null,\"lng\":-6.2591013,\"lat\":53.3475905}},{\"acc\":[],\"time\":390,\"gps\":{\"elevation\":null,\"lng\":-6.258792,\"lat\":53.3470086}},{\"acc\":[],\"time\":400,\"gps\":{\"elevation\":null,\"lng\":-6.25911,\"lat\":53.3469351}},{\"acc\":[],\"time\":410,\"gps\":{\"elevation\":null,\"lng\":-6.2593428,\"lat\":53.3468825}},{\"acc\":[],\"time\":420,\"gps\":{\"elevation\":null,\"lng\":-6.2600776,\"lat\":53.3467107}},{\"acc\":[],\"time\":430,\"gps\":{\"elevation\":null,\"lng\":-6.2607335,\"lat\":53.3465574}},{\"acc\":[],\"time\":440,\"gps\":{\"elevation\":null,\"lng\":-6.2618852,\"lat\":53.3462881}},{\"acc\":[],\"time\":450,\"gps\":{\"elevation\":null,\"lng\":-6.2626132,\"lat\":53.3461284}},{\"acc\":[],\"time\":460,\"gps\":{\"elevation\":null,\"lng\":-6.2625161,\"lat\":53.3456213}},{\"acc\":[],\"time\":470,\"gps\":{\"elevation\":null,\"lng\":-6.2628751,\"lat\":53.3456018}},{\"acc\":[],\"time\":480,\"gps\":{\"elevation\":null,\"lng\":-6.2627671,\"lat\":53.3448972}},{\"acc\":[],\"time\":490,\"gps\":{\"elevation\":null,\"lng\":-6.2633611,\"lat\":53.3448847}},{\"acc\":[],\"time\":500,\"gps\":{\"elevation\":null,\"lng\":-6.2633394,\"lat\":53.3442105}},{\"acc\":[],\"time\":510,\"gps\":{\"elevation\":null,\"lng\":-6.26387,\"lat\":53.3441912}},{\"acc\":[],\"time\":520,\"gps\":{\"elevation\":null,\"lng\":-6.2642788,\"lat\":53.3441799}},{\"acc\":[],\"time\":530,\"gps\":{\"elevation\":null,\"lng\":-6.2644509,\"lat\":53.3441775}},{\"acc\":[],\"time\":540,\"gps\":{\"elevation\":null,\"lng\":-6.26444,\"lat\":53.3438399}},{\"acc\":[],\"time\":550,\"gps\":{\"elevation\":null,\"lng\":-6.2644216,\"lat\":53.3429968}},{\"acc\":[],\"time\":560,\"gps\":{\"elevation\":null,\"lng\":-6.2646944,\"lat\":53.3421317}},{\"acc\":[],\"time\":570,\"gps\":{\"elevation\":null,\"lng\":-6.265404,\"lat\":53.3414662}},{\"acc\":[],\"time\":580,\"gps\":{\"elevation\":null,\"lng\":-6.2656141,\"lat\":53.3406712}},{\"acc\":[],\"time\":590,\"gps\":{\"elevation\":null,\"lng\":-6.2656749,\"lat\":53.3404613}},{\"acc\":[],\"time\":600,\"gps\":{\"elevation\":null,\"lng\":-6.2657997,\"lat\":53.3398737}},{\"acc\":[],\"time\":610,\"gps\":{\"elevation\":null,\"lng\":-6.2658639,\"lat\":53.3395894}},{\"acc\":[],\"time\":620,\"gps\":{\"elevation\":null,\"lng\":-6.2659588,\"lat\":53.3391577}},{\"acc\":[],\"time\":630,\"gps\":{\"elevation\":null,\"lng\":-6.2661022,\"lat\":53.338532}},{\"acc\":[],\"time\":640,\"gps\":{\"elevation\":null,\"lng\":-6.2660752,\"lat\":53.3383892}},{\"acc\":[],\"time\":650,\"gps\":{\"elevation\":null,\"lng\":-6.2658551,\"lat\":53.3377832}},{\"acc\":[],\"time\":660,\"gps\":{\"elevation\":null,\"lng\":-6.2657971,\"lat\":53.3376138}},{\"acc\":[],\"time\":670,\"gps\":{\"elevation\":null,\"lng\":-6.2657367,\"lat\":53.3374278}},{\"acc\":[],\"time\":680,\"gps\":{\"elevation\":null,\"lng\":-6.2656096,\"lat\":53.3369777}},{\"acc\":[],\"time\":690,\"gps\":{\"elevation\":null,\"lng\":-6.2654495,\"lat\":53.3364281}},{\"acc\":[],\"time\":700,\"gps\":{\"elevation\":null,\"lng\":-6.2653568,\"lat\":53.336174}},{\"acc\":[],\"time\":710,\"gps\":{\"elevation\":null,\"lng\":-6.2652056,\"lat\":53.3354833}},{\"acc\":[],\"time\":720,\"gps\":{\"elevation\":null,\"lng\":-6.2652546,\"lat\":53.3347258}},{\"acc\":[],\"time\":730,\"gps\":{\"elevation\":null,\"lng\":-6.2652779,\"lat\":53.3341412}},{\"acc\":[],\"time\":740,\"gps\":{\"elevation\":null,\"lng\":-6.2651742,\"lat\":53.333871}},{\"acc\":[],\"time\":750,\"gps\":{\"elevation\":null,\"lng\":-6.265012,\"lat\":53.3337555}},{\"acc\":[],\"time\":760,\"gps\":{\"elevation\":null,\"lng\":-6.2650038,\"lat\":53.3335892}},{\"acc\":[],\"time\":770,\"gps\":{\"elevation\":null,\"lng\":-6.2647978,\"lat\":53.332599}}],\"transport_type\":\"mountain\",\"uuid\":\"abc46949-7f23-474f-bf7c-e313f3c74e5d\",\"is_culled\":false,\"suspension\":true}";

    @Test
    public void parse() throws JSONException {
        Journey journey = Journey.parse(testJourney);

        JSONObject actual = journey.getJSON(true, false);
        actual.remove("uuid");

        assertEquals(
                actual.toString(),
                "{\"data\":[{\"acc\":[0,0,0],\"gps\":[53.3588887,-6.2530891],\"time\":0},{\"acc\":[0,0,0],\"gps\":[53.3584649,-6.2533216],\"time\":10},{\"acc\":[0,0,0],\"gps\":[53.358193,-6.253461],\"time\":20},{\"acc\":[0,0,0],\"gps\":[53.3581476,-6.2534842],\"time\":30},{\"acc\":[0,0,0],\"gps\":[53.3579793,-6.2526218],\"time\":40},{\"acc\":[0,0,0],\"gps\":[53.3579255,-6.2523619],\"time\":50},{\"acc\":[0,0,0],\"gps\":[53.3577423,-6.2525459],\"time\":60},{\"acc\":[0,0,0],\"gps\":[53.357707,-6.2526408],\"time\":70},{\"acc\":[0,0,0],\"gps\":[53.3573489,-6.2536058],\"time\":80},{\"acc\":[0,0,0],\"gps\":[53.3572005,-6.2540083],\"time\":90},{\"acc\":[0,0,0],\"gps\":[53.3571312,-6.2541836],\"time\":100},{\"acc\":[0,0,0],\"gps\":[53.3570936,-6.2542868],\"time\":110},{\"acc\":[0,0,0],\"gps\":[53.3569809,-6.2545955],\"time\":120},{\"acc\":[0,0,0],\"gps\":[53.3569514,-6.2546764],\"time\":130},{\"acc\":[0,0,0],\"gps\":[53.3568576,-6.2549334],\"time\":140},{\"acc\":[0,0,0],\"gps\":[53.3564663,-6.2559997],\"time\":150},{\"acc\":[0,0,0],\"gps\":[53.3563727,-6.2558978],\"time\":160},{\"acc\":[0,0,0],\"gps\":[53.3556449,-6.257938],\"time\":170},{\"acc\":[0,0,0],\"gps\":[53.3550156,-6.2574727],\"time\":180},{\"acc\":[0,0,0],\"gps\":[53.3549528,-6.2574232],\"time\":190},{\"acc\":[0,0,0],\"gps\":[53.35465,-6.257203],\"time\":200},{\"acc\":[0,0,0],\"gps\":[53.3541819,-6.2568679],\"time\":210},{\"acc\":[0,0,0],\"gps\":[53.3539818,-6.2577681],\"time\":220},{\"acc\":[0,0,0],\"gps\":[53.3538836,-6.2581941],\"time\":230},{\"acc\":[0,0,0],\"gps\":[53.3538163,-6.2584321],\"time\":240},{\"acc\":[0,0,0],\"gps\":[53.3535786,-6.2591722],\"time\":250},{\"acc\":[0,0,0],\"gps\":[53.3534972,-6.2594117],\"time\":260},{\"acc\":[0,0,0],\"gps\":[53.3533742,-6.259676],\"time\":270},{\"acc\":[0,0,0],\"gps\":[53.3531925,-6.2600954],\"time\":280},{\"acc\":[0,0,0],\"gps\":[53.3528433,-6.2608812],\"time\":290},{\"acc\":[0,0,0],\"gps\":[53.3527764,-6.2610318],\"time\":300},{\"acc\":[0,0,0],\"gps\":[53.352555,-6.2613445],\"time\":310},{\"acc\":[0,0,0],\"gps\":[53.3519419,-6.2610744],\"time\":320},{\"acc\":[0,0,0],\"gps\":[53.3503787,-6.2603792],\"time\":330},{\"acc\":[0,0,0],\"gps\":[53.349819,-6.260128],\"time\":340},{\"acc\":[0,0,0],\"gps\":[53.3488709,-6.2597226],\"time\":350},{\"acc\":[0,0,0],\"gps\":[53.3484455,-6.2595282],\"time\":360},{\"acc\":[0,0,0],\"gps\":[53.3483991,-6.2595044],\"time\":370},{\"acc\":[0,0,0],\"gps\":[53.3475905,-6.2591013],\"time\":380},{\"acc\":[0,0,0],\"gps\":[53.3470086,-6.258792],\"time\":390},{\"acc\":[0,0,0],\"gps\":[53.3469351,-6.25911],\"time\":400},{\"acc\":[0,0,0],\"gps\":[53.3468825,-6.2593428],\"time\":410},{\"acc\":[0,0,0],\"gps\":[53.3467107,-6.2600776],\"time\":420},{\"acc\":[0,0,0],\"gps\":[53.3465574,-6.2607335],\"time\":430},{\"acc\":[0,0,0],\"gps\":[53.3462881,-6.2618852],\"time\":440},{\"acc\":[0,0,0],\"gps\":[53.3461284,-6.2626132],\"time\":450},{\"acc\":[0,0,0],\"gps\":[53.3456213,-6.2625161],\"time\":460},{\"acc\":[0,0,0],\"gps\":[53.3456018,-6.2628751],\"time\":470},{\"acc\":[0,0,0],\"gps\":[53.3448972,-6.2627671],\"time\":480},{\"acc\":[0,0,0],\"gps\":[53.3448847,-6.2633611],\"time\":490},{\"acc\":[0,0,0],\"gps\":[53.3442105,-6.2633394],\"time\":500},{\"acc\":[0,0,0],\"gps\":[53.3441912,-6.26387],\"time\":510},{\"acc\":[0,0,0],\"gps\":[53.3441799,-6.2642788],\"time\":520},{\"acc\":[0,0,0],\"gps\":[53.3441775,-6.2644509],\"time\":530},{\"acc\":[0,0,0],\"gps\":[53.3438399,-6.26444],\"time\":540},{\"acc\":[0,0,0],\"gps\":[53.3429968,-6.2644216],\"time\":550},{\"acc\":[0,0,0],\"gps\":[53.3421317,-6.2646944],\"time\":560},{\"acc\":[0,0,0],\"gps\":[53.3414662,-6.265404],\"time\":570},{\"acc\":[0,0,0],\"gps\":[53.3406712,-6.2656141],\"time\":580},{\"acc\":[0,0,0],\"gps\":[53.3404613,-6.2656749],\"time\":590},{\"acc\":[0,0,0],\"gps\":[53.3398737,-6.2657997],\"time\":600},{\"acc\":[0,0,0],\"gps\":[53.3395894,-6.2658639],\"time\":610},{\"acc\":[0,0,0],\"gps\":[53.3391577,-6.2659588],\"time\":620},{\"acc\":[0,0,0],\"gps\":[53.338532,-6.2661022],\"time\":630},{\"acc\":[0,0,0],\"gps\":[53.3383892,-6.2660752],\"time\":640},{\"acc\":[0,0,0],\"gps\":[53.3377832,-6.2658551],\"time\":650},{\"acc\":[0,0,0],\"gps\":[53.3376138,-6.2657971],\"time\":660},{\"acc\":[0,0,0],\"gps\":[53.3374278,-6.2657367],\"time\":670},{\"acc\":[0,0,0],\"gps\":[53.3369777,-6.2656096],\"time\":680},{\"acc\":[0,0,0],\"gps\":[53.3364281,-6.2654495],\"time\":690},{\"acc\":[0,0,0],\"gps\":[53.336174,-6.2653568],\"time\":700},{\"acc\":[0,0,0],\"gps\":[53.3354833,-6.2652056],\"time\":710},{\"acc\":[0,0,0],\"gps\":[53.3347258,-6.2652546],\"time\":720},{\"acc\":[0,0,0],\"gps\":[53.3341412,-6.2652779],\"time\":730},{\"acc\":[0,0,0],\"gps\":[53.333871,-6.2651742],\"time\":740},{\"acc\":[0,0,0],\"gps\":[53.3337555,-6.265012],\"time\":750},{\"acc\":[0,0,0],\"gps\":[53.3335892,-6.2650038],\"time\":760},{\"acc\":[0,0,0],\"gps\":[53.332599,-6.2647978],\"time\":770}],\"metresToCut\":500,\"sendRelativeTime\":true,\"transport_type\":\"mountain\",\"minutesToCut\":5,\"device\":\"phone\",\"is_culled\":false,\"suspension\":true}"
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

}
