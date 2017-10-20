package jmorfsdk.old.grammeme;

public class OldMorfologyParameters {

    /**
     * Часть речи
     */
    public enum Post {
        INDETERMINATELY,
        NOUN,
        ADJF,
        ADJS,
        COMP,
        VERB,
        INFN,
        PRTF,
        PRTS,
        GRND,
        NUMR,
        ADVB,
        NPRO,
        PRED,
        PREP,
        CONJ,
        PRCL,
        INTJ
    }

    /**
     * Одушевленность
     */
    public enum ANim {
        INDETERMINATELY,
        ANIM,
        INAN
    }

    /**
     * Род
     */
    public enum GNdr {
        INDETERMINATELY,
        MASC,
        FEMN,
        NEUT,
        MS_F
    }

    public enum NMbr {
        INDETERMINATELY,
        SING,
        PLUR
    }

    public enum CAse {
        INDETERMINATELY,
        NOMN,
        GENT,
        DATV,
        ACCS,
        ABLT,
        LOCT,
        VOCT,
        GEN1,
        GEN2,
        ACC2,
        LOC1,
        LOC2
    }

    public enum ASpc {
        INDETERMINATELY,
        PERF,
        IMPF
    }

    public enum TRns {
        INDETERMINATELY,
        TRAN,
        INTR
    }

    public enum PErs {
        INDETERMINATELY,
        PER1,
        PER2,
        PER3
    }

    public enum TEns {
        INDETERMINATELY,
        PRES,
        PAST,
        FUTR
    }

    public enum MOod {
        INDETERMINATELY,
        INDC,
        IMPR
    }

    public enum INvl {
        INDETERMINATELY,
        INCL,
        EXCL
    }

    public enum VOic {
        INDETERMINATELY,
        ACTV,
        PSSV
    }

    public enum Others {
        INDETERMINATELY,
        SGTM,
        PLTM,
        FIXD,
        ABBR,
        NAME,
        SURN,
        PATR,
        GEOX,
        ORGN,
        TRAD,
        SUBX,
        SUPR,
        QUAL,
        APRO,
        ANUM,
        POSS,
        V_EY,
        V_OY,
        CMP2,
        V_EJ,
        //--    
        IMPE,
        IMPX,
        MULT,
        REFL,
        //--
        INFR,
        SLNG,
        ARCH,
        LITR,
        ERRO,
        DIST,
        QUES,
        DMNS,
        PRNT,
        V_BE,
        V_EN,
        V_IE,
        V_BI,
        FIMP,
        PRDX,
        COUN,
        COLL,
        V_SH,
        AF_P,
        INMX,
        VPRE,
        ANPH,
        INIT,
        ADJX
    }

    public static Enum getEnum(String str) {

        for (Post en : Post.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }
        
        for (ANim en : ANim.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }

        for (GNdr en : GNdr.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }

        for (NMbr en : NMbr.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }
        
        for (CAse en : CAse.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }

        for (ASpc en : ASpc.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }

        for (TRns en : TRns.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }

        for (PErs en : PErs.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }

        for (TEns en : TEns.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }

        for (MOod en : MOod.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }

        for (INvl en : INvl.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }

        for (VOic en : VOic.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }
        
        for (Others en : Others.values()) {
            if(en.name().equals(str)){
                return en;
            }
        }
        
        return null;
    }
}
