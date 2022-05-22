package com.crepuscule.jstu.homework5.jsonClass;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class singleWordClass {
    public String input;
    public Blng blng_sents_part;
    public class Blng {
        @SerializedName("trs-classify")
        public List<Trs> trs;
        public class Trs {
            public String proportion;
            public String tr;
        }
    }
}
