
package jmorfsdk.grammeme;

/**
 *
 * @author Alex Porechny alex.porechny@mail.ru
 */
public interface MorfologyParameters {
    
    public interface Group{
        
        /**
         * одушевлённое
         */
        public static final long ANIM = 0x2L;
        /**
         * неодушевлённое
         */
        public static final long INAN = 0x3L;

        /**
         * общий род (м/ж)
         */
        public static final long MS_F = 0x4L << 2;
        /**
         * мужской род
         */
        public static final long MASC = 0x5L << 2;
        /**
         * женский род
         */
        public static final long FEMN = 0x6L << 2;
        /**
         * средний род
         */
        public static final long NEUT = 0x7L << 2;

        /**
         * единственное число
         */
        public static final long SING = 0x2L << 5;
        /**
         * множественное число
         */
        public static final long PLUR = 0x3L << 5;

        /**
         * именительный падеж
         */
        public static final long NOMN = 0x1L << 7;
        /**
         * родительный падеж, для обобщения - 1*10
         */
        public static final long GENT = 0x2L << 7;
        /**
         * первый родительный падеж
         */
        public static final long GEN1 = 0xAL << 7;
        /**
         * второй родительный (частичный) падеж
         */
        public static final long GEN2 = 0xEL << 7;
        /**
         * дательный падеж
         */
        public static final long DATV = 0x3L << 7;
        /**
         * винительный падеж, для обощения 1*00
         */
        public static final long ACCS = 0x8L << 7;
        /**
         * второй винительный падеж
         */
        public static final long ACC2 = 0xCL << 7;
        /**
         * творительный падеж
         */
        public static final long ABLT = 0x5L << 7;
         /**
          * предложный падеж, для обобщения - 1*11
          */
        public static final long LOCT = 0x7L << 7;
        /**
         * первый предложный падеж
         */
        public static final long LOC1 = 0xBL << 7;
        /**
         * второй предложный (местный) падеж
         */
        public static final long LOC2 = 0xFL << 7;
        /**
         * звательный падеж
         */
        public static final long VOCT = 0x9L << 7;

        /**
         * совершенный вид
         */
        public static final long PERF = 0x2L << 11;
        /**
         * несовершенный вид
         */
        public static final long IMPF = 0x3L << 11;

        /**
         * переходный
         */
        public static final long TRAN = 0x2L << 13;
        /**
         * непереходный
         */
        public static final long INTR = 0x3L << 13;

        /**
         * 1 лицо
         */
        public static final long PER1 = 0x1L << 15;
        /**
         * 2 лицо
         */
        public static final long PER2 = 0x2L << 15;
        /**
         * 3 лицо
         */
        public static final long PER3 = 0x3L << 15;

        /**
         * настоящее время
         */
        public static final long PRES = 0x1L << 17;
        /**
         * прошедшее время
         */
        public static final long PAST = 0x2L << 17;
        /**
         * будущее время
         */
        public static final long FUTR = 0x3L << 17;

        /**
         * изъявительное наклонение
         */
        public static final long INDC = 0x2L << 19;
        /**
         * повелительное наклонение
         */
        public static final long IMPR = 0x3L << 19;

        /**
         * говорящий включён (идем, идемте)
         */
        public static final long INCL = 0x2L << 21;
        /**
         * говорящий не включён в действие (иди, идите)
         */
        public static final long EXCL = 0x3L << 21;

        /**
         * действительный залог
         */
        public static final long ACTV = 0x2L << 23;
        /**
         * страдательный залог
         */
        public static final long PSSV = 0x3L << 23;

        /**
         * аббревиатура
         */
        public static final long ABBR = 0x1L << 25;
        /**
         * имя
         */
        public static final long NAME = 0x4L << 25;
        /**
         * фамилия
         */
        public static final long SURN = 0x5L << 25;
        /**
         * отчество
         */
        public static final long PARN = 0x6L << 25;
        /**
         * Инициал
         */
        public static final long INIT = 0x7L << 25;

