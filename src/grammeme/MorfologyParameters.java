
package grammeme;

/**
 *
 * @author Alex Porechny alex.porechny@mail.ru
 */
public interface MorfologyParameters {
    
    // Одушевленность
    public static long ANIM = 0x2;
    public static long INAN = 0x3;
    
    // Род
    public static long MASC = 0x5 << 2;
    public static long FEMN = 0x6 << 2;
    public static long NEUT = 0x7 << 2;
    public static long MS_F = 0x8 << 2;
    
     // число
    public static long SING = 0x2 << 5;
    public static long PLUR = 0x3 << 5;

    //Падеж
    //именительный  -   1*01 
    //родителельный -   1*10
    //винительный   -   1*00
    //предложный    -   1*11
    public static long NOMN = 0x1 << 7;
    public static long GENT = 0x2 << 7;
    public static long DATV = 0x3 << 7;
    public static long ACCS = 0x4 << 7;
    public static long ABLT = 0x5 << 7;
    public static long LOCT = 0x7 << 7;
    public static long VOCT = 0x9 << 7;
    public static long GEN1 = 0xA << 7;
    public static long GEN2 = 0xE << 7;
    public static long ACC2 = 0x8 << 7;
    public static long LOC1 = 0xB << 7;
    public static long LOC2 = 0xF << 7;
        
    // категория вида
    public static long PERF = 0x2 << 11;
    public static long IMPF = 0x3 << 11;

    // категория переходности
    public static long TRAN = 0x2 << 13;
    public static long INTR = 0x3 << 13;

    // категория лица
    public static long PER1 = 0x2 << 15;
    public static long PER2 = 0x3 << 15;
    public static long PER3 = 0x3 << 15;

    // категория времени
    public static long PRES = 0x2 << 17;
    public static long PAST = 0x3 << 17;
    public static long FUTR = 0x3 << 17;

    // категория наклонения
    public static long INDC = 0x2 << 19;
    public static long IMPR = 0x3 << 19;

    // категория совместности
    public static long INCL = 0x2 << 21;
    public static long EXCL = 0x3 << 21;

    // категория залога
    public static long ACTV = 0x2 << 23;
    public static long PSSV = 0x3 << 23;

    // аббревиатура и полное имя
    public static long ABBR = 0x4 << 25;
    public static long NAME = 0x5 << 25;
    public static long SURN = 0x6 << 25;
    public static long PARN = 0x7 << 25;
    public static long INIT = 0x8 << 25;
    
    // категория залога
    public static long APRO = 0x2 << 28;
    public static long ANUM = 0x2 << 28;
    public static long POSS = 0x3 << 28;
    
    // форма на _ье/_ие/ьи
    public static long V_BE = 0x4 << 30;
    public static long V_IE = 0x5 << 30;
    public static long V_BI = 0x6 << 30;
    
    // категория залога
    public static long INFR = 0x2 << 30 << 2;
    public static long SLNG = 0x3 << 30 << 2;
    public static long ARCH = 0x3 << 30 << 2;
    
    public static long SGTM = 0x1;
    public static long PLTM = 0x1 << 1;
    public static long V_EN = 0x1 << 2;
    public static long IMPE = 0x1 << 3;
    public static long IMPX = 0x1 << 4;
    public static long MULT = 0x1 << 5;
    public static long REFL = 0x1 << 6;
    public static long FIXD = 0x1 << 7;
    public static long GEOX = 0x1 << 8;
    public static long ORGN = 0x1 << 9;
    public static long TRAD = 0x1 << 10;
    public static long SUBX = 0x1 << 11;
    public static long SUPR = 0x1 << 12;
    public static long QUAL = 0x1 << 13;
    public static long V_EY = 0x1 << 14;
    public static long V_OY = 0x1 << 15;
    public static long CMP2 = 0x1 << 16;
    public static long V_EJ = 0x1 << 17;
    //--    
    public static long LITR = 0x1 << 18;
    public static long ERRO = 0x1 << 19;
    public static long DIST = 0x1 << 20;
    public static long QUES = 0x1 << 21;
    public static long DMNS = 0x1 << 22;
    public static long PRNT = 0x1 << 23;
    public static long PRDX = 0x1 << 24;
    public static long COUN = 0x1 << 25;
    public static long AF_P = 0x1 << 26;
    public static long INMX = 0x1 << 27;
    public static long VPRE = 0x1 << 28;
    public static long ADJX = 0x1 << 29;

    public static class Post {
        public static byte NOUN = 0x11;

        public static byte ADJF = 0x12;
        public static byte ADJS = 0x17;

        public static byte VERB = 0x14;
        public static byte INFN = 0x17;

        public static byte PRTF = 0x16;
        public static byte PRTS = 0x17;

        public static byte GRND = 0x19;
        public static byte FIMP = 0x1A;
        public static byte V_SH = 0x1B;

        public static byte NUMR = 0x1C;
        public static byte COLL = 0x1D;

        public static byte NPRO = 0x1E;
        public static byte ANPH = 0x1F;

        public static byte ADVB = 0x9;
        public static byte COMP = 0xA;
        public static byte PRED = 0xB;
        public static byte PREP = 0xC;
        public static byte CONJ = 0xD;
        public static byte PRCL = 0xE;
        public static byte INTJ = 0xF;
    }
}
