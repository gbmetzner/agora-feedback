package com.agora.domain.feedback.common;

import io.hypersistence.tsid.TSID;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.function.Supplier;

@UtilityClass
public class IdGenerator {

    private static final TSID.Factory factory;

   static {
       int worker = 1;  // max: 2^5-1 = 31
       int process = 1; // max: 2^5-1 = 31
       int node = (worker << 5 | process); // max: 2^10-1 = 1023

       var customEpoch = Instant.parse("2025-01-01T00:00:00.000Z");

       // a factory that returns TSIDs similar to Discord Snowflakes
       factory = TSID.Factory.builder()
               .withCustomEpoch(customEpoch)
               .withNode(node)
               .build();
   }

   public Long generateId() {
       return generateTSID().toLong();
   }

   public String generateIdAsString() {
       return generateTSID().toString();
   }

   public TSID generateTSID() {
       return factory.generate();
   }

   public String toString(Long id) {
       return TSID.from(id).toString();
   }

   public Long toLong(String id) {
       return TSID.from(id).toLong();
   }

    public static class CustomTsidSupplier implements Supplier<TSID.Factory> {
        @Override
        public TSID.Factory get() {
            return factory;
        }
    }

}
