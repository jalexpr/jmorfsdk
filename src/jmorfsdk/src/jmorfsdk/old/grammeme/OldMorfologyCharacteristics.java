package jmorfsdk.old.grammeme;

import jmorfsdk.old.grammeme.OldMorfologyParameters.*;
import java.util.*;

public class OldMorfologyCharacteristics {

    private ANim odush = ANim.INDETERMINATELY;
    private GNdr rod = GNdr.INDETERMINATELY;
    private NMbr nMbr = NMbr.INDETERMINATELY;
    private CAse cAse = CAse.INDETERMINATELY;
    private ASpc aSpc = ASpc.INDETERMINATELY;
    private TRns tRns = TRns.INDETERMINATELY;
    private PErs pErs = PErs.INDETERMINATELY; 
    private TEns tEns = TEns.INDETERMINATELY;
    private MOod mOod = MOod.INDETERMINATELY;
    private INvl iNvl = INvl.INDETERMINATELY;
    private VOic vOic = VOic.INDETERMINATELY;
    private final HashSet<Others> others = new HashSet<>();

    /**
     * @param characteristic - любая существующая характеристика
     * Кидаем любую характеристика, а она сама добавляется
     * @return 
     */
    public boolean addMorfCharacteristics(Enum characteristic) {

        if(characteristic == null) {
            return false;
        } else {
            switch (characteristic.getDeclaringClass().getSimpleName()) {
                case "ANim":
                    odush = (ANim) characteristic;
                    break;
                case "GNdr":
                    rod = (GNdr) characteristic;
                    break;
                case "NMbr":
                    nMbr = (NMbr) characteristic;
                    break;
                case "CAse":
                    cAse = (CAse) characteristic;
                    break;
                case "ASpc":
                    aSpc = (ASpc) characteristic;
                    break;
                case "TRns":
                    tRns = (TRns) characteristic;
                    break;
                case "PErs":
                    pErs = (PErs) characteristic;
                    break;
                case "TEns":
                    tEns = (TEns) characteristic;
                    break;
                case "MOod":
                    mOod = (MOod) characteristic;
                    break;
                case "INvl":
                    iNvl = (INvl) characteristic;
                    break;
                case "VOic":
                    vOic = (VOic) characteristic;
                    break;
                case "Others":
                    others.add((Others) characteristic);
                    break;
                default:
                    System.err.println("Попытка добавить неверную характеристику: " + characteristic);
            }
            return true;
        }
    }

    /**
     * @return the others
     */
    public HashSet<Others> getOthers() {
        return others;
    }

    /**
     * @param strOthers
     * пытаемся найти конкретную характеристику
     * @return true - если характеристика есть, иначе false
     */
    public boolean getOthers(String strOthers) {
        try {
            return others.contains(Others.valueOf(strOthers));
        } catch (java.lang.IllegalArgumentException e) {
            System.err.println("Попытка найти неверную характеристику:" + strOthers);
            return false;
        }
    }

    /**
     * @return the odush
     */
    public ANim getOdush() {
        return odush;
    }

    /**
     * @return the rod
     */
    public GNdr getRod() {
        return rod;
    }

    /**
     * @return the nMbr
     */
    public NMbr getNMbr() {
        return nMbr;
    }

    /**
     * @return the cAse
     */
    public CAse getCAse() {
        return cAse;
    }

    /**
     * @return the aSpc
     */
    public ASpc getASpc() {
        return aSpc;
    }

    /**
     * @return the tEns
     */
    public TEns getTEns() {
        return tEns;
    }

    /**
     * @return the mOod
     */
    public MOod getMOod() {
        return mOod;
    }

    /**
     * @return the iNvl
     */
    public INvl getINvl() {
        return iNvl;
    }

    /**
     * @return the vOic
     */
    public VOic getVOic() {
        return vOic;
    }

    /**
     * @return the tRns
     */
    public TRns getTRns() {
        return tRns;
    }

    /**
     * @return the pErs
     */
    public PErs getPErs() {
        return pErs;
    }
}