        /**
         * местоименное
         */
        public static final long APRO = 0x1L << 28;
        /**
         * порядковое
         */
        public static final long ANUM = 0x2L << 28;
        /**
         * притяжательное
         */
        public static final long POSS = 0x3L << 28;

        /**
         * форма на _ье
         */
        public static final long V_BE = 0x1L << 30;
        /**
         * форма на _ие; отчество через _ие_
         */
        public static final long V_IE = 0x2L << 30;
        /**
         * форма на _ьи
         */
        public static final long V_BI = 0x3L << 30;
    }
    
    public interface Alone {
        public static final byte SHIFTBIT = 31;

        public static final long SGTM = 0x1L << SHIFTBIT;
        public static final long PLTM = 0x1L << SHIFTBIT << 1;
        public static final long V_EN = 0x1L << SHIFTBIT << 2;
        public static final long IMPE = 0x1L << SHIFTBIT << 3;
        public static final long IMPX = 0x1L << SHIFTBIT << 4;
        public static final long MULT = 0x1L << SHIFTBIT << 5;
        public static final long REFL = 0x1L << SHIFTBIT << 6;
        public static final long FIXD = 0x1L << SHIFTBIT << 7;
        public static final long GEOX = 0x1L << SHIFTBIT << 8;
        public static final long ORGN = 0x1L << SHIFTBIT << 9;
        public static final long TRAD = 0x1L << SHIFTBIT << 10;
        public static final long SUBX = 0x1L << SHIFTBIT << 11;
        public static final long SUPR = 0x1L << SHIFTBIT << 12;
        public static final long QUAL = 0x1L << SHIFTBIT << 13;
        public static final long V_EY = 0x1L << SHIFTBIT << 14;
        public static final long V_OY = 0x1L << SHIFTBIT << 15;
        public static final long CMP2 = 0x1L << SHIFTBIT << 16;
        public static final long V_EJ = 0x1L << SHIFTBIT << 17;
        //--    
        public static final long LITR = 0x1L << SHIFTBIT << 18;
        public static final long ERRO = 0x1L << SHIFTBIT << 19;
        public static final long DIST = 0x1L << SHIFTBIT << 20;
        public static final long QUES = 0x1L << SHIFTBIT << 21;
        public static final long DMNS = 0x1L << SHIFTBIT << 22;
        public static final long PRNT = 0x1L << SHIFTBIT << 23;
        public static final long PRDX = 0x1L << SHIFTBIT << 24;
        public static final long COUN = 0x1L << SHIFTBIT << 25;
        public static final long AF_P = 0x1L << SHIFTBIT << 26;
        public static final long INMX = 0x1L << SHIFTBIT << 27;
        public static final long VPRE = 0x1L << SHIFTBIT << 28;
        public static final long ADJX = 0x1L << SHIFTBIT << 29;
        /**
         * разговорное
         */
        public static final long INFR = 0x1L << SHIFTBIT << 30;
        /**
         * жаргонное
         */
        public static final long SLNG = 0x1L << SHIFTBIT << 31;
        /**
         * устаревшее
         */
        public static final long ARCH = 0x1L << SHIFTBIT << 32;
    }
    
    public interface Post {
        public static final byte NOUN = 0x11;

        public static final byte ADJF = 0x12;
        public static final byte ADJS = 0x17;

        public static final byte VERB = 0x14;
        public static final byte INFN = 0x17;

        public static final byte PRTF = 0x16;
        public static final byte PRTS = 0x17;

        public static final byte GRND = 0x19;
        public static final byte FIMP = 0x1A;
        public static final byte V_SH = 0x1B;

        public static final byte NUMR = 0x1C;
        public static final byte COLL = 0x1D;

        public static final byte NPRO = 0x1E;
        public static final byte ANPH = 0x1F;

        public static final byte ADVB = 0x9;
        public static final byte COMP = 0xA;
        public static final byte PRED = 0xB;
        public static final byte PREP = 0xC;
        public static final byte CONJ = 0xD;
        public static final byte PRCL = 0xE;
        public static final byte INTJ = 0xF;
    }
}
