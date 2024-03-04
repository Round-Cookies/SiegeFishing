package me.asakura_kukii.siegefishing.config;

import me.asakura_kukii.lib.jackson.databind.annotation.JsonDeserialize;
import me.asakura_kukii.lib.jackson.databind.annotation.JsonSerialize;
import me.asakura_kukii.siegecore.io.PFile;
import me.asakura_kukii.siegecore.io.helper.PFileIdDeserializer;
import me.asakura_kukii.siegecore.io.helper.PFileIdSerializer;
import me.asakura_kukii.siegecore.io.helper.PVectorDeserializer;
import me.asakura_kukii.siegecore.io.helper.PVectorSerializer;
import me.asakura_kukii.siegecore.item.PAbstractItem;
import me.asakura_kukii.siegecore.util.math.PVector;
import me.asakura_kukii.siegefishing.effect.PParticle;
import me.asakura_kukii.siegefishing.effect.PSound;
import me.asakura_kukii.siegefishing.inventory.PSong;
import me.asakura_kukii.siegefishing.map.PFishMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PConfig extends PFile {

    public float bucketExperience = 5.0F;

    public List<String> bucketHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> bucketParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> bucketSound = new ArrayList<>();

    public List<String> insectPlaceHint = new ArrayList<>();

    public List<String> insectFightStartHint = new ArrayList<>();

    public List<String> insectWaitEjectHint = new ArrayList<>();

    public List<String> insectDamageDamageHint = new ArrayList<>();

    public List<String> insectHugeDamageDamageHint = new ArrayList<>();

    public List<String> insectDamageHugeDamageHint = new ArrayList<>();

    public List<String> insectDamageAvoidanceHint = new ArrayList<>();

    public List<String> insectHugeDamageAvoidanceHint = new ArrayList<>();

    public List<String> insectAvoidanceDamageHint = new ArrayList<>();

    public List<String> insectAvoidanceHugeDamageHint = new ArrayList<>();

    public List<String> insectAvoidanceAvoidanceHint = new ArrayList<>();

    public List<String> insectWinHint = new ArrayList<>();

    public List<String> insectWinHealthHint = new ArrayList<>();

    public List<String> insectWinAttackHint = new ArrayList<>();

    public List<String> insectWinAvoidanceHint = new ArrayList<>();

    public List<String> insectLoseHint = new ArrayList<>();

    public List<String> insectLoseHealthHint = new ArrayList<>();

    public List<String> insectLoseAttackHint = new ArrayList<>();

    public List<String> insectLoseAvoidanceHint = new ArrayList<>();

    public List<String> insectDrawHint = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> insectPlaceParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> insectPlaceSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> insectEjectParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> insectEjectSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> insectDamageParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> insectDamageSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> insectHugeDamageParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> insectHugeDamageSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> insectAvoidanceParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> insectAvoidanceSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> insectFightStartParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> insectFightStartSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> insectFightEndParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> insectFightEndSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PAbstractItem> lotteryBaitList =new ArrayList<>();

    public List<Integer> lotteryBaitCountList = new ArrayList<>();

    public List<Float> lotteryBaitWeightList = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PAbstractItem> lotteryBuffList = new ArrayList<>();

    public List<Integer> lotteryBuffCountList = new ArrayList<>();

    public List<Float> lotteryBuffWeightList = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PAbstractItem> lotteryRodLevel4List = new ArrayList<>();

    public List<Float> lotteryRodLevel4WeightList = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PAbstractItem> lotteryRodLevel5List = new ArrayList<>();

    public List<Float> lotteryRodLevel5WeightList = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> lotterySuccessParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> lotterySuccessSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> lotteryFailureParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> lotteryFailureSound = new ArrayList<>();

    public float lotteryCost = 160F;

    public static List<Float> lotteryWeightNormal = new ArrayList<>();

    public static List<Float> lotteryWeightForLevel4 = new ArrayList<>();

    public static List<Float> lotteryWeightForLevel5 = new ArrayList<>();

    static {
        lotteryWeightNormal.add(41.4F);
        lotteryWeightNormal.add(50.0F);
        lotteryWeightNormal.add(4F);
        lotteryWeightNormal.add(0.6F);
        lotteryWeightForLevel4.add(0F);
        lotteryWeightForLevel4.add(0F);
        lotteryWeightForLevel4.add(99.4F);
        lotteryWeightForLevel4.add(0.6F);
        lotteryWeightForLevel5.add(0F);
        lotteryWeightForLevel5.add(0F);
        lotteryWeightForLevel5.add(0F);
        lotteryWeightForLevel5.add(1F);
    }

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> purchaseSuccessSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> purchaseFailureSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> clickSound = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> bookSound = new ArrayList<>();

    @JsonSerialize(using = PVectorSerializer.class)
    @JsonDeserialize(using = PVectorDeserializer.class)
    public PVector spawn = new PVector();

    @JsonSerialize(using = PFileIdSerializer.class)
    @JsonDeserialize(using = PFileIdDeserializer.class)
    public PFishMap pFM = null;

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSong> hiddenSongList = new ArrayList<>();

    public List<String> songLogFormat = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PParticle> achievementParticle = new ArrayList<>();

    @JsonSerialize(contentUsing = PFileIdSerializer.class)
    @JsonDeserialize(contentUsing = PFileIdDeserializer.class)
    public List<PSound> achievementSound = new ArrayList<>();

    @JsonSerialize(using = PVectorSerializer.class)
    @JsonDeserialize(using = PVectorDeserializer.class)
    public PVector boatBias = new PVector();

    public float boatScale = 1.1F;

    public String achievementItemBroadcastFormat = "";

    public String achievementItemLogFormat = "";

    public String lotteryItemBroadcastFormat = "";

    public String showItemBroadCastFormat = "";

    @Override
    public void finalizeDeserialization() throws IOException {

    }

    @Override
    public void defaultValue() {

    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }
}
